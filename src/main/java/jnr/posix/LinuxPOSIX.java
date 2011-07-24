package jnr.posix;

import static jnr.constants.platform.Errno.*;

import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.Pointer;
import java.io.FileDescriptor;
import jnr.posix.util.Platform;

final class LinuxPOSIX extends BaseNativePOSIX {
    private volatile boolean use_fxstat64 = true;
    private volatile boolean use_lxstat64 = true;
    private volatile boolean use_xstat64 = true;
    private final int statVersion;
    
    LinuxPOSIX(String libraryName, LibCProvider libcProvider, POSIXHandler handler) {
        super(libraryName, libcProvider, handler);


        statVersion = Platform.IS_32_BIT ? 3 : 0;
    }
    
    @Override
    public BaseHeapFileStat allocateStat() {
        if (Platform.IS_32_BIT) {
            return new LinuxHeapFileStat(this);
        } else {
            return new Linux64HeapFileStat(this);
        }
    }

    private final FileStat old_fstat(FileDescriptor fileDescriptor) {
        try {
            return super.fstat(fileDescriptor);
        } catch (UnsatisfiedLinkError ex2) {
            handler.unimplementedError("fstat");
            return null;
        }
    }

    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        if (use_fxstat64) {
            try {
                FileStat stat = allocateStat();
                int fd = helper.getfd(fileDescriptor);

                if (((LinuxLibC) libc()).__fxstat64(statVersion, fd, stat) < 0) {
                    handler.error(ENOENT, "" + fd);
                }

                return stat;

            } catch (UnsatisfiedLinkError ex) {
                use_fxstat64 = false;
                return old_fstat(fileDescriptor);
            }

        } else {
            return old_fstat(fileDescriptor);
        }
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
                    handler.error(ENOENT, path);
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
                    handler.error(ENOENT, path);
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
    
    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new LinuxPasswd((Pointer) arg) : null;
        }
    };
}
