package jnr.posix;

import jnr.ffi.Struct;

abstract public class Timespec extends Struct {
    public Timespec(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    abstract public void sec(long sec);
    abstract public void nsec(long nsec);
    abstract public long sec();
    abstract public long nsec();
}
