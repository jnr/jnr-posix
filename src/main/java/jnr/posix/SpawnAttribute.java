package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.TypeAlias;
import jnr.ffi.byref.NumberByReference;

public abstract class SpawnAttribute {
    public static final int RESETIDS   = 0x0001;  /* [SPN] R[UG]ID not E[UG]ID */
    public static final int SETPGROUP  = 0x0002;  /* [SPN] set non-parent PGID */
    public static final int SETSIGDEF  = 0x0004;  /* [SPN] reset sigset default */
    public static final int SETSIGMASK = 0x0008;  /* [SPN] set signal mask */
    
    abstract boolean set(POSIX posix, Pointer nativeFileActions);

    public static SpawnAttribute pgroup(long pgroup) {
        return new PGroup(pgroup);
    }

    public static SpawnAttribute flags(short flags) {
        return new SetFlags(flags);
    }

    public static SpawnAttribute sigmask(long sigmask) {
        return new Sigmask(sigmask);
    }


    private static final class PGroup extends SpawnAttribute {
        final long pgroup;

        public PGroup(long pgroup) {
            this.pgroup = pgroup;
        }

        final boolean set(POSIX posix, Pointer nativeSpawnAttr) {
            return ((UnixLibC) posix.libc()).posix_spawnattr_setpgroup(nativeSpawnAttr, pgroup) == 0;
        }
    }

    private static final class SetFlags extends SpawnAttribute {
        final short flags;

        public SetFlags(short flags) {
            this.flags = flags;
        }

        final boolean set(POSIX posix, Pointer nativeSpawnAttr) {
            return ((UnixLibC) posix.libc()).posix_spawnattr_setflags(nativeSpawnAttr, flags) == 0;
        }
    }

    private static final class Sigmask extends SpawnAttribute {
        final long sigmask;

        public Sigmask(long sigmask) {
            this.sigmask = sigmask;
        }

        final boolean set(POSIX posix, Pointer nativeSpawnAttr) {
            return ((UnixLibC) posix.libc()).posix_spawnattr_setsigmask(nativeSpawnAttr, new NumberByReference(TypeAlias.u_int32_t, sigmask)) == 0;
        }
    }

}
