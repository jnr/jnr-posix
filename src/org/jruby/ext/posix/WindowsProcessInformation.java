/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

/**
 *
 * @author enebo
 */
public class WindowsProcessInformation extends jnr.ffi.struct.Struct {
    final Pointer hProcess = new Pointer();
    final Pointer hThread = new Pointer();
    final Unsigned32 dwProcessId = new Unsigned32();
    final Unsigned32 dwThreadId = new Unsigned32();

    public WindowsProcessInformation(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    
    public int getThread() {
        return hThread.intValue();
    }
    
    public Pointer getProcess() {
        return hProcess;
    }
    
    public int getPid() {
        return dwProcessId.intValue();
    }
}
