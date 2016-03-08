/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jnr.posix.util;

import jnr.ffi.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import jnr.posix.POSIX;

/**
 *
 * @author enebo
 */
public class WindowsHelpers {
    static final jnr.ffi.Runtime runtime = jnr.ffi.Runtime.getSystemRuntime();
    static final int WORDSIZE = jnr.ffi.Runtime.getSystemRuntime().addressSize();
    
    public static byte[] toWPath(String path) {
        return toWString(path);
    }

    public static byte[] toWString(String string) {
        if (string == null) return null;
        
        string += (char) 0;

        try {
            return string.getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            return null; // JVM mandates this encoding. Not reached
        }
    }
    
    // FIXME: This does not work and I am unsure if it is because I am violating something
    // CreateProcess requires OR because there are weird requirements in how env needs to be
    // setup for CreateProcess (e.g. =C:=C:/ vars).
    public static Pointer createWideEnv(String[] envp) {
        if (envp == null) return null;
        byte[] marker = {0};        
        int envLength = envp.length;

        // Allocate pointer for env pointer entries plus last \0\0 marker
        Pointer result = Memory.allocateDirect(runtime, WORDSIZE * (envLength + 1));
        
        for (int i = 0; i < envLength; i++) {
            byte[] bytes = toWString(envp[i]);
            Pointer envElement = Memory.allocateDirect(runtime, bytes.length + 1);
            envElement.put(0, bytes, 0, bytes.length);
            envElement.put(bytes.length, marker, 0, marker.length);
            result.putPointer(i * WORDSIZE, envElement);
        }

        Pointer nullMarker = Memory.allocateDirect(runtime, marker.length);
        nullMarker.put(0, marker, 0, marker.length);
        result.putPointer(WORDSIZE * envLength, nullMarker);

        
        return result;
    }
    // Windows cmd strings have various escaping:
    // 1. <>|^ can all be escaped with ^ (e.g. ^<)
    // 2. \s\t must be quoted if not already
    // 3. Any arguments with double quotes must be escaped with a double 
    //    quote around whole cmd    
    private static void joinSingleArgv(StringBuilder buffer, String arg, 
            boolean quote, boolean escape) {
        int backslashCount = 0;
        int start = 0;
        
        if (quote) buffer.append('"');
        
        for (int i = 0; i < arg.length(); i++) {
            char c = arg.charAt(i);
            switch(c) {
                case '\\':
                    backslashCount++;
                    break;
                case '"': {
                    buffer.append(arg.substring(start, i));
                    for (int j = 0; j < backslashCount + 1; j++) {
                        buffer.append('\\');
                    }
                    backslashCount = 0;
                    start = i;
                }
                case '<': case '>': case '|': case '^': {
                    if (escape && !quote) {
                        buffer.append(arg.substring(start, i));
                        buffer.append('^');
                        start = i;
                        break;
                    }
                }
                default: {
                    backslashCount = 0;
                    break;
                }
            }
        }
        buffer.append(arg.substring(start));
        
        if (quote) buffer.append('"');
    }
    
    public static String joinArgv(String command, String[] argv, boolean escape) {
        StringBuilder buffer = new StringBuilder();

        if (command != null) {
            buffer.append(command);
            buffer.append(' ');
        }

        int last_index = argv.length - 1;
        for (int i = 0; i <= last_index; i++) {
            joinSingleArgv(buffer, argv[i], quotable(argv[i]), escape);
            if (i != last_index) buffer.append(' '); // Add space between arguments
        }
        
        return buffer.toString();
    }    
    
    
    public static boolean quotable(String value) {
        if (value == null) return false;
        StringTokenizer toker = new StringTokenizer(value, " \t\"'");
        toker.nextToken(); // We know a string with no delimeters will return self
        return toker.hasMoreTokens();
    }
    
    public static boolean isBatch(String value) {
        if (value == null) return false;
        int length = value.length();
        
        if (length < 5) return false;
        
        String end = value.substring(length - 4);
        
        return end.equalsIgnoreCase(".bat") || end.equalsIgnoreCase(".cmd");
    }
    
