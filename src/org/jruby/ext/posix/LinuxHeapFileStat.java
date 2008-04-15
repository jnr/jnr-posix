/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

/**
 *
 * @author enebo
 */
public class LinuxHeapFileStat extends BaseHeapFileStat {
    public Int64 st_dev = new Int64();
    public Short __pad1 = new Short();
    public Int32 st_ino = new Int32();
    public Int32 st_mode = new Int32();
    public Int32 st_nlink = new Int32();
    public Int32 st_uid = new Int32();
    public Int32 st_gid = new Int32();
    public Int64 st_rdev = new Int64();
    public Short __pad2 = new Short();
    public Int32 st_size = new Int32();
    public Int32 st_blksize = new Int32();
    public Int32 st_blocks = new Int32();
    public Int32 st_atim_sec = new Int32();     // Time of last access (time_t)
    public Int32 st_atim_nsec = new Int32(); // Time of last access (nanoseconds)
    public Int32 st_mtim_sec = new Int32();     // Last data modification time (time_t)
    public Int32 st_mtim_nsec = new Int32(); // Last data modification time (nanoseconds)
    public Int32 st_ctim_sec = new Int32();     // Time of last status change (time_t)
    public Int32 st_ctim_nsec = new Int32(); // Time of last status change (nanoseconds)
    public Int32 __unused4 = new Int32();
    public Int32 __unused5 = new Int32();
    
    public LinuxHeapFileStat() {
        this(null);
    }
    public LinuxHeapFileStat(POSIX posix) {
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
