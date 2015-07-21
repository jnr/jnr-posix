package jnr.posix;

import jnr.ffi.StructLayout;

public abstract class NativeGroup implements Group {
    protected final jnr.ffi.Runtime runtime;
    protected final StructLayout structLayout;
    protected NativeGroup(jnr.ffi.Runtime runtime, StructLayout structLayout) {
        this.runtime = runtime;
        this.structLayout = structLayout;
    }
}
