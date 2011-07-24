package org.jruby.ext.posix;

import jnr.ffi.Struct;


public abstract class NativePasswd extends Struct implements Passwd {
    NativePasswd(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    NativePasswd(jnr.ffi.Pointer pointer) {
        super(pointer.getRuntime());
        useMemory(pointer);
    }
}
