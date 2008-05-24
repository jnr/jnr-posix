package org.jruby.ext.posix;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import java.io.FileDescriptor;

public class LinuxPOSIX extends BaseNativePOSIX {
    private static int STAT_VERSION = 3;

    private boolean hasFxstat;
    private boolean hasLxstat;
    private boolean hasXstat;
    private boolean hasFstat = false;
    private boolean hasLstat = false;
    private boolean hasStat = false;
    
    public LinuxPOSIX(String libraryName, LibC libc, POSIXHandler handler) {
        super(libraryName, libc, handler);

        /*
         * Most linux systems define stat/lstat/fstat as macros which force
         * us to call these weird signature versions.
         */
        hasFxstat = hasMethod("__fxstat64");
        hasLxstat = hasMethod("__lxstat64");
        hasXstat = hasMethod("__xstat64");
        
        /*
         * At least one person is using uLibc on linux which has real 
         * definitions for stat/lstat/fstat.
         */
        if (!hasFxstat) hasFstat = hasMethod("fstat64");
        if (!hasLxstat) hasLstat = hasMethod("lstat64");
        if (!hasXstat) hasStat = hasMethod("stat64");
    }
    
    @Override
    public FileStat allocateStat() {
        return new LinuxHeapFileStat(this);
    }

    @Override
    public FileStat fstat(FileDescriptor fileDescriptor) {
        if (!hasFxstat) {
            if (hasFstat) return super.fstat(fileDescriptor);
            
            handler.unimplementedError("fstat");
        }

        FileStat stat = allocateStat();
        int fd = helper.getfd(fileDescriptor);
        
        if (((LinuxLibC) libc).__fxstat64(STAT_VERSION, fd, stat) < 0) handler.error(ERRORS.ENOENT, "" + fd);
        
        return stat;
    }

    @Override
    public FileStat lstat(String path) {
        if (!hasLxstat) {
            if (hasLstat) return super.lstat(path);
            
            handler.unimplementedError("lstat");
        }

        FileStat stat = allocateStat();

        if (((LinuxLibC) libc).__lxstat64(STAT_VERSION, path, stat) < 0) handler.error(ERRORS.ENOENT, path);
        
        return stat;
    }

    @Override
    public FileStat stat(String path) {
        if (!hasXstat) {
            if (hasStat) return super.stat(path);
            
            handler.unimplementedError("stat");
        }
        
        FileStat stat = allocateStat(); 

        if (((LinuxLibC) libc).__xstat64(STAT_VERSION, path, stat) < 0) handler.error(ERRORS.ENOENT, path);
        
        return stat;
    }
    
    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return new LinuxPasswd((Pointer) arg);
        }
    };
}
