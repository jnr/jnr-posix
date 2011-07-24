package jnr.posix;

import jnr.ffi.Struct;

public class FileTime extends Struct {
    public final Unsigned32 dwLowDateTime = new Unsigned32();
    public final Unsigned32 dwHighDateTime = new Unsigned32();
    
    FileTime(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
}
