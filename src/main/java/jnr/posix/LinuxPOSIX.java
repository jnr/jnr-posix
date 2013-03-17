package jnr.posix;

import jnr.constants.platform.Errno;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.FromNativeContext;
import jnr.posix.util.Platform;

import java.io.FileDescriptor;

import static jnr.constants.platform.Errno.ENOENT;

final class LinuxPOSIX extends BaseNativePOSIX {
    private volatile boolean use_fxstat64 = true;
    private volatile boolean use_lxstat64 = true;
    private volatile boolean use_xstat64 = true;
    private final int statVersion;
    
    LinuxPOSIX(LibCProvider libcProvider, POSIXHandler handler) {
        super(libcProvider, handler);


        statVersion = Platform.IS_32_BIT ? 3 : 0;
    }
    
    @Override
    public FileStat allocateStat() {
        if (Platform.IS_32_BIT) {
            return new LinuxFileStat32(this);
        } else {
            return new LinuxFileStat64(this);
        }
    }

    private FileStat old_fstat(int fd) {
        try {
            return super.fstat(fd);
        } catch (UnsatisfiedLinkError ex2) {
            handler.unimplementedError("fstat");
            return null;
        }
    }


    @Override
    public FileStat fstat(int fd) {
        if (use_fxstat64) {
            try {
                FileStat stat = allocateStat();
                if (((LinuxLibC) libc()).__fxstat64(statVersion, fd, stat) < 0) {
                    handler.error(Errno.valueOf(errno()), Integer.toString(fd));
                }

                return stat;

            } catch (UnsatisfiedLinkError ex) {
                use_fxstat64 = false;
                return old_fstat(fd);
            }

        } else {
            return old_fstat(fd);
        }
    }

    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        return fstat(helper.getfd(fileDescriptor));
    }

    private final FileStat old_lstat(String path) {
        try {
            return super.lstat(path);
        } catch (UnsatisfiedLinkError ex) {
            handler.unimplementedError("lstat");
            return null;
        }
    }

    @Override
    public FileStat lstat(String path) {
        if (use_lxstat64) {
            try {
                FileStat stat = allocateStat();

                if (((LinuxLibC) libc()).__lxstat64(statVersion, path, stat) < 0) {
                    handler.error(Errno.valueOf(errno()), path);
                }

                return stat;
            } catch (UnsatisfiedLinkError ex) {
                use_lxstat64 = false;
                return old_lstat(path);
            }
        } else {
            return old_lstat(path);
        }
    }

    private final FileStat old_stat(String path) {
        try {
            return super.stat(path);
        } catch (UnsatisfiedLinkError ex) {
            handler.unimplementedError("stat");
            return null;
        }
    }

    @Override
    public FileStat stat(String path) {

        if (use_xstat64) {
            try {
                FileStat stat = allocateStat();

                if (((LinuxLibC) libc()).__xstat64(statVersion, path, stat) < 0) {
                    handler.error(Errno.valueOf(errno()), path);
                }

                return stat;
            } catch (UnsatisfiedLinkError ex) {
                use_xstat64 = false;
                return old_stat(path);
            }

        } else {
            return old_stat(path);
        }
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
}
