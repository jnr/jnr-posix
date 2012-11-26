/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

/**
 *
 * @author enebo
 */
public class WindowsStartupInfo extends jnr.ffi.Struct {
    public final Unsigned32  cb = new Unsigned32();
    public final Pointer lpReserved = new Pointer(); //new UTF8String();
    public final Pointer lpDesktop = new Pointer(); //UTF8String();
    public final Pointer lpTitle = new Pointer(); //new UTF8String();
    public final Unsigned32  dwX = new Unsigned32();
    public final Unsigned32  dwY = new Unsigned32();
    public final Unsigned32  dwXSize = new Unsigned32();
    public final Unsigned32  dwYSize = new Unsigned32();
    public final Unsigned32  dwXCountChars = new Unsigned32();
    public final Unsigned32  dwYCountChars = new Unsigned32();
    public final Unsigned32  dwFillAttribute = new Unsigned32();
    public final Unsigned32  dwFlags = new Unsigned32();
    public final Unsigned16   wShowWindow = new Unsigned16();
    public final Unsigned16   cbReserved2 = new Unsigned16();
    public final Pointer lpReserved2 = new Pointer();
    public final Pointer standardInput = new Pointer();
    public final Pointer standardOutput = new Pointer();
    public final Pointer standardError = new Pointer();
  
    public WindowsStartupInfo(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
  
    public void setFlags(int value) {
        dwFlags.set(value);
    }

    public void setStandardInput(HANDLE standardInput) {
        this.standardInput.set(standardInput.toPointer());
    }

    public void setStandardOutput(HANDLE standardOutput) {
        this.standardOutput.set(standardOutput.toPointer());
    }

    public void setStandardError(HANDLE standardError) {
        this.standardError.set(standardError.toPointer());
    }
}
