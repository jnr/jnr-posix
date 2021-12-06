package jnr.posix;

import java.nio.ByteBuffer;
import jnr.constants.platform.Confstr;
import jnr.constants.platform.Pathconf;
import jnr.constants.platform.Sysconf;
import jnr.ffi.*;
import jnr.ffi.mapper.FromNativeContext;


final class MacOSPOSIX extends BaseNativePOSIX {

    private final NSGetEnviron environ;

    MacOSPOSIX(LibCProvider libcProvider, POSIXHandler handler) {
        super(libcProvider, handler);

        final LibraryLoader<NSGetEnviron> loader = LibraryLoader.create(NSGetEnviron.class);
        loader.library("libSystem.B.dylib");
        environ = loader.load();
    }

    public FileStat allocateStat() {
        if (Platform.getNativePlatform().getCPU() == Platform.CPU.AARCH64) {
            return new MacOSFileStat64(this);
        }

        return new MacOSFileStat(this);
    }

    public MsgHdr allocateMsgHdr() {
        return new MacOSMsgHdr(this);
    }

    @Override
    public Pointer allocatePosixSpawnFileActions() {
        return Memory.allocateDirect(getRuntime(), 8);
    }

    @Override
    public Pointer allocatePosixSpawnattr() {
        return Memory.allocateDirect(getRuntime(), 8);
    }

    public SocketMacros socketMacros() {
        return MacOSSocketMacros.INSTANCE;
    }

    public long sysconf(Sysconf name) {
        return libc().sysconf(name);
    }

    public int confstr(Confstr name, ByteBuffer buf, int len) {
        return libc().confstr(name, buf, len);
    }

    public int fpathconf(int fd, Pathconf name) {
        return libc().fpathconf(fd, name);
    }

    public Times times() {
        return NativeTimes.times(this);
    }

    @Override
    public Pointer environ() {
        return environ._NSGetEnviron().getPointer(0);
    }

    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new MacOSPasswd((Pointer) arg) : null;
        }
    };
}
