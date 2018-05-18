package jnr.posix;

import jnr.ffi.Struct;

abstract public class Flock extends Struct {
    public Flock(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    public abstract void type(short type);
    public abstract void whence(short whence);
    public abstract void start(long start);
    public abstract void len(long len);
    public abstract void pid(int pid);

    public abstract short type();
    public abstract short whence();
    public abstract long start();
    public abstract long len();
    public abstract int pid();
}
