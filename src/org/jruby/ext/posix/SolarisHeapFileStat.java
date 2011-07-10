
package org.jruby.ext.posix;

import jnr.ffi.struct.StructUtil;

public class SolarisHeapFileStat extends BaseHeapFileStat {
    public static final int _ST_FSTYPSZ = 16;		/* array size for file system type name */
    
    public final Signed32 st_dev = new Signed32();
    public final SignedLong[] st_pad1 = array(new SignedLong[3]);
    public final Signed64 st_ino = new Signed64();
    public final Signed32 st_mode = new Signed32();
    public final Signed32 st_nlink = new Signed32();
    public final Signed32 st_uid = new Signed32();
    public final Signed32 st_gid = new Signed32();
    public final Signed32 st_rdev = new Signed32();
    public final SignedLong[] st_pad2 = array(new SignedLong[2]);
    public final Signed64 st_size = new Signed64();
    public final Signed32 st_atim_sec = new Signed32();
    public final Signed32 st_atim_nsec = new Signed32();
    public final Signed32 st_mtim_sec = new Signed32();
    public final Signed32 st_mtim_nsec = new Signed32();
    public final Signed32 st_ctim_sec = new Signed32();
    public final Signed32 st_ctim_nsec = new Signed32();
    public final Signed32 st_blksize = new Signed32();
    public final Signed64 st_blocks = new Signed64();
    public final Signed8[] st_fstype = array(new Signed8[_ST_FSTYPSZ]);
    public final SignedLong[] st_pad4 = array(new SignedLong[8]);
    
    public SolarisHeapFileStat(NativePOSIX posix) {
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
