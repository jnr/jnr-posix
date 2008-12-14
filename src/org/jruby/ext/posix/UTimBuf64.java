package org.jruby.ext.posix;

import com.sun.jna.Structure;

public class UTimBuf64 extends Structure {
    public long actime;
    public long modtime;

    public UTimBuf64(long atime, long mtime) {
        actime = atime / 1000;
        modtime = mtime / 1000;
    }
}
