package org.jruby.ext.posix;

public interface WindowsLibC extends LibC {
    public int _open_osfhandle(int handle, int flags);
    public int _utime64(String filename, UTimBuf64 times);
}
