package jnr.posix;

import jnr.ffi.*;

/**
 *
 */
public class WindowsSecurityAttributes extends jnr.ffi.Struct {
    public final Unsigned32 length = new Unsigned32();
    public final Pointer securityDescriptor = new Pointer();
    public final WBOOL inheritHandle = new WBOOL();

    public WindowsSecurityAttributes(jnr.ffi.Runtime runtime) {
        super(runtime);
        
        // This seems like the sensible defaults for this.
        length.set(Struct.size(this));
        inheritHandle.set(true);
    }
    
    public long getLength() {
        return length.get();
    }

    public boolean getInheritHandle() {
        return inheritHandle.get();
    }
}
