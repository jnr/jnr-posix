/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

/**
 *
 * @author enebo
 */
public class WindowsProcessInformation extends HeapStruct {
    Pointer hProcess = new Pointer();
    Pointer hThread = new Pointer();
    Unsigned32 dwProcessId = new Unsigned32();
    Unsigned32 dwThreadId = new Unsigned32();
    
    public WindowsProcessInformation() {
        super();
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
