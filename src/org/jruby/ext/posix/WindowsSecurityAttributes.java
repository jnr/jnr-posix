package org.jruby.ext.posix;

import com.kenai.jaffl.struct.StructUtil;

/**
 *
 */
public class WindowsSecurityAttributes extends HeapStruct {
    public final Unsigned32 length = new Unsigned32();
    public final Pointer securityDescriptor = new Pointer();
    public final WBOOL inheritHandle = new WBOOL();

    public WindowsSecurityAttributes() {
        super();
        
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
