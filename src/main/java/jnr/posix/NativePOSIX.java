package jnr.posix;

import jnr.ffi.Memory;
import jnr.ffi.Pointer;

/**
 *
 */
public abstract class NativePOSIX implements POSIX {


    jnr.ffi.Runtime getRuntime() {
        return jnr.ffi.Runtime.getRuntime(libc());
    }

    public abstract SocketMacros socketMacros();

    public Pointer allocatePosixSpawnFileActions() {
        return Memory.allocateDirect(getRuntime(), 128);
    }

    public Pointer allocatePosixSpawnattr() {
        return Memory.allocateDirect(getRuntime(), 128);
    }

}