    public static String[] processCommandLine(POSIX posix, String command, 
            String program, String path) {
        String shell = null;
        
        if (program != null) {
            String fullPath = Finder.findFileInPath(posix, program, path);
            
            shell = fullPath == null ? program : fullPath.replace('/', '\\');
        } else {
            // Strip off leading whitespace
            command = command.substring(firstNonWhitespaceIndex(command));
            
            // FIXME: Ruby first looks for RUBYSHELL, but this only applies for
            // JRuby (I doubt Jython wants to honor that env).  We need a generic
            // hook for other envs to look for?
            shell = System.getenv("COMSPEC");
            boolean notHandledYet = true;
            if (shell != null) {
                boolean commandDotCom = isCommandDotCom(shell);
                if (hasBuiltinSpecialNeeds(command) || isInternalCommand(command, commandDotCom)) {
                    String quote = commandDotCom ? "\"" : "";
                    command = shell + " /c " + quote + command + quote;
                    notHandledYet = false;
                }
            }
            
            if (notHandledYet) {
                char firstChar = command.charAt(0);
                char quote = firstChar == '"' ? firstChar : (firstChar == '\'' ? firstChar : (char) 0);
                int commandLength = command.length();

                int i = quote == 0 ? 0 : 1;
                
                for(;; i++) {
                    if (i == commandLength) {
                        shell = command;
                        break;
                    }
                    
                    char c = command.charAt(i);
                    
                    if (c == quote) {
                        shell = command.substring(1, i);
                        break;
                    }
                    if (quote != 0) continue;
                    
                    if (Character.isSpaceChar(c) || isFunnyChar(c)) {
                        shell = command.substring(0, i);
                        break;
                    }
                }
                shell = Finder.findFileInPath(posix, shell, path);
                
                if (shell == null) {
                    shell = command.substring(0, i);
                } else {
                    if (!shell.contains(" ")) quote = 0;
                    
                    shell = shell.replace('/', '\\');
                }
            }                
        }
        
        return new String[] { command, shell };
    }

    public static String[] processCommandArgs(POSIX posix, String program, 
            String[] argv, String path) {
           if (program == null || program.length() == 0) program = argv[0];
        
        boolean addSlashC = false;
        boolean isNotBuiltin = false;
        boolean notHandledYet = true;
        String shell = System.getenv("COMSPEC");
        String command = null;
        if (shell != null) {
            boolean commandDotCom = isCommandDotCom(shell);
            if (isInternalCommand(program, commandDotCom)) {
                isNotBuiltin = !commandDotCom;
                program = shell;
                addSlashC = true;
                notHandledYet = false;
            }
        }
        if (notHandledYet) {
            command = Finder.findFileInPath(posix, program, path);
            if (command != null) {
                program = command.replace('/', '\\');
            } else if (program.contains("/")) {
                command = program.replace('/', '\\');
                program = command;
            }
        }
        
        if (addSlashC || isBatch(program)) {
            if (addSlashC) {
                command = program + " /c ";
            } else {
                String[] newArgv = new String[argv.length - 1];
                System.arraycopy(argv, 1, newArgv, 0, argv.length - 1);
                argv = newArgv;
            }

            if (argv.length > 0) {
                command = WindowsHelpers.joinArgv(command, argv, isNotBuiltin);
            }
            program = addSlashC ? shell : null;
        } else {
            command = WindowsHelpers.joinArgv(null, argv, false);
        }
        
        return new String[] { command, program };
    }
    
    private static boolean isFunnyChar(char c) {
        return c == '<' || c == '>' || c == '|' || c == '*' || c == '?' ||
                c == '"';
    }

