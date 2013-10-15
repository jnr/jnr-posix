package jnr.posix;

/**
 *
 */
abstract class NativePOSIX implements POSIX {
    jnr.ffi.Runtime getRuntime() {
        return jnr.ffi.Runtime.getRuntime(libc());
    }
}
