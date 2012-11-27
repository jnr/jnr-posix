package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.StdCall;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.types.intptr_t;

import java.nio.ByteBuffer;

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

    public int _wmkdir(@In WString path);
    public int _wrmdir(@In WString path);
    public int _wchmod(@In WString path, int pmode);
    
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
}
