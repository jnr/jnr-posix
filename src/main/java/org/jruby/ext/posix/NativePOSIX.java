package org.jruby.ext.posix;

import jnr.ffi.*;

/**
 *
 */
abstract class NativePOSIX implements POSIX {
    private final jnr.ffi.Runtime runtime = jnr.ffi.Runtime.getSystemRuntime();
    jnr.ffi.Runtime getRuntime() {
        return runtime;
    }
}
