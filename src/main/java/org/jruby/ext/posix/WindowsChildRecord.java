/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

import jnr.ffi.Struct.Pointer;

/**
 *
 * @author enebo
 */
public class WindowsChildRecord {
    private Pointer process;
    int pid;

    public WindowsChildRecord(Pointer process, int pid) {
        this.process = process;
        this.pid = pid;
    }
    
    public Pointer getProcess() {
        return process;
    }
    
    public int getPid() {
        return pid;
    }
}
