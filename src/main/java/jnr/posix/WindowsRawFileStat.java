package jnr.posix;

import jnr.posix.util.WindowsHelpers;
import jnr.posix.windows.CommonFileInformation;
import jnr.posix.windows.WindowsFileInformation;

/**
 *
 */
public class WindowsRawFileStat extends JavaFileStat {
    private int st_atime;
    private int st_rdev;
    private int st_dev;
    private int st_nlink;
    private int st_mode;

    public WindowsRawFileStat(POSIX posix, POSIXHandler handler) {
        super(posix, handler);
    }

    public void setup(String path, CommonFileInformation fileInfo) {
        st_mode = fileInfo.getMode(path);
        setup(fileInfo);

        if (WindowsHelpers.isDriveLetterPath(path)) {
            int letterAsNumber = Character.toUpperCase(path.charAt(0)) - 'A';
            st_rdev = letterAsNumber;
            st_dev = letterAsNumber;
        }
    }

    public void setup(CommonFileInformation fileInfo) {
        st_atime = (int) fileInfo.getLastAccessTimeMicroseconds();
        st_mtime = (int) fileInfo.getLastWriteTimeMicroseconds();
        st_ctime = (int) fileInfo.getCreationTimeMicroseconds();
        st_size = isDirectory() ? 0 : fileInfo.getFileSize();
        st_nlink = 1;
        st_mode &= ~(S_IWGRP | S_IWOTH);
    }

    public int mode() {
        return st_mode;
    }

    public int gid() {
        return 0;
    }

    public int uid() {
        return 0;
    }

    public long atime() {
        return st_atime;
    }

    public long dev() {
        return st_dev;
    }

    public int nlink() {
        return st_nlink;
    }

    public long rdev() {
        return st_rdev;
    }

    @Override
    public long blocks() {
        return -1;
    }

    @Override
    public long blockSize() {
        return -1;
    }

    public boolean isBlockDev() {
        return (mode() & S_IFMT) == S_IFBLK;
    }

    public boolean isCharDev() {
        return (mode() & S_IFMT) == S_IFCHR;
    }

    public boolean isDirectory() {
        return (mode() & S_IFMT) == S_IFDIR;
    }

    public boolean isEmpty() {
        return st_size() == 0;
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

    public boolean isFile() {
        return (mode() & S_IFMT) == S_IFREG;
    }

    public boolean isFifo() {
        return (mode() & S_IFMT) == S_IFIFO;
    }

    public boolean isGroupOwned() {
        return groupMember(gid());
    }

    public boolean isIdentical(FileStat other) {
        return dev() == other.dev() && ino() == other.ino();
    }

    public boolean isNamedPipe() {
        return (mode() & S_IFIFO) != 0;
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

    public boolean isSetgid() {
        return (mode() & S_ISGID) != 0;
    }

    public boolean isSetuid() {
        return (mode() & S_ISUID) != 0;
    }

    public boolean isSocket() {
        return (mode() & S_IFMT) == S_IFSOCK;
    }

    public boolean isSticky() {
        return (mode() & S_ISVTX) != 0;
    }

    public boolean isSymlink() {
        return (mode() & S_IFMT) == S_IFLNK;
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
}
