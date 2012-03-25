package jnr.posix;

import jnr.ffi.*;

/**
 * defines the memory layout of a struct stat on MacOS
 */
public class MacOSStatLayout extends StructLayout {

    public MacOSStatLayout(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public final class time_t extends SignedLong {
    }
    public final Signed32 st_dev = new Signed32();
    public final Signed32 st_ino = new Signed32();
    public final Signed16 st_mode = new Signed16();
    public final Signed16 st_nlink = new Signed16();
    public final Signed32 st_uid = new Signed32();
    public final Signed32 st_gid = new Signed32();
    public final Signed32 st_rdev = new Signed32();
    public final time_t st_atime = new time_t();
    public final SignedLong st_atimensec = new SignedLong();
    public final time_t st_mtime = new time_t();
    public final SignedLong st_mtimensec = new SignedLong();
    public final time_t st_ctime = new time_t();
    public final SignedLong st_ctimensec = new SignedLong();
    public final Signed64 st_size = new Signed64();
    public final Signed64 st_blocks = new Signed64();
    public final Signed32 st_blksize = new Signed32();
    public final Signed32 st_flags = new Signed32();
    public final Signed32 st_gen = new Signed32();
    public final Signed32 st_lspare = new Signed32();
    public final Signed64 st_qspare0 = new Signed64();
    public final Signed64 st_qspare1 = new Signed64();
}
