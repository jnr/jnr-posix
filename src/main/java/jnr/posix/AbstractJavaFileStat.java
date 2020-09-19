package jnr.posix;

public abstract class AbstractJavaFileStat implements FileStat {
    protected final POSIXHandler handler;
    protected final POSIX posix;

    public AbstractJavaFileStat(POSIX posix, POSIXHandler handler) {
        this.handler = handler;
        this.posix = posix;
    }

    public boolean isBlockDev() {
        handler.unimplementedError("block device detection");

        return false;
    }

    /**
     * Limitation: [see JRUBY-1516] We just pick more likely value.  This is a little scary.
     */
    public boolean isCharDev() {
        return false;
    }

    public boolean isFifo() {
        handler.unimplementedError("fifo file detection");

        return false;
    }

    public boolean isNamedPipe() {
        handler.unimplementedError("piped file detection");

        return false;
    }

    public boolean isSetgid() {
        handler.unimplementedError("setgid detection");

        return false;
    }

    public boolean isSetuid() {
        handler.unimplementedError("setuid detection");

        return false;
    }

    public boolean isSocket() {
        handler.unimplementedError("socket file type detection");

        return false;
    }

    public boolean isSticky() {
        handler.unimplementedError("sticky bit detection");

        return false;
    }

    public int major(long dev) {
        handler.unimplementedError("major device");

        return -1;
    }

    public int minor(long dev) {
        handler.unimplementedError("minor device");

        return -1;
    }

    public int nlink() {
        handler.unimplementedError("stat.nlink");

        return -1;
    }

    public long rdev() {
        handler.unimplementedError("stat.rdev");

        return -1;
    }

    // Limitation: We have no pure-java way of getting uid. RubyZip needs this defined to work.
    public int uid() {
        return -1;
    }

    public long blocks() {
        handler.unimplementedError("stat.st_blocks");

        return -1;
    }

    public long blockSize() {
        // Limitation: We cannot determine, so always return 4096 (better than blowing up)
        return 4096;
    }

    public long dev() {
        handler.unimplementedError("stat.st_dev");

        return -1;
    }

    public String ftype() {
        if (isFile()) {
            return "file";
        } else if (isDirectory()) {
            return "directory";
        }

        return "unknown";
    }

    public int gid() {
        handler.unimplementedError("stat.st_gid");

        return -1;
    }

    public boolean groupMember(int gid) {
        return posix.getgid() == gid || posix.getegid() == gid;
    }

    /**
     *  Limitation: We have no pure-java way of getting inode.  webrick needs this defined to work.
     */
    public long ino() {
        return 0;
    }
}
