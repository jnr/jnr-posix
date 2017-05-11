package jnr.posix;

import jnr.constants.platform.Errno;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.FromNativeContext;
import jnr.posix.util.Platform;

import java.io.FileDescriptor;

final class LinuxPOSIX extends BaseNativePOSIX implements Linux {
    private volatile boolean use_fxstat64 = true;
    private volatile boolean use_lxstat64 = true;
    private volatile boolean use_xstat64 = true;
    private final int statVersion;

    LinuxPOSIX(LibCProvider libcProvider, POSIXHandler handler) {
        super(libcProvider, handler);


        if (Platform.IS_32_BIT) {
            statVersion = 3;
        } else {
            FileStat stat = allocateStat();

            if (((LinuxLibC) libc()).__xstat64(0, "/dev/null", stat) < 0) {
                statVersion = 1;
            } else {
                statVersion = 0;
            }
        }
    }

    @Override
    public FileStat allocateStat() {
        if (Platform.IS_32_BIT) {
            return new LinuxFileStat32(this);
        } else {
            if ("aarch64".equals(Platform.ARCH)) {
                return new LinuxFileStatAARCH64(this);
            } else {
                return new LinuxFileStat64(this);
            }
        }
    }

    public MsgHdr allocateMsgHdr() {
        return new LinuxMsgHdr(this);
    }

    @Override
    public Pointer allocatePosixSpawnFileActions() {
        return Memory.allocateDirect(getRuntime(), 80);
    }

    @Override
    public Pointer allocatePosixSpawnattr() {
        return Memory.allocateDirect(getRuntime(), 336);
    }

    public SocketMacros socketMacros() {
        return LinuxSocketMacros.INSTANCE;
    }

    private int old_fstat(int fd, FileStat stat) {
        try {
            return super.fstat(fd, stat);
        } catch (UnsatisfiedLinkError ex2) {
            handler.unimplementedError("fstat");
            return -1;
        }
    }

    @Override
    public int fstat(int fd, FileStat stat) {
        if (use_fxstat64) {
            int ret;
            try {
                if ((ret = ((LinuxLibC) libc()).__fxstat64(statVersion, fd, stat)) < 0) {
                    handler.error(Errno.valueOf(errno()), "fstat", Integer.toString(fd));
                }

                return ret;

            } catch (UnsatisfiedLinkError ex) {
                use_fxstat64 = false;
                return old_fstat(fd, stat);
            }

        } else {
            return old_fstat(fd, stat);
        }
    }

    @Override
    public FileStat fstat(int fd) {
        FileStat stat = allocateStat();
        int ret = fstat(fd, stat);
        if (ret < 0) handler.error(Errno.valueOf(errno()), "fstat", Integer.toString(fd));
        return stat;
    }

    @Override
    public int fstat(FileDescriptor fileDescriptor, FileStat stat) {
        return fstat(helper.getfd(fileDescriptor), stat);
    }

    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        FileStat stat = allocateStat();
        int fd = helper.getfd(fileDescriptor);
        int ret = fstat(fd, stat);
        if (ret < 0) handler.error(Errno.valueOf(errno()), "fstat", Integer.toString(fd));
        return stat;
    }

    private final int old_lstat(String path, FileStat stat) {
        try {
            return super.lstat(path, stat);
        } catch (UnsatisfiedLinkError ex) {
            handler.unimplementedError("lstat");
            return -1;
        }
    }

    @Override
    public int lstat(String path, FileStat stat) {
        if (use_lxstat64) {
            try {
                return ((LinuxLibC) libc()).__lxstat64(statVersion, path, stat);
            } catch (UnsatisfiedLinkError ex) {
                use_lxstat64 = false;
                return old_lstat(path, stat);
            }
        } else {
            return old_lstat(path, stat);
        }
    }

    @Override
    public FileStat lstat(String path) {
        FileStat stat = allocateStat();
        int ret = lstat(path, stat);
        if (ret < 0) handler.error(Errno.valueOf(errno()), "lstat", path);
        return stat;
    }

    private final int old_stat(String path, FileStat stat) {
        try {
            return super.stat(path, stat);
        } catch (UnsatisfiedLinkError ex) {
            handler.unimplementedError("stat");
            return -1;
        }
    }

    @Override
    public int stat(String path, FileStat stat) {

        if (use_xstat64) {
            try {
                return ((LinuxLibC) libc()).__xstat64(statVersion, path, stat);
            } catch (UnsatisfiedLinkError ex) {
                use_xstat64 = false;
                return old_stat(path, stat);
            }

        } else {
            return old_stat(path, stat);
        }
    }

    @Override
    public FileStat stat(String path) {
        FileStat stat = allocateStat();
        int ret = stat(path, stat);
        if (ret < 0) handler.error(Errno.valueOf(errno()), "stat", path);
        return stat;
    }

    public long sysconf(Sysconf name) {
        return libc().sysconf(name);
    }

    public Times times() {
        return NativeTimes.times(this);
    }

    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new LinuxPasswd((Pointer) arg) : null;
        }
    };

    static final public class Syscall {
        static final ABI _ABI_X86_32 = new ABI_X86_32();
        static final ABI _ABI_X86_64 = new ABI_X86_64();
        static final ABI _ABI_AARCH64 = new ABI_AARCH64();

        public static ABI abi() {
            if ("x86_64".equals(Platform.ARCH)) {
                if (Platform.IS_64_BIT) {
                    return _ABI_X86_64;
                }
            } else if ("i386".equals(Platform.ARCH)) {
                return _ABI_X86_32;
            } else if ("aarch64".equals(Platform.ARCH)) {
                return _ABI_AARCH64;
            }
            return null;
        }

        interface ABI {
            public int __NR_ioprio_set();
            public int __NR_ioprio_get();
        }

        /** @see /usr/include/asm/unistd_32.h */
        final static class ABI_X86_32 implements ABI {
            @Override
            public int __NR_ioprio_set() {
                return 289;
            }
            @Override
            public int __NR_ioprio_get() {
                return 290;
            }
        }

        /** @see /usr/include/asm/unistd_64.h */
        final static class ABI_X86_64 implements ABI {
            @Override
            public int __NR_ioprio_set() {
                return 251;
            }
            @Override
            public int __NR_ioprio_get() {
                return 252;
            }
        }

        /** @see /usr/include/asm-generic/unistd.h */
        final static class ABI_AARCH64 implements ABI {
            @Override
            public int __NR_ioprio_set() {
                return 30;
            }
            @Override
            public int __NR_ioprio_get() {
                return 31 ;
            }
        }
    }

    public int ioprio_get(int which, int who) {
        Syscall.ABI abi = Syscall.abi();
        if (abi == null) {
            handler.unimplementedError("ioprio_get");
            return -1;
        }

        return libc().syscall(abi.__NR_ioprio_get(), which, who);
    }

    public int ioprio_set(int which, int who, int ioprio) {
        Syscall.ABI abi = Syscall.abi();
        if (abi == null) {
            handler.unimplementedError("ioprio_set");
            return -1;
        }

        return libc().syscall(abi.__NR_ioprio_set(), which, who, ioprio);
    }
}
