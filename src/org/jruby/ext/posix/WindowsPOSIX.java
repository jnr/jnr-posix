package org.jruby.ext.posix;

import com.kenai.constantine.platform.Errno;
import static com.kenai.constantine.platform.Errno.*;
import static com.kenai.constantine.platform.windows.LastError.*;

import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.Map;

final class WindowsPOSIX extends BaseNativePOSIX {
    private final static int FILE_TYPE_CHAR = 0x0002;

    private final static Map<Integer, Errno> errorToErrnoMapper
            = new HashMap<Integer, Errno>();

    static {
        errorToErrnoMapper.put(ERROR_INVALID_FUNCTION.value(), EINVAL);
        errorToErrnoMapper.put(ERROR_FILE_NOT_FOUND.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_PATH_NOT_FOUND.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_TOO_MANY_OPEN_FILES.value(), EMFILE);
        errorToErrnoMapper.put(ERROR_ACCESS_DENIED.value(), EACCES);
        errorToErrnoMapper.put(ERROR_INVALID_HANDLE.value(), EBADF );
        errorToErrnoMapper.put(ERROR_ARENA_TRASHED.value(), ENOMEM);
        errorToErrnoMapper.put(ERROR_NOT_ENOUGH_MEMORY.value(), ENOMEM);
        errorToErrnoMapper.put(ERROR_INVALID_BLOCK.value(), ENOMEM);
        errorToErrnoMapper.put(ERROR_BAD_ENVIRONMENT.value(), E2BIG );
        errorToErrnoMapper.put(ERROR_BAD_FORMAT.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INVALID_ACCESS.value(), EINVAL);
        errorToErrnoMapper.put(ERROR_INVALID_DATA.value(), EINVAL);
        errorToErrnoMapper.put(ERROR_INVALID_DRIVE.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_CURRENT_DIRECTORY.value(), EACCES);
        errorToErrnoMapper.put(ERROR_NOT_SAME_DEVICE.value(), EXDEV );
        errorToErrnoMapper.put(ERROR_NO_MORE_FILES.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_WRITE_PROTECT.value(), EROFS );
        errorToErrnoMapper.put(ERROR_BAD_UNIT.value(), ENODEV);
        errorToErrnoMapper.put(ERROR_NOT_READY.value(), ENXIO );
        errorToErrnoMapper.put(ERROR_BAD_COMMAND.value(), EACCES);
        errorToErrnoMapper.put(ERROR_CRC.value(), EACCES);
        errorToErrnoMapper.put(ERROR_BAD_LENGTH.value(), EACCES);
        errorToErrnoMapper.put(ERROR_SEEK.value(), EIO);
        errorToErrnoMapper.put(ERROR_NOT_DOS_DISK.value(), EACCES);
        errorToErrnoMapper.put(ERROR_SECTOR_NOT_FOUND.value(), EACCES);
        errorToErrnoMapper.put(ERROR_OUT_OF_PAPER.value(), EACCES);
        errorToErrnoMapper.put(ERROR_WRITE_FAULT.value(), EIO);
        errorToErrnoMapper.put(ERROR_READ_FAULT.value(), EIO);
        errorToErrnoMapper.put(ERROR_GEN_FAILURE.value(), EACCES);
        errorToErrnoMapper.put(ERROR_LOCK_VIOLATION.value(), EACCES);
        errorToErrnoMapper.put(ERROR_SHARING_VIOLATION.value(), EACCES);
        errorToErrnoMapper.put(ERROR_WRONG_DISK.value(), EACCES);
        errorToErrnoMapper.put(ERROR_SHARING_BUFFER_EXCEEDED.value(), EACCES);
        errorToErrnoMapper.put(ERROR_BAD_NETPATH.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_NETWORK_ACCESS_DENIED.value(), EACCES);
        errorToErrnoMapper.put(ERROR_BAD_NET_NAME.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_FILE_EXISTS.value(), EEXIST);
        errorToErrnoMapper.put(ERROR_CANNOT_MAKE.value(), EACCES);
        errorToErrnoMapper.put(ERROR_FAIL_I24.value(), EACCES);
        errorToErrnoMapper.put(ERROR_INVALID_PARAMETER.value(), EINVAL);
        errorToErrnoMapper.put(ERROR_NO_PROC_SLOTS.value(), EAGAIN);
        errorToErrnoMapper.put(ERROR_DRIVE_LOCKED.value(), EACCES);
        errorToErrnoMapper.put(ERROR_BROKEN_PIPE.value(), EPIPE);
        errorToErrnoMapper.put(ERROR_DISK_FULL.value(), ENOSPC);
        errorToErrnoMapper.put(ERROR_INVALID_TARGET_HANDLE.value(), EBADF);
        errorToErrnoMapper.put(ERROR_INVALID_HANDLE.value(), EINVAL);
        errorToErrnoMapper.put(ERROR_WAIT_NO_CHILDREN.value(), ECHILD);
        errorToErrnoMapper.put(ERROR_CHILD_NOT_COMPLETE.value(), ECHILD);
        errorToErrnoMapper.put(ERROR_DIRECT_ACCESS_HANDLE.value(), EBADF);
        errorToErrnoMapper.put(ERROR_NEGATIVE_SEEK.value(), EINVAL);
        errorToErrnoMapper.put(ERROR_SEEK_ON_DEVICE.value(), EACCES);
        errorToErrnoMapper.put(ERROR_DIR_NOT_EMPTY.value(), ENOTEMPTY);
        errorToErrnoMapper.put(ERROR_DIRECTORY.value(), ENOTDIR);
        errorToErrnoMapper.put(ERROR_NOT_LOCKED.value(), EACCES);
        errorToErrnoMapper.put(ERROR_BAD_PATHNAME.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_MAX_THRDS_REACHED.value(), EAGAIN);
        errorToErrnoMapper.put(ERROR_LOCK_FAILED.value(), EACCES);
        errorToErrnoMapper.put(ERROR_ALREADY_EXISTS.value(), EEXIST);
        errorToErrnoMapper.put(ERROR_INVALID_STARTING_CODESEG.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INVALID_STACKSEG.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INVALID_MODULETYPE.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INVALID_EXE_SIGNATURE.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_EXE_MARKED_INVALID.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_BAD_EXE_FORMAT.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_ITERATED_DATA_EXCEEDS_64k.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INVALID_MINALLOCSIZE.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_DYNLINK_FROM_INVALID_RING.value(),ENOEXEC);
        errorToErrnoMapper.put(ERROR_IOPL_NOT_ENABLED.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INVALID_SEGDPL.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_AUTODATASEG_EXCEEDS_64k.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_RING2SEG_MUST_BE_MOVABLE.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_RELOC_CHAIN_XEEDS_SEGLIM.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_INFLOOP_IN_RELOC_CHAIN.value(), ENOEXEC);
        errorToErrnoMapper.put(ERROR_FILENAME_EXCED_RANGE.value(), ENOENT);
        errorToErrnoMapper.put(ERROR_NESTING_NOT_ALLOWED.value(), EAGAIN);
        // ERROR_PIPE_LOCAL (in MRI)
        errorToErrnoMapper.put(229, EPIPE);
        errorToErrnoMapper.put(ERROR_BAD_PIPE.value(), EPIPE);
        errorToErrnoMapper.put(ERROR_PIPE_BUSY.value(), EAGAIN);
        errorToErrnoMapper.put(ERROR_NO_DATA.value(), EPIPE);
        errorToErrnoMapper.put(ERROR_PIPE_NOT_CONNECTED.value(), EPIPE);
        errorToErrnoMapper.put(ERROR_OPERATION_ABORTED.value(), EINTR);
        errorToErrnoMapper.put(ERROR_NOT_ENOUGH_QUOTA.value(), ENOMEM);
        errorToErrnoMapper.put(ERROR_MOD_NOT_FOUND.value(), ENOENT);
        errorToErrnoMapper.put(WSAENAMETOOLONG.value(), ENAMETOOLONG);
        errorToErrnoMapper.put(WSAENOTEMPTY.value(), ENOTEMPTY);
        errorToErrnoMapper.put(WSAEINTR.value(), EINTR);
        errorToErrnoMapper.put(WSAEBADF.value(), EBADF);
        errorToErrnoMapper.put(WSAEACCES.value(), EACCES);
        errorToErrnoMapper.put(WSAEFAULT.value(), EFAULT);
        errorToErrnoMapper.put(WSAEINVAL.value(), EINVAL);
        errorToErrnoMapper.put(WSAEMFILE.value(), EMFILE);
    }

