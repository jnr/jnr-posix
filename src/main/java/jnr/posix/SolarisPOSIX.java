package jnr.posix;

import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Sysconf;
import jnr.ffi.*;
import jnr.ffi.mapper.FromNativeContext;
import jnr.posix.util.MethodName;
import jnr.posix.util.Platform;

import static jnr.constants.platform.Errno.EINVAL;

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

    public static final int LOCK_SH = 1;
    public static final int LOCK_EX = 2;
    public static final int LOCK_NB = 4;
    public static final int LOCK_UN = 8;

    public static final int SEEK_SET = 0;

    public static class Layout extends StructLayout {
        protected Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final int16_t l_type = new int16_t(); // short
        public final int16_t l_whence = new int16_t(); // short
        public final off_t l_start = new off_t();
        public final off_t l_len = new off_t();
        public final int32_t l_sysid = new int32_t(); // int
        public final pid_t l_pid = new pid_t();
        public final int32_t[] l_pad = new int32_t[4]; // int[4]
    }

    private static final Layout FLOCK_LAYOUT = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public int flock(int fd, int operation) {
        Pointer lock = getRuntime().getMemoryManager().allocateTemporary(FLOCK_LAYOUT.size(), true);

        switch (operation & ~LOCK_NB) {
            case LOCK_SH:
                FLOCK_LAYOUT.l_type.set(lock, (short) Fcntl.F_RDLCK.intValue());
                break;
            case LOCK_EX:
                FLOCK_LAYOUT.l_type.set(lock, (short) Fcntl.F_WRLCK.intValue());
                break;
            case LOCK_UN:
                FLOCK_LAYOUT.l_type.set(lock, (short) Fcntl.F_UNLCK.intValue());
                break;
            default:
                errno(EINVAL.intValue());
                return -1;
        }
        FLOCK_LAYOUT.l_whence.set(lock, (short) SEEK_SET);
        FLOCK_LAYOUT.l_start.set(lock, 0);
        FLOCK_LAYOUT.l_len.set(lock, 0);

        return libc().fcntl(fd, (operation & LOCK_NB) != 0 ? Fcntl.F_SETLK.intValue() : Fcntl.F_SETLKW.intValue(), lock);
    }

    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new SolarisPasswd((Pointer) arg) : null;
        }
    };

    public Pointer allocatePosixSpawnFileActions() {
        return Memory.allocateDirect(getRuntime(), 8);
    }

    public Pointer allocatePosixSpawnattr() {
        return Memory.allocateDirect(getRuntime(), 8);
    }
}
