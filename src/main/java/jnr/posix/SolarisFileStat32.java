
package jnr.posix;

import jnr.ffi.StructLayout;

public class SolarisFileStat32 extends BaseFileStat implements NanosecondFileStat {
    static final class Layout extends StructLayout {

        Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }
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
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public SolarisFileStat32(NativePOSIX posix) {
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
