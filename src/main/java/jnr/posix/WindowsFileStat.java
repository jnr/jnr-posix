package jnr.posix;

import jnr.ffi.StructLayout;

// http://msdn.microsoft.com/en-us/library/14h5k7ff.aspx
// This layout is meant to be used with stat64() family so _USE_32BIT_TIME_T is not in play.
public class WindowsFileStat extends BaseFileStat {
    private static final class Layout extends StructLayout {

        private Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

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
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public WindowsFileStat(NativePOSIX posix) {
        super(posix, layout);
    }

    public long atime() {
        return layout.st_atime.get(memory);
    }

    public long blockSize() {
        return 512;
    }

    public long blocks() {
        return (layout.st_size.get(memory) + 512 - 1) / 512;
    }

    public long ctime() {
        return layout.st_ctime.get(memory);
    }

    public long dev() {
        return layout.st_dev.get(memory);
    }

    public int gid() {
        return layout.st_gid.get(memory);
    }

    public long ino() {
        return layout.st_ino.get(memory);
    }

    public int mode() {
        return layout.st_mode.get(memory) & ~(S_IWGRP | S_IWOTH) & 0xffff;
    }

    public long mtime() {
        return layout.st_mtime.get(memory);
    }

    public int nlink() {
        return layout.st_nlink.get(memory);
    }

    public long rdev() {
        return layout.st_rdev.get(memory);
    }

    public long st_size() {
        return layout.st_size.get(memory);
    }

    public int uid() {
        return layout.st_uid.get(memory);
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
        return "st_dev: " + layout.st_dev.get(memory) +
                ", st_mode: " + Integer.toOctalString(mode()) +
                ", layout.st_nlink: " + layout.st_nlink.get(memory) +
                ", layout.st_rdev: " + layout.st_rdev.get(memory) +
                ", layout.st_size: " + layout.st_size.get(memory) +
                ", layout.st_uid: " + layout.st_uid.get(memory) +
                ", layout.st_gid: " + layout.st_gid.get(memory) +
                ", layout.st_atime: " + layout.st_atime.get(memory) +
                ", layout.st_ctime: " + layout.st_ctime.get(memory) +
                ", layout.st_mtime: " + layout.st_mtime.get(memory) +
                ", layout.st_ino: " + layout.st_ino.get(memory);
    }
}
