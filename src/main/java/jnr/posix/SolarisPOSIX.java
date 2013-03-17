package jnr.posix;

import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.FromNativeContext;
import jnr.posix.util.Platform;

import java.io.FileDescriptor;

import static jnr.constants.platform.Errno.ENOENT;

final class SolarisPOSIX extends BaseNativePOSIX {
    SolarisPOSIX(LibCProvider libc, POSIXHandler handler) {
        super(libc, handler);
    }
    
    public FileStat allocateStat() {
        return Platform.IS_32_BIT ? new SolarisFileStat32(this) : new SolarisFileStat64(this);
    }
    
    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        FileStat stat = allocateStat();
        int fd = helper.getfd(fileDescriptor);

        if ((Platform.IS_32_BIT ? libc().fstat64(fd, stat) : libc().fstat(fd, stat)) < 0) handler.error(ENOENT, ""+fd);
        
        return stat;
    }
    
    @Override
    public int lchmod(String filename, int mode) {
        handler.unimplementedError("lchmod");
        
        return -1;
    }
    
    @Override
    public int lstat(String path, FileStat stat) {
        return Platform.IS_32_BIT ? libc().lstat64(path, stat) : libc().lstat(path, stat);
    }
    
    @Override
    public int stat(String path, FileStat stat) {
        return Platform.IS_32_BIT ? libc().stat64(path, stat) : libc().stat(path, stat);
    }


    public long sysconf(Sysconf name) {
        return libc().sysconf(name);
    }

    public Times times() {
        return NativeTimes.times(this);
    }


    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new SolarisPasswd((Pointer) arg) : null;
        }
    };
}