    WindowsPOSIX(String libraryName, LibCProvider libc, POSIXHandler handler) {
        super(libraryName, libc, handler);
    }
    
    @Override
    public BaseHeapFileStat allocateStat() {
        return new WindowsFileStat(this);
    }

    @Override
    public int kill(int pid, int signal) {
        handler.unimplementedError("kill");

        return -1;
    }

    @Override
    public int chown(String filename, int user, int group) {
        return 0;
    }

    @Override
    public int getegid() {
        handler.unimplementedError("egid");

        return -1;
    }

    @Override
    public int setegid(int egid) {
        handler.unimplementedError("setegid");

        return -1;
    }

    @Override
    public int geteuid() {
        return 0;
    }

    @Override
    public int seteuid(int euid) {
        handler.unimplementedError("seteuid");

        return -1;
    }

    @Override
    public int getuid() {
        return 0;
    }

    @Override
    public int setuid(int uid) {
        handler.unimplementedError("setuid");

        return -1;
    }

    @Override
    public int getgid() {
        handler.unimplementedError("getgid");

        return -1;
    }

    @Override
    public int setgid(int gid) {
        handler.unimplementedError("setgid");

        return -1;
    }

    @Override
    public int getpgid(int pid) {
        handler.unimplementedError("getpgid");

        return -1;
    }
    
