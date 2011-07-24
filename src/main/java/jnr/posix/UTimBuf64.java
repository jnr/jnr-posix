package jnr.posix;

import jnr.ffi.Struct;

public final class UTimBuf64 extends Struct {
    public final Signed64 actime = new Signed64();
    public final Signed64 modtime = new Signed64();

    public UTimBuf64(jnr.ffi.Runtime runtime, long actime, long modtime) {
        super(runtime);
        this.actime.set(actime);
        this.modtime.set(modtime);
    }
}
