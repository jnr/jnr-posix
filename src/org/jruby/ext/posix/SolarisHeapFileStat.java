/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

/**
 *
 * @author enebo
 */
public class SolarisHeapFileStat extends BaseHeapFileStat {
    public final Int32 st_dev = new Int32();
    public final Int32 st_pad1_0 = new Int32();
    public final Int32 st_pad1_1 = new Int32();
    public final Int32 st_pad1_2 = new Int32();
    public final Int32 st_ino = new Int32();
    public final Int32 st_mode = new Int32();
    public final Int32 st_nlink = new Int32();
    public final Int32 st_uid = new Int32();
    public final Int32 st_gid = new Int32();
    public final Int32 st_rdev = new Int32();
    public final Int32 st_pad2_0 = new Int32();
    public final Int32 st_pad2_1 = new Int32();
    public final Int32 st_size = new Int32();
    public final Int32 st_pad3 = new Int32();
    public final Int32 st_atim_sec = new Int32();
    public final Int32 st_atim_nsec = new Int32();
    public final Int32 st_mtim_sec = new Int32();
    public final Int32 st_mtim_nsec = new Int32();
    public final Int32 st_ctim_sec = new Int32();
    public final Int32 st_ctim_nsec = new Int32();
    public final Int32 st_blksize = new Int32();
    public final Int32 st_blocks = new Int32();
    public final Int32 st_pad7 = new Int32();
    public final Int32 st_pad8 = new Int32();
    public final Int32 st_pad9 = new Int32();
    public final Int32 st_pad4_0 = new Int32();
    public final Int32 st_pad4_1 = new Int32();
    public final Int32 st_pad4_2 = new Int32();
    public final Int32 st_pad4_3 = new Int32();
    public final Int32 st_pad4_4 = new Int32();
    public final Int32 st_pad4_5 = new Int32();
    public final Int32 st_pad4_6 = new Int32();
    public final Int32 st_pad4_7 = new Int32();
    
    public SolarisHeapFileStat() {
        this(null);
    }
    public SolarisHeapFileStat(POSIX posix) {
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

    public int ino() {
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
