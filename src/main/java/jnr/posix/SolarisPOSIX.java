package jnr.posix;

import static jnr.constants.platform.Errno.*;

import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.Pointer;
import java.io.FileDescriptor;
import jnr.posix.util.Platform;

final class SolarisPOSIX extends BaseNativePOSIX {
    SolarisPOSIX(String libraryName, LibCProvider libc, POSIXHandler handler) {
        super(libraryName, libc, handler);
    }
    
    public BaseHeapFileStat allocateStat() {
        return Platform.IS_32_BIT ? new SolarisHeapFileStat(this) : new Solaris64FileStat(this);
    }
    
    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        FileStat stat = allocateStat();
        int fd = helper.getfd(fileDescriptor);

        if (libc().fstat64(fd, stat) < 0) handler.error(ENOENT, ""+fd);
        
        return stat;
    }
    
    @Override
    public int lchmod(String filename, int mode) {
        handler.unimplementedError("lchmod");
        
        return -1;
    }
    
    @Override
    public int lstat(String path, FileStat stat) {
        return libc().lstat64(path, stat);
    }
    
    @Override
    public int stat(String path, FileStat stat) {
        return libc().stat64(path, stat);
    }
    
    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new SolarisPasswd((Pointer) arg) : null;
        }
    };
}
