package org.jruby.ext.posix;

import com.kenai.jaffl.mapper.FromNativeContext;
import com.kenai.jaffl.Pointer;
import java.io.FileDescriptor;
import org.jruby.ext.posix.util.Platform;

public class SolarisPOSIX extends BaseNativePOSIX {
    public SolarisPOSIX(String libraryName, LibCProvider libc, POSIXHandler handler) {
        super(libraryName, libc, handler);
    }
    
    public BaseHeapFileStat allocateStat() {
        return Platform.IS_32_BIT ? new SolarisHeapFileStat(this) : new Solaris64FileStat(this);
    }
    
    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        FileStat stat = allocateStat();
        int fd = helper.getfd(fileDescriptor);

        if ((Platform.IS_64_BIT ? libc().fstat(fd, stat) : libc().fstat64(fd, stat)) < 0) handler.error(ERRORS.ENOENT, ""+fd);
        
        return stat;
    }
    
    @Override
    public int lchmod(String filename, int mode) {
        handler.unimplementedError("lchmod");
        
        return -1;
    }
    
    @Override
    public FileStat lstat(String path) {
        FileStat stat = allocateStat();

        if ((Platform.IS_64_BIT ? libc().lstat(path, stat) : libc().lstat64(path, stat)) < 0) handler.error(ERRORS.ENOENT, path);
        
        return stat;
    }
    
    @Override
    public FileStat stat(String path) {
        FileStat stat = allocateStat(); 

        if ((Platform.IS_64_BIT ? libc().stat(path, stat) : libc().stat64(path, stat)) < 0) handler.error(ERRORS.ENOENT, path);
        
        return stat;
    }
    
    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new SolarisPasswd((Pointer) arg) : null;
        }
    };
}
