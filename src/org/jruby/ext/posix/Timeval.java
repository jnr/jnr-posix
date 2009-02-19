package org.jruby.ext.posix;

import com.kenai.jaffl.struct.Struct;

 abstract public class Timeval extends Struct {
    abstract public void setTime(long[] timeval);
}
