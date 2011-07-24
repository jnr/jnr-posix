package org.jruby.ext.posix;

public final class Linux64HeapFileStat extends BaseHeapFileStat {
    public final Signed64 st_dev = new Signed64();
    public final Signed64 st_ino = new Signed64();
    public final Signed64 st_nlink = new Signed64();
    public final Signed32 st_mode = new Signed32();
    public final Signed32 st_uid = new Signed32();
    public final Signed32 st_gid = new Signed32();
    public final Signed64 st_rdev = new Signed64();
    public final Signed64 st_size = new Signed64();
    public final Signed64 st_blksize = new Signed64();
    public final Signed64 st_blocks = new Signed64();
    public final Signed64 st_atime = new Signed64();     // Time of last access (time_t)
    public final Signed64 st_atimensec = new Signed64(); // Time of last access (nanoseconds)
    public final Signed64 st_mtime = new Signed64();     // Last data modification time (time_t)
    public final Signed64 st_mtimensec = new Signed64(); // Last data modification time (nanoseconds)
    public final Signed64 st_ctime = new Signed64();     // Time of last status change (time_t)
    public final Signed64 st_ctimensec = new Signed64(); // Time of last status change (nanoseconds)
    public final Signed64 __unused4 = new Signed64();
    public final Signed64 __unused5 = new Signed64();
    public final Signed64 __unused6 = new Signed64();

    public Linux64HeapFileStat(NativePOSIX posix) {
        super(posix);
    }

    public long atime() {
        return st_atime.get();
    }

    public long blockSize() {
        return st_blksize.get();
    }

    public long blocks() {
        return st_blocks.get();
    }

    public long ctime() {
        return st_ctime.get();
    }

    public long dev() {
        return st_dev.get();
    }

    public int gid() {
        return st_gid.get();
    }

    public long ino() {
        return st_ino.get();
    }

    public int mode() {
        return st_mode.get();
    }

    public long mtime() {
        return st_mtime.get();
    }

    public int nlink() {
        return (int) st_nlink.get();
    }

    public long rdev() {
        return st_rdev.get();
    }

    public long st_size() {
        return st_size.get();
    }

    public int uid() {
        return st_uid.get();
    }
}
