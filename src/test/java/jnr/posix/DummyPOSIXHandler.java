/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

import jnr.constants.platform.Errno;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

/**
 *
 * @author wayne
 */
public class DummyPOSIXHandler implements POSIXHandler {

    public void error(Errno error, String extraData) {
        throw new UnsupportedOperationException("error: " + error);
    }
    
    public void error(Errno error, String methodName, String extraData) {
        throw new UnsupportedOperationException("error: " + error);
    }    

    public void unimplementedError(String methodName) {
        throw new UnsupportedOperationException("unimplemented method: " + methodName);
    }

    public void warn(WARNING_ID id, String message, Object... data) {
        throw new UnsupportedOperationException("warning: " + id);
    }

    public boolean isVerbose() {
        return true;
    }

    public File getCurrentWorkingDirectory() {
        return new File("/tmp");
    }

    public String[] getEnv() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PrintStream getOutputStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PrintStream getErrorStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
