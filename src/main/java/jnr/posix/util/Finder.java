package jnr.posix.util;

import java.io.File;
import java.util.*;

import jnr.posix.FileStat;
import jnr.posix.POSIX;

public class Finder {
    private static final Collection<String> EXECUTABLE_EXTENSIONS
            = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(".exe", ".com", ".cmd", ".bat")));

    public static String findFileInPath(POSIX posix, String name, String path) {
        if (path == null || path.length() == 0) path = System.getenv("PATH");
        
        // MRI sets up a bogus path which seems like it would violate security
        // if nothing else since if I don't have /usr/bin in my path but I end
        // up executing it anyways???  Returning original name and hoping for 
        // best.
        if (path == null || path.length() == 0) return name;
        
        return findFileCommon(posix, name, path, true);
    }
    
    public static String findFileCommon(POSIX posix, String name, String path, boolean executableOnly) {
        // No point looking for nothing...
        if (name == null || name.length() == 0) return name;
        
        int length = name.length();
        boolean isAbsolute = false;
        boolean isPath = false;
        int i = 0;
        if (Platform.IS_WINDOWS) {
            if (length > 1 && Character.isLetter(name.charAt(0)) && name.charAt(1) == ':') {
                i = 2;
                isAbsolute = true;
            }

            int extensionIndex = -1;
            char c = name.charAt(i);
            if (i == '/' || i == '\\') {
                i++;
                c = name.charAt(i);
                isAbsolute = true;
            }

            // Is this a partial path and does it contain an explicit 
            // file extension?
            for (; i < length; i++) {
                switch (c) {
                    case '/':
                    case '\\':
                        isPath = true;
                        extensionIndex = -1;
                        break;
                    case '.':
                        extensionIndex = i - 1;
                        break;
                }
                c = name.charAt(i);
            }

            if (extensionIndex >= 0 && !EXECUTABLE_EXTENSIONS.contains(name.substring(extensionIndex).toLowerCase())) {
                extensionIndex = -1;
            }
            
            if (!executableOnly) {
                if (isAbsolute) return name;
            } else if (isPath) {
                if (extensionIndex >= 0) return name;
                
                if (executableOnly) {
                    return addExtension(name);
                } else if (new File(name).exists()) {
                    return name;
                }

                return null;
            }

            String[] paths = path.split(File.pathSeparator);
            for (int p = 0; p < paths.length; p++) {
                String currentPath = paths[p];
                int currentPathLength = currentPath.length();
                
                if (currentPath == null || currentPathLength == 0) continue;
                
                if (currentPath.charAt(0) == '~' && 
                    (currentPathLength == 1 || 
                    (currentPathLength > 1 && (currentPath.charAt(1) == '/' || currentPath.charAt(1) == '\\')))) {
                    String home = System.getenv("HOME");
                    
                    if (home != null) {
                        currentPath = home + (currentPathLength == 1 ? "" : currentPath.substring(1));
                    }
                }
                    
                if (!currentPath.endsWith("/") && !currentPath.endsWith("\\")) {
                    currentPath += "\\";
                }
                
                String filename = currentPath + name;
                if (Platform.IS_WINDOWS) filename = filename.replace('/', '\\');
                
                if (Platform.IS_WINDOWS && executableOnly && extensionIndex == -1) {
                    String extendedFilename = addExtension(filename);
                    
                    if (extendedFilename != null) return extendedFilename;
                    continue;
                }

                try {
                    FileStat stat = posix.stat(filename);
                    if (!executableOnly || (!stat.isDirectory() && stat.isExecutable())) return filename;
                } catch (Throwable t) {}
            }
        }
        
        return null;
    }
    
    public static String addExtension(String path) {
        for (String extension : EXECUTABLE_EXTENSIONS) {
            String newPath = path + extension;
            
            if (new File(newPath).exists()) return newPath;
        }
        
        return null;
    }
}
