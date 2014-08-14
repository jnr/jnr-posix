package jnr.posix;

import jnr.constants.platform.Sysconf;
import jnr.ffi.*;
import jnr.ffi.mapper.FromNativeContext;


final class MacOSPOSIX extends BaseNativePOSIX {

    MacOSPOSIX(LibCProvider libcProvider, POSIXHandler handler) {
        super(libcProvider, handler);
    }

    public FileStat allocateStat() {
        return new MacOSFileStat(this);
    }

    public MsgHdr allocateMsgHdr() {
        return new MacOSMsgHdr(this);
    }

    public SocketMacros socketMacros() {
        return MacOSSocketMacros.INSTANCE;
    }

    public long sysconf(Sysconf name) {
        return libc().sysconf(name);
    }

    public Times times() {
        return NativeTimes.times(this);
    }
    
    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new MacOSPasswd((Pointer) arg) : null;
        }
    };
}
