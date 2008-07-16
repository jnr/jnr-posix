package org.jruby.ext.posix;

public class Linux64HeapFileStat extends BaseHeapFileStat {
    public Int64 st_dev = new Int64();
    public Int64 st_ino = new Int64();
    public Int64 st_nlink = new Int64();
    public Int32 st_mode = new Int32();
    public Int32 st_uid = new Int32();
    public Int32 st_gid = new Int32();
    public Int64 st_rdev = new Int64();
    public Int64 st_size = new Int64();
    public Int64 st_blksize = new Int64();
    public Int64 st_blocks = new Int64();
    public Int64 st_atime = new Int64();     // Time of last access (time_t)
    public Int64 st_atimensec = new Int64(); // Time of last access (nanoseconds)
    public Int64 st_mtime = new Int64();     // Last data modification time (time_t)
    public Int64 st_mtimensec = new Int64(); // Last data modification time (nanoseconds)
    public Int64 st_ctime = new Int64();     // Time of last status change (time_t)
    public Int64 st_ctimensec = new Int64(); // Time of last status change (nanoseconds)
    public Int64 __unused4 = new Int64();
    public Int64 __unused5 = new Int64();
    public Int64 __unused6 = new Int64();

    public Linux64HeapFileStat() {
        super(null);
    }

    public Linux64HeapFileStat(POSIX posix) {
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
