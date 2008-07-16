package org.jruby.ext.posix;

public class Linux64FileStat extends BaseNativeFileStat {
    public long st_dev;
    public long st_ino;
    public long st_nlink;
    public int st_mode;
    public int st_uid;
    public int st_gid;
    public long st_rdev;
    public long st_size;
    public long st_blksize;
    public long st_blocks;
    public long st_atime;     // Time of last access (time_t)
    public long st_atimensec; // Time of last access (nanoseconds)
    public long st_mtime;     // Last data modification time (time_t)
    public long st_mtimensec; // Last data modification time (nanoseconds)
    public long st_ctime;     // Time of last status change (time_t)
    public long st_ctimensec; // Time of last status change (nanoseconds)
    public long __unused4;
    public long __unused5;
        public long __unused6;


    public Linux64FileStat(POSIX posix) {
        super(posix);
    }

    public long atime() {
        return st_atime;
    }

    public long blockSize() {
        return st_blksize;
    }

    public long blocks() {
        return st_blocks;
    }

    public long ctime() {
        return st_ctime;
    }

    public long dev() {
        return st_dev;
    }

    public int gid() {
        return st_gid;
    }

    public long ino() {
        return st_ino;
    }

    public int mode() {
        return st_mode;
    }

    public long mtime() {
        return st_mtime;
    }

    public int nlink() {
        return (int) st_nlink;
    }

    public long rdev() {
        return st_rdev;
    }

    public long st_size() {
        return st_size;
    }

    public int uid() {
        return st_uid;
    }
}
