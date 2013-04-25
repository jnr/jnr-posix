package jnr.posix.util;

import jnr.constants.platform.Errno;
import jnr.posix.POSIXHandler;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A POSIXHandler with reasonable default behavior.
 */
public class DefaultPOSIXHandler implements POSIXHandler {
    public void error(Errno error, String extraData) {
        throw new RuntimeException("native error " + error.description() + " " + extraData);
    }

    public void error(Errno error, String methodName, String extraData) {
        throw new RuntimeException("native error calling " + methodName + ": " + error.description() + " " + extraData);
    }
    
    public void unimplementedError(String methodName) {
        throw new IllegalStateException(methodName + " is not implemented in jnr-posix");
    }

    public void warn(WARNING_ID id, String message, Object... data) {
        String msg;
        try {
            msg = String.format(message, data);
        }
        catch (IllegalFormatException e) {
            msg = message + " " + Arrays.toString(data);
        }
        Logger.getLogger("jnr-posix").log(Level.WARNING, msg);
    }

    public boolean isVerbose() {
        return false;
    }

    public File getCurrentWorkingDirectory() {
        return new File(".");
    }

    public String[] getEnv() {
        String[] envp = new String[System.getenv().size()];
        int i = 0;
        for (Map.Entry<String, String> pair : System.getenv().entrySet()) {
            envp[i++] = new StringBuilder(pair.getKey()).append("=").append(pair.getValue()).toString();
        }
        return envp;
    }

    public InputStream getInputStream() {
        return System.in;
    }

    public PrintStream getOutputStream() {
        return System.out;
    }

    public int getPID() {
        return 0;
    }

    public PrintStream getErrorStream() {
        return System.err;
    }
}
