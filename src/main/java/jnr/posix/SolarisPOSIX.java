package jnr.posix;

import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.FromNativeContext;
import jnr.posix.util.MethodName;
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

    public MsgHdr allocateMsgHdr() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return null;
    }

    public SocketMacros socketMacros() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return null;
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
