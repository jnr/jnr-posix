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
}
