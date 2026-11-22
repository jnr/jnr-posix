package jnr.posix;

import jnr.ffi.Pointer;

/**
 * A {@link DirEnt} copied out of a struct dirent, whose layout differs between platforms.
 */
final class NativeDirEnt implements DirEnt {
    /**
     * Where the fields of a platform's struct dirent are.
     */
    static final class Layout {
        // 64-bit Linux, glibc and musl: d_ino (8), d_off (8), d_reclen (2), d_type (1), d_name
        static final Layout LINUX_64 = new Layout(8, 18, 19);
        // macOS with 64-bit inodes, the only kind on arm64: d_ino (8), d_seekoff (8), d_reclen (2), d_namlen (2),
        // d_type (1), d_name
        static final Layout DARWIN_64_BIT_INODE = new Layout(8, 20, 21);
        // macOS on x86_64, where opendir and readdir without the $INODE64 suffix use 32-bit inodes, as stat does in
        // MacOSFileStat: d_ino (4), d_reclen (2), d_type (1), d_namlen (1), d_name
        static final Layout DARWIN_32_BIT_INODE = new Layout(4, 6, 8);

        private final int inoSize;
        private final int typeOffset;
        private final int nameOffset;

        private Layout(int inoSize, int typeOffset, int nameOffset) {
            this.inoSize = inoSize;
            this.typeOffset = typeOffset;
            this.nameOffset = nameOffset;
        }

        DirEnt read(Pointer entry) {
            long ino = inoSize == 8 ? entry.getLong(0) : entry.getInt(0) & 0xffffffffL;
            int type = entry.getByte(typeOffset) & 0xff;

            return new NativeDirEnt(ino, type, entry.getString(nameOffset));
        }
    }

    private final long ino;
    private final int type;
    private final String name;

    private NativeDirEnt(long ino, int type, String name) {
        this.ino = ino;
        this.type = type;
        this.name = name;
    }

    @Override
    public long d_ino() {
        return ino;
    }

    @Override
    public int d_type() {
        return type;
    }

    @Override
    public String d_name() {
        return name;
    }

    @Override
    public String toString() {
        return "DirEnt[" + name + ", type " + type + ", ino " + ino + "]";
    }
}
