package org.jruby.ext.posix;

import jnr.ffi.struct.Struct;

 abstract public class Timeval extends Struct {
    abstract public void setTime(long[] timeval);
}
