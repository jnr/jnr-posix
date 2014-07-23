package jnr.posix;

import jnr.ffi.*;
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

    public static SpawnAttribute sigdef(long sigdef) {
        throw new RuntimeException("sigdefault not yet supported");
//        return new Sigdef(sigdef);
    }

    public static SpawnAttribute sigmask(long sigmask) {
        throw new RuntimeException("sigmask not yet supported");
//        return new Sigmask(sigmask);
    }


    private static final class PGroup extends SpawnAttribute {
        final long pgroup;

        public PGroup(long pgroup) {
            this.pgroup = pgroup;
        }

        final boolean set(POSIX posix, Pointer nativeSpawnAttr) {
            return ((UnixLibC) posix.libc()).posix_spawnattr_setpgroup(nativeSpawnAttr, pgroup) == 0;
        }

        public String toString() {
            return "SpawnAttribute::PGroup(pgroup = " + pgroup + ")";
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

        public String toString() {
            return "SpawnAttribute::SetFlags(flags = " + Integer.toHexString(flags) + ")";
        }
    }

    private static final class Sigmask extends SpawnAttribute {
        final long sigmask;

        public Sigmask(long sigmask) {
            this.sigmask = sigmask;
        }

        final boolean set(POSIX posix, Pointer nativeSpawnAttr) {
            throw new RuntimeException("sigmask not yet supported");
//            return ((UnixLibC) posix.libc()).posix_spawnattr_setsigmask(nativeSpawnAttr, mask) == 0;
        }

        public String toString() {
            return "SpawnAttribute::Sigmask(mask = " + Long.toHexString(sigmask) + ")";
        }
    }

    private static final class Sigdef extends SpawnAttribute {
        final long sigdef;

        public Sigdef(long sigdef) {
            this.sigdef = sigdef;
        }

        final boolean set(POSIX posix, Pointer nativeSpawnAttr) {
            throw new RuntimeException("sigdefault not yet supported");
//            return ((UnixLibC) posix.libc()).posix_spawnattr_setsigdefault(nativeSpawnAttr, sigdef) == 0;
        }

        public String toString() {
            return "SpawnAttribute::Sigdef(def = " + Long.toHexString(sigdef) + ")";
        }
    }

}
