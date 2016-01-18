package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.Variable;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.StdCall;
import jnr.ffi.annotations.Transient;
import jnr.ffi.byref.IntByReference;

import java.nio.ByteBuffer;
import jnr.posix.windows.SystemTime;
import jnr.posix.windows.WindowsByHandleFileInformation;
import jnr.posix.windows.WindowsFileInformation;
import jnr.posix.windows.WindowsFindData;

public interface WindowsLibC extends LibC {
    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;

    public static final int NORMAL_PRIORITY_CLASS = 0x00000020;
    public static final int CREATE_UNICODE_ENVIRONMENT = 0x00000400;
    
    public static final int INFINITE = -1;

    public static final int FILE_TYPE_DISK = 0x0001;
    public static final int FILE_TYPE_CHAR = 0x0002;
    public static final int FILE_TYPE_PIPE = 0x0003;
    public static final int FILE_TYPE_REMOTE = 0x8000;
    public static final int FILE_TYPE_UNKNOWN = 0x0000;
    
    public int _open_osfhandle(HANDLE handle, int flags);
    public HANDLE _get_osfhandle(int fd);
    public int _close(int fd);
    public int _getpid();
    int _stat64(CharSequence path, @Out @Transient FileStat stat);

    int _umask(int mask);

    public int _wmkdir(@In WString path);
    public boolean RemoveDirectoryW(@In WString path);
    public int _wchmod(@In WString path, int pmode);
    public int _wchdir(@In WString path);
    public int _wstat64(@In WString path, @Out @Transient FileStat stat);
    public int _wstat64(@In byte[] path, @Out @Transient FileStat stat);
    public int _pipe(int[] fds, int psize, int textmode);
    
    @StdCall
    public boolean CreateProcessW(byte[] applicationName, 
                                 @In @Out ByteBuffer buffer, 
                                 WindowsSecurityAttributes processAttributes,
                                 WindowsSecurityAttributes threadAttributes,
                                 int inheritHandles,
                                 int creationFlags,
                                 @In Pointer envp,
                                 @In byte[] currentDirectory,
                                 WindowsStartupInfo startupInfo,
                                 WindowsProcessInformation processInformation);

    public int FileTimeToSystemTime(@In FileTime fileTime, @Out @Transient SystemTime systemTime);
    public int GetFileAttributesW(@In WString path);
    public int GetFileAttributesExW(@In WString path, @In int infoLevel, @Out @Transient WindowsFileInformation fileInformation);
    public int GetFileAttributesExW(@In byte[] path, @In int infoLevel, @Out @Transient WindowsFileInformation fileInformation);
    public int SetFileAttributesW(@In WString path, int flags);
    public int GetFileInformationByHandle(@In HANDLE handle, @Out @Transient WindowsByHandleFileInformation fileInformation);

    public int FindClose(HANDLE handle);
    public HANDLE FindFirstFileW(@In WString wpath, @Out WindowsFindData findData);
    public HANDLE FindFirstFileW(@In byte[] wpath, @Out WindowsFindData findData);
    
    @StdCall
    public boolean GetExitCodeProcess(HANDLE handle, @Out Pointer exitCode);

    @StdCall
    public boolean GetExitCodeProcess(HANDLE handle, @Out IntByReference exitCode);

    @StdCall
    public int GetFileType(HANDLE handle);

    @StdCall
    public int GetFileSize(HANDLE handle, @Out IntByReference outSizeHigh);
    
    @StdCall
    public HANDLE GetStdHandle(int stdHandle);

    @StdCall
    public boolean CreateHardLinkW(@In WString oldname, @In WString newName, @In WString reserved);

    @StdCall
    HANDLE CreateFileW(
            byte[] lpFileName,
            int dwDesiredAccess,
            int dwShareMode,
            Pointer lpSecurityAttributes,
            int dwCreationDisposition,
            int dwFlagsAndAttributes,
            int hTemplateFile
    );
    
    @StdCall
    boolean SetEnvironmentVariableW(
            @In WString envName,
            @In WString envValue);

    @StdCall
    boolean SetFileTime(
            HANDLE  hFile,
            FileTime lpCreationTime,
            FileTime lpLastAccessTime,
            FileTime lpLastWriteTime
    );

    @StdCall
    boolean CloseHandle(HANDLE handle);
    
    @StdCall
    int WaitForSingleObject(HANDLE handle, int milliseconds);

    Variable<Long> _environ();
}
