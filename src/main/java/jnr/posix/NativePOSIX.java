package jnr.posix;

/**
 *
 */
public abstract class NativePOSIX implements POSIX {


    jnr.ffi.Runtime getRuntime() {
        return jnr.ffi.Runtime.getRuntime(libc());
    }

    public abstract SocketMacros socketMacros();

}
