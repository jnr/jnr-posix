package org.jruby.ext.posix;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;

public class SolarisPOSIX extends BaseNativePOSIX {
    public SolarisPOSIX(String libraryName, LibC libc, POSIXHandler handler) {
        super(libraryName, libc, handler);
    }
    
    public FileStat allocateStat() {
        return new SolarisHeapFileStat(this);
    }
    
    @Override
    public int lchmod(String filename, int mode) {
        handler.unimplementedError("lchmod");
        
        return -1;
    }
    
    public static class PasswordConverter extends PointerConverter {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return new SolarisPasswd((Pointer) arg);
        }
    }
    
    public static final PasswordConverter PASSWD = new PasswordConverter();
}
