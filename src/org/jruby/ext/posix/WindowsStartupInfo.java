/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

/**
 *
 * @author enebo
 */
public class WindowsStartupInfo extends HeapStruct {
    Unsigned32  cb = new Unsigned32();
    Pointer lpReserved = new Pointer(); //new UTF8String();
    Pointer lpDesktop = new Pointer(); //UTF8String();
    Pointer lpTitle = new Pointer(); //new UTF8String();
    Unsigned32  dwX = new Unsigned32();
    Unsigned32  dwY = new Unsigned32();
    Unsigned32  dwXSize = new Unsigned32();
    Unsigned32  dwYSize = new Unsigned32();
    Unsigned32  dwXCountChars = new Unsigned32();
    Unsigned32  dwYCountChars = new Unsigned32();
    Unsigned32  dwFillAttribute = new Unsigned32();
    Unsigned32  dwFlags = new Unsigned32();
    Unsigned16   wShowWindow = new Unsigned16();
    Unsigned16   cbReserved2 = new Unsigned16();
    Pointer lpReserved2 = new Pointer();
    Pointer standardInput = new Pointer();
    Pointer standardOutput = new Pointer();
    Pointer standardError = new Pointer();
  
    public WindowsStartupInfo() {
        super();
    }
  
    public void setFlags(int value) {
        dwFlags.set(value);
    }
            
    public void setStandardInput(com.kenai.jaffl.Pointer standardInput) {
        this.standardInput.set(standardInput);
    }
    
    public void setStandardOutput(com.kenai.jaffl.Pointer standardOutput) {
        this.standardOutput.set(standardOutput);
    }
    
    public void setStandardError(com.kenai.jaffl.Pointer standardError) {
        this.standardError.set(standardError);
    }
}
