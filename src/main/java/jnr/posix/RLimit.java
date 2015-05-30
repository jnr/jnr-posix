package jnr.posix;

import jnr.ffi.*;

public abstract class RLimit extends Struct {
    protected RLimit(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public abstract void init(long current, long max);
    public abstract long rlimCur();
    public abstract long rlimMax();
}
