package org.jruby.ext.posix;

import jnr.ffi.Struct;

abstract public class Timeval extends Struct {
    public Timeval(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
    abstract public void setTime(long[] timeval);
}
