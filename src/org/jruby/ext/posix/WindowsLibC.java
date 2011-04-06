package org.jruby.ext.posix;

import com.kenai.jaffl.Pointer;
import com.kenai.jaffl.annotations.In;
import com.kenai.jaffl.annotations.StdCall;

public interface WindowsLibC extends LibC {
    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;
    
    public static final int NORMAL_PRIORITY_CLASS = 0x00000020;
    
    public static final int INFINITE = -1;
    
    public int _open_osfhandle(int handle, int flags);

    public int _wmkdir(@In byte[] path);
    
    @StdCall
    public boolean CreateProcessA(String applicationName, 
                                 String commandLine, 
                                 WindowsSecurityAttributes processAttributes,
                                 WindowsSecurityAttributes threadAttributes,
                                 int inheritHandles,
                                 int creationFlags,
                                 Pointer environment,
                                 String currentDirectory,
                                 WindowsStartupInfo startupInfo,
                                 WindowsProcessInformation processInformation);
    
    @StdCall
    public boolean GetExitCodeProcess(int handle, Pointer exitCode);

    @StdCall
    public int GetFileType(int handle);

    @StdCall
    public int GetFileSize(int handle, Pointer outSizeHigh);
    
    @StdCall
    public Pointer GetStdHandle(int stdHandle);

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
    
    @StdCall
    int WaitForSingleObject(int handle, int milliseconds);
}
