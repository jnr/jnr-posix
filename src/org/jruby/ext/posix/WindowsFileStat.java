package org.jruby.ext.posix;

public class WindowsFileStat extends BaseHeapFileStat {
    public final Signed32 st_dev = new Signed32();
    public final Signed16 st_ino = new Signed16();
    public final Signed16 st_mode = new Signed16();
    public final Signed16 st_nlink = new Signed16();
    public final Signed16 st_uid = new Signed16();
    public final Signed16 st_gid = new Signed16();
    public final Signed32 st_rdev = new Signed32();
    public final Signed64 st_size = new Signed64();
    public final Signed64 st_atime = new Signed64();
    public final Signed64 st_mtime = new Signed64();
    public final Signed64 st_ctime = new Signed64();

    public WindowsFileStat(POSIX posix) {
        super(posix);
    }

    public long atime() {
        return st_atime.get();
    }

    public long blockSize() {
        return 512;
    }

    public long blocks() {
        return (st_size.get() + 512 - 1) / 512;
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
        return st_mode.get() & 0xffff;
    }

    public long mtime() {
        return st_mtime.get();
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
    public java.lang.String toString() {
        return "st_dev: " + st_dev.get() +
                ", st_mode: " + Integer.toOctalString(mode()) +
                ", st_nlink: " + st_nlink.get() +
                ", st_rdev: " + st_rdev.get() +
                ", st_size: " + st_size.get() +
                ", st_uid: " + st_uid.get() +
                ", st_gid: " + st_gid.get() +
                ", st_atime: " + st_atime.get() +
                ", st_ctime: " + st_ctime.get() +
                ", st_mtime: " + st_mtime.get() +
                ", st_ino: " + st_ino.get();
    }
}
