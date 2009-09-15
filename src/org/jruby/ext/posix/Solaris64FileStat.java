
package org.jruby.ext.posix;


public class Solaris64FileStat extends BaseHeapFileStat {
    public static final int _ST_FSTYPSZ = 16;		/* array size for file system type name */
    public final UnsignedLong st_dev = new UnsignedLong();
    public final Int64 st_ino = new Int64();
    public final Int32 st_mode = new Int32();
    public final Int32 st_nlink = new Int32();
    public final Int32 st_uid = new Int32();
    public final Int32 st_gid = new Int32();
    public final UnsignedLong st_rdev = new UnsignedLong();
    public final Int64 st_size = new Int64();
    public final SignedLong st_atim_sec = new SignedLong();
    public final SignedLong st_atim_nsec = new SignedLong();
    public final SignedLong st_mtim_sec = new SignedLong();
    public final SignedLong st_mtim_nsec = new SignedLong();
    public final SignedLong st_ctim_sec = new SignedLong();
    public final SignedLong st_ctim_nsec = new SignedLong();
    public final Int32 st_blksize = new Int32();
    public final Int64 st_blocks = new Int64();
    public final Signed8[] st_fstype = array(new Signed8[_ST_FSTYPSZ]);
    
    public Solaris64FileStat() {
        this(null);
    }
    public Solaris64FileStat(POSIX posix) {
        super(posix);
    }
    
    public long atime() {
        return st_atim_sec.get();
    }

    public long blocks() {
        return st_blocks.get();
    }

    public long blockSize() {
        return st_blksize.get();
    }

    public long ctime() {
        return st_ctim_sec.get();
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
        return st_mode.get() & 0xffff;
    }

    public long mtime() {
        return st_mtim_sec.get();
    }

    public int nlink() {
        return st_nlink.get();
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
