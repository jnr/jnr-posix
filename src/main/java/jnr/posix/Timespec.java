package jnr.posix;

import jnr.ffi.Struct;

abstract public class Timespec extends Struct {
    public Timespec(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    abstract public void setTime(long[] timespec);
    public abstract void sec(long sec);
    public abstract void nsec(long nsec);
    public abstract long sec();
    public abstract long nsec();
}
