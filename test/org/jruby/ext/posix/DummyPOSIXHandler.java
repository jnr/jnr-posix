/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import org.jruby.ext.posix.POSIX.ERRORS;

/**
 *
 * @author wayne
 */
public class DummyPOSIXHandler implements POSIXHandler {

    public void error(ERRORS error, String extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unimplementedError(String methodName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void warn(WARNING_ID id, String message, Object... data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isVerbose() {
        return false;
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