    private static boolean hasBuiltinSpecialNeeds(String value) {
        int length = value.length();
        char quote = '\0';
        
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\'': case '\"':
                    if (quote == '\0') {
                        quote = c;
                    } else if (quote == c) {
                        quote = '\0';
                    }
                    break;
                case '>': case '<': case '|': case '\n':
                    if (quote != '\0') return true;
                    break;
                case '%':  // %FOO% check
                    if (i + 1 < length) {
                        i += 1;
                        char c2 = value.charAt(i);
                        if (c2 != ' ' && !Character.isLetter(c2)) break;
                        for (int j = i; j < length; j++) {
                            c2 = value.charAt(j);
                            if (c2 != ' ' && !Character.isLetterOrDigit(c2)) break;
                        }
                        if (c2 == '%') return true;
                    }
                    break;
            }
	}
        return false;
    }
    
    private static int firstNonWhitespaceIndex(String value) {
        int length = value.length();
        int i = 0;
        for (; i < length && Character.isSpaceChar(value.charAt(i)); i++) {}
        return i;
    }
    
    public static String escapePath(String path) {
        StringBuilder buf = new StringBuilder();
        
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            
            buf.append(c);
            if (c == '\\') buf.append(c);
        }
        return buf.toString() + "\\\\";
    }    
    
    private final static String COMMAND_DOT_COM = "command.com";
    private final static int CDC_LENGTH = COMMAND_DOT_COM.length();
    private enum InternalType { SHELL, COMMAND, BOTH };
    private static Map<String, InternalType> INTERNAL_COMMANDS = new HashMap<String, InternalType>() {{
        put("assoc", InternalType.COMMAND);
        put("break", InternalType.BOTH);
        put("call", InternalType.BOTH);
        put("cd", InternalType.BOTH);
        put("chcp", InternalType.SHELL);
        put("chdir", InternalType.BOTH);
        put("cls", InternalType.BOTH);
        put("color", InternalType.COMMAND);
        put("copy", InternalType.BOTH);
        put("ctty", InternalType.SHELL);
        put("date", InternalType.BOTH);
        put("del", InternalType.BOTH);
        put("dir", InternalType.BOTH);
        put("echo", InternalType.BOTH);
        put("endlocal", InternalType.COMMAND);
        put("erase", InternalType.BOTH);
        put("exit", InternalType.BOTH);
        put("for", InternalType.BOTH);
        put("ftype", InternalType.COMMAND);
        put("goto", InternalType.BOTH);
        put("if", InternalType.BOTH);
        put("lfnfor", InternalType.SHELL);
        put("lh", InternalType.SHELL);
        put("lock", InternalType.SHELL);
        put("md", InternalType.BOTH);
        put("mkdir", InternalType.BOTH);
        put("move", InternalType.COMMAND);
        put("path", InternalType.BOTH);
        put("pause", InternalType.BOTH);
        put("popd", InternalType.COMMAND);
        put("prompt", InternalType.BOTH);
        put("pushd", InternalType.COMMAND);
        put("rd", InternalType.BOTH);
        put("rem", InternalType.BOTH);
        put("ren", InternalType.BOTH);
        put("rename", InternalType.BOTH);
        put("rmdir", InternalType.BOTH);
        put("set", InternalType.BOTH);
        put("setlocal", InternalType.COMMAND);
        put("shift", InternalType.BOTH);
        put("start", InternalType.COMMAND);
        put("time", InternalType.BOTH);
        put("title", InternalType.COMMAND);
        put("truename", InternalType.SHELL);
        put("type", InternalType.BOTH);
        put("unlock", InternalType.SHELL);
        put("ver", InternalType.BOTH);
        put("verify", InternalType.BOTH);
        put("vol", InternalType.BOTH);
    }};
    
    private static boolean isDirectorySeparator(char value) {
        return value == '/' || value == '\\';
    }    
    private static boolean isCommandDotCom(String command) {
        int length = command.length();
        int i = length - CDC_LENGTH;
        
        return i == 0 || i > 0 && isDirectorySeparator(command.charAt(i - 1)) &&
                command.regionMatches(true, i, COMMAND_DOT_COM, 0, CDC_LENGTH);
    }
    
    private static boolean isInternalCommand(String command, boolean hasCommandDotCom) {
        assert command != null && !Character.isSpaceChar(command.charAt(0)) : "Spaces should have been stripped off already";
        
        int length = command.length();
        
        StringBuilder buf = new StringBuilder();
        int i = 0;
        char c = 0;
        for (; i < length; i++) {
            c = command.charAt(i);
            if (!Character.isLetter(c)) break;
            buf.append(Character.toLowerCase(c));
        }
        
        if (i < length) {
            if (c == '.' && i + 1 < length) i++;

            switch (command.charAt(i)) {
                case '<': case '>': case '|':
                    return true;
                case '\0': case ' ': case '\t': case '\n':
                    break;
                default:
                    return false;
            }
        }

        InternalType kindOf = INTERNAL_COMMANDS.get(buf.toString());
        return kindOf == InternalType.BOTH || 
                (hasCommandDotCom ? kindOf == InternalType.COMMAND : kindOf == InternalType.SHELL);
    }

    public static boolean isDriveLetterPath(String path) {
        return path.length() >= 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':';
    }
        
}
