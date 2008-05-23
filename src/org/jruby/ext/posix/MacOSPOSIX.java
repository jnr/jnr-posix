package org.jruby.ext.posix;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;

public class MacOSPOSIX extends BaseNativePOSIX {
    private boolean hasLchmod;
    private boolean hasLchown;

    public MacOSPOSIX(String libraryName, LibC libc, POSIXHandler handler) {
        super(libraryName, libc, handler);
        
        hasLchmod = hasMethod("lchmod");
        hasLchown = hasMethod("lchown");
    }
    
    public FileStat allocateStat() {
        return new MacOSHeapFileStat(this);
    }
    
    @Override
    public int lchmod(String filename, int mode) {
        if (!hasLchmod) handler.unimplementedError("lchmod");
        
        return libc.lchmod(filename, mode);
    }
    
    @Override
    public int lchown(String filename, int user, int group) {
        if (!hasLchown) handler.unimplementedError("lchown");
        
        return super.lchown(filename, user, group);
    }
    
    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return new MacOSPasswd((Pointer) arg);
        }
    };
}
