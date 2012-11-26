/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

/**
 *
 * @author enebo
 */
public class WindowsProcessInformation extends jnr.ffi.Struct {
    final Pointer hProcess = new Pointer();
    final Pointer hThread = new Pointer();
    final Unsigned32 dwProcessId = new Unsigned32();
    final Unsigned32 dwThreadId = new Unsigned32();

    public WindowsProcessInformation(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    
    public HANDLE getThread() {
        return new HANDLE(hThread.get());
    }
    
    public HANDLE getProcess() {
        return new HANDLE(hProcess.get());
    }
    
    public int getPid() {
        return dwProcessId.intValue();
    }
}
