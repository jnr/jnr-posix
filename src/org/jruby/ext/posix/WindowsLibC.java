package org.jruby.ext.posix;

import com.kenai.jaffl.annotations.StdCall;

public interface WindowsLibC extends LibC {
    public int _open_osfhandle(int handle, int flags);
    public int _utime64(String filename, UTimBuf64 times);
    public int _wmkdir(byte[] path, int mode);
    @StdCall
    public int GetFileType(int handle);
    @StdCall
    public boolean CreateHardLinkA(String oldname, String newName, byte[] reserved);
}
