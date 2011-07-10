package org.jruby.ext.posix;

import jnr.ffi.*;
import jnr.ffi.struct.StructUtil;

/**
 *
 */
public class WindowsSecurityAttributes extends jnr.ffi.struct.Struct {
    public final Unsigned32 length = new Unsigned32();
    public final Pointer securityDescriptor = new Pointer();
    public final WBOOL inheritHandle = new WBOOL();

    public WindowsSecurityAttributes(jnr.ffi.Runtime runtime) {
        super(runtime);
        
        // This seems like the sensible defaults for this.
        length.set(StructUtil.getSize(this));
        inheritHandle.set(true);
    }
    
    public long getLength() {
        return length.get();
    }

    public boolean getInheritHandle() {
        return inheritHandle.get();
    }
}
