package jnr.posix;

import jnr.ffi.StructLayout;

public final class LinuxFileStat64 extends BaseFileStat {
    public static final class Layout extends StructLayout {

        public Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

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
    }

    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public LinuxFileStat64(LinuxPOSIX posix) {
        super(posix, layout);
    }

    public long atime() {
        return layout.st_atime.get(memory);
    }

    public long blockSize() {
        return layout.st_blksize.get(memory);
    }

    public long blocks() {
        return layout.st_blocks.get(memory);
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
        return layout.st_mode.get(memory);
    }

    public long mtime() {
        return layout.st_mtime.get(memory);
    }

    public int nlink() {
        return (int) layout.st_nlink.get(memory);
    }

    public long rdev() {
        return layout.st_rdev.get(memory);
    }

    public long st_size() {
        return layout. st_size.get(memory);
    }

    public int uid() {
        return layout.st_uid.get(memory);
    }
}
