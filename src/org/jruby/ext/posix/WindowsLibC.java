package org.jruby.ext.posix;

public interface WindowsLibC extends LibC {
    public int _open_osfhandle(int handle, int flags);
}
