
package jnr.posix;


import jnr.ffi.*;

public class SolarisFileStat64 extends BaseFileStat implements NanosecondFileStat {
    static final class Layout extends StructLayout {

        Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public static final int _ST_FSTYPSZ = 16;		/* array size for file system type name */
        public final UnsignedLong st_dev = new UnsignedLong();
        public final Signed64 st_ino = new Signed64();
        public final Signed32 st_mode = new Signed32();
        public final Signed32 st_nlink = new Signed32();
        public final Signed32 st_uid = new Signed32();
        public final Signed32 st_gid = new Signed32();
        public final UnsignedLong st_rdev = new UnsignedLong();
        public final Signed64 st_size = new Signed64();
        public final SignedLong st_atim_sec = new SignedLong();
        public final SignedLong st_atim_nsec = new SignedLong();
        public final SignedLong st_mtim_sec = new SignedLong();
        public final SignedLong st_mtim_nsec = new SignedLong();
        public final SignedLong st_ctim_sec = new SignedLong();
        public final SignedLong st_ctim_nsec = new SignedLong();
        public final Signed32 st_blksize = new Signed32();
        public final Signed64 st_blocks = new Signed64();
        public final Signed8[] st_fstype = array(new Signed8[_ST_FSTYPSZ]);
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public SolarisFileStat64() {
        this(null);
    }
    public SolarisFileStat64(NativePOSIX posix) {
        super(posix, layout);
    }

    public long atime() {
        return layout.st_atim_sec.get(memory);
    }

    public long blocks() {
        return layout.st_blocks.get(memory);
    }

    public long blockSize() {
        return layout.st_blksize.get(memory);
    }

    public long ctime() {
        return layout.st_ctim_sec.get(memory);
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
        return layout.st_mode.get(memory) & 0xffff;
    }

    public long mtime() {
        return layout.st_mtim_sec.get(memory);
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

    @Override
    public long aTimeNanoSecs() {
        return layout.st_atim_nsec.get(memory);
    }

    @Override
    public long cTimeNanoSecs() {
        return layout.st_ctim_nsec.get(memory);
    }

    @Override
    public long mTimeNanoSecs() {
        return layout.st_mtim_nsec.get(memory);
    }
}