    @Override
    public int getpgid() {
        handler.unimplementedError("getpgid");

        return -1;
    }

    @Override
    public int setpgid(int pid, int pgid) {
        handler.unimplementedError("setpgid");

        return -1;
    }
    
    @Override
    public int getpriority(int which, int who) {
        handler.unimplementedError("getpriority");

        return -1;
    }
    
    @Override
    public int setpriority(int which, int who, int prio) {
        handler.unimplementedError("setpriority");

        return -1;
    }

    @Override
    public int getppid() {
        return 0;
    }
    
    @Override
    public int lchmod(String filename, int mode) {
        handler.unimplementedError("lchmod");
        
        return -1;
    }
    
    @Override
    public int lchown(String filename, int user, int group) {
        handler.unimplementedError("lchown");
        
        return -1;
    }
    
    @Override
    public FileStat lstat(String path) {
        return stat(path);
    }

    @Override
    public String readlink(String oldpath) {
        handler.unimplementedError("readlink");

        return null;
    }

    @Override
    public int utimes(String path, long[] atimeval, long[] mtimeval) {
        UTimBuf64 times = null;
        if (atimeval != null && mtimeval != null) {
            times = new UTimBuf64(atimeval[0], mtimeval[0]);
        }
        return ((WindowsLibC)libc())._utime64(path, times);
    }

    @Override
    public int wait(int[] status) {
        handler.unimplementedError("wait");

        return -1;
    }

    @Override
    public int waitpid(int pid, int[] status, int flags) {
        handler.unimplementedError("waitpid");

        return -1;
    }

    @Override
    public String getlogin() {
        return helper.getlogin();
    }

    @Override
    public int endgrent() {
        return 0;
    }

    @Override
    public int endpwent() {
        return helper.endpwent();
    }

    @Override
    public Group getgrent() {
        return null;
    }

    @Override
    public Passwd getpwent() {
        return null;
    }

    @Override
    public Group getgrgid(int which) {
        return null;
    }

    @Override
    public Passwd getpwnam(String which) {
        return null;
    }

    @Override
    public Group getgrnam(String which) {
        return null;
    }
    
    @Override
    public int setgrent() {
        return 0;
    }
    
    @Override
    public int setpwent() {
        return helper.setpwent();
    }

    @Override
    public Passwd getpwuid(int which) {
        return null;
    }

    @Override
    public boolean isatty(FileDescriptor fd) {
        int handle = (int)helper.gethandle(fd);

        int type = ((WindowsLibC)libc()).GetFileType(handle);
        if (type == FILE_TYPE_CHAR) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int link(String oldpath, String newpath) {
        boolean linkCreated =  ((WindowsLibC)libc()).CreateHardLinkA(newpath, oldpath, null);

        if (!linkCreated) {
            int error = errno();
            handler.error(mapErrorToErrno(error), oldpath + " or " + newpath);
            return error;
        } else {
            return 0;
        }
    }

    private static Errno mapErrorToErrno(int error) {
        Errno errno = errorToErrnoMapper.get(error);
        if (errno == null) {
            errno = __UNKNOWN_CONSTANT__;
        }
        return errno;
    }
}
