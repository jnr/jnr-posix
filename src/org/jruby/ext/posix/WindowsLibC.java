package org.jruby.ext.posix;

import com.kenai.jaffl.Pointer;
import com.kenai.jaffl.annotations.In;
import com.kenai.jaffl.annotations.StdCall;

public interface WindowsLibC extends LibC {
    public int _open_osfhandle(int handle, int flags);

    public int _wmkdir(@In byte[] path);

     @StdCall
    public int GetFileType(int handle);

    @StdCall
    public boolean CreateHardLinkW(byte[] oldname, byte[] newName, @In byte[] reserved);

    @StdCall
    int CreateFileW(
            byte[] lpFileName,
            int dwDesiredAccess,
            int dwShareMode,
            Pointer lpSecurityAttributes,
            int dwCreationDisposition,
            int dwFlagsAndAttributes,
            int hTemplateFile
    );

    @StdCall
    boolean SetFileTime(
            int hFile,
            FileTime lpCreationTime,
            FileTime lpLastAccessTime,
            FileTime lpLastWriteTime
    );

    @StdCall
    boolean CloseHandle(int handle);
}
