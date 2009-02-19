package org.jruby.ext.posix;

import com.sun.jna.Structure;

public class UTimBuf64 extends Structure {
    public long actime;
    public long modtime;

    public UTimBuf64(long actime, long modtime) {
        this.actime = actime;
        this.modtime = modtime;
    }
}
