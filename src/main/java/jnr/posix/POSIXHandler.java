package jnr.posix;

import jnr.constants.platform.Errno;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * The POSIXHandler class allows you do implement the runtime-specific behavior you need in
 * such a way that it is insulated from the implementation of the POSIX library.  Implementing
 * each of the methods in this interface should give you are working POSIX implementation. 
 *
 */
public interface POSIXHandler {
    public enum WARNING_ID {
        DUMMY_VALUE_USED("DUMMY_VALUE_USED");
        
        private String messageID;

        WARNING_ID(String messageID) {
            this.messageID = messageID;
        }
    }
    public void error(Errno error, String extraData);
    public void error(Errno error, String methodName, String extraData);
    
    /**
     * Specify that posix method is unimplemented.  In JRuby we generate an
     * exception with this.
     *
     * @param methodName the POSIX method that failed
     */
    public void unimplementedError(String methodName);
    
    public void warn(WARNING_ID id, String message, Object... data);
    
    /**
     * @return should we provide verbose output about POSIX activities
     */
    public boolean isVerbose();

    /**
     * @return current working directory of your runtime.
     */
    public File getCurrentWorkingDirectory();
    
    /**
     * @return current set of environment variables of your runtime.
     */
    public String[] getEnv();
    
    /**
     * @return your runtime's current input stream
     */
    public InputStream getInputStream();
    
    /**
     * @return your runtime's current output stream
     */
    public PrintStream getOutputStream();
    
    /**
     * Get your runtime's process ID.  This is only intended for non-native POSIX support (e.g.
     * environments where JNA cannot load or security restricted environments).  In JRuby we
     * found a number of packages which would rather have some identity for the runtime than
     * nothing.
     * 
     * Note: If you do not want this to work you impl can just call unimplementedError(String).
     *
     * @return your runtime's process ID
     */
    public int getPID();
    
    /**
     * Get your runtime's current ErrorStream
     *
     * @return your runtime's current error stream
     */
    public PrintStream getErrorStream();
}
