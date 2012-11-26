/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

/**
 *
 * @author enebo
 */
public class WindowsChildRecord {
    private final HANDLE process;
    private final int pid;

    public WindowsChildRecord(HANDLE process, int pid) {
        this.process = process;
        this.pid = pid;
    }
    
    public HANDLE getProcess() {
        return process;
    }
    
    public int getPid() {
        return pid;
    }
}
