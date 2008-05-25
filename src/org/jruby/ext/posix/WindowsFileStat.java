package org.jruby.ext.posix;

public class WindowsFileStat extends BaseNativeFileStat {
    public int st_dev;
    public short st_ino;
    public short st_mode;
    public short st_nlink;
    public short st_uid;
    public short st_gid;
    public int st_rdev;
    public long st_size;
    public int st_atime;
    public int spare1;
    public int st_mtime;
    public int spare2;
    public int st_ctime;
    public long st_blksize;
    public long st_blocks;

    public WindowsFileStat(POSIX posix) {
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
        return st_mode & 0xffff;
    }

    public long mtime() {
        return st_mtime;
    }

    public int nlink() {
        return st_nlink;
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

    // FIXME: Implement 
    @Override
    public boolean groupMember(int gid) {
        return true;
    }
    
    @Override
    public boolean isExecutable() {
        if (isOwned()) return (mode() & S_IXUSR) != 0;
        if (isGroupOwned()) return (mode() & S_IXGRP) != 0;
        if ((mode() & S_IXOTH) != 0) return false;

        return true;
    }

    @Override
    public boolean isExecutableReal() {
        if (isROwned()) return (mode() & S_IXUSR) != 0;
        if (groupMember(gid())) return (mode() & S_IXGRP) != 0;
        if ((mode() & S_IXOTH) != 0) return false;

        return true;
    }

    // FIXME: Implement
    @Override
    public boolean isOwned() {
        return true;
    }
    
    // FIXME: Implement
    @Override
    public boolean isROwned() {
        return true;
    }
    @Override
    public boolean isReadable() {
        if (isOwned()) return (mode() & S_IRUSR) != 0;
        if (isGroupOwned()) return (mode() & S_IRGRP) != 0;
        if ((mode() & S_IROTH) != 0) return false;

        return true;
    }
    
    @Override
    public boolean isReadableReal() {
        if (isROwned()) return (mode() & S_IRUSR) != 0;
        if (groupMember(gid())) return (mode() & S_IRGRP) != 0;
        if ((mode() & S_IROTH) != 0) return false;

        return true;
    }

    @Override
    public boolean isWritable() {
        if (isOwned()) return (mode() & S_IWUSR) != 0;
        if (isGroupOwned()) return (mode() & S_IWGRP) != 0;
        if ((mode() & S_IWOTH) != 0) return false;

        return true;
    }

    @Override
    public boolean isWritableReal() {
        if (isROwned()) return (mode() & S_IWUSR) != 0;
        if (groupMember(gid())) return (mode() & S_IWGRP) != 0;
        if ((mode() & S_IWOTH) != 0) return false;

        return true;
    }

    @Override
    public String toString() {
	return "st_dev: " + st_dev +
	    ", st_mode: " + Integer.toOctalString(st_mode) +
	    ", st_nlink: " + st_nlink +
	    ", st_rdev: " + st_rdev +
	    ", st_size: " + st_size +
	    ", st_uid: " + st_uid +
	    ", st_gid: " + st_gid +
	    ", st_atime: " + st_atime +
	    ", st_ctime: " + st_ctime +
	    ", st_mtime: " + st_mtime +
	    ", st_ino: " + st_ino;
    }
}
