package jnr.posix;

import jnr.ffi.*;

/**
 *
 */
abstract class NativePOSIX implements POSIX {
    private final jnr.ffi.Runtime runtime = jnr.ffi.Runtime.getRuntime(libc());
    jnr.ffi.Runtime getRuntime() {
        return runtime;
    }
}
