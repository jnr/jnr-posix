package jnr.posix.windows;

import jnr.posix.FileTime;

import static jnr.posix.FileStat.*;

/**
 * Shared logic between by handle and file path FILE_INFORMATION.
 */
public abstract class CommonFileInformation extends jnr.ffi.Struct {
    public static int FILE_ATTRIBUTE_READONLY  = 0x01;
    public static int FILE_ATTRIBUTE_DIRECTORY = 0x10;

    public class HackyFileTime {
        private final UnsignedLong dwHighDateTime;
        private final UnsignedLong dwLowDateTime;

        public HackyFileTime(UnsignedLong high, UnsignedLong low) {
            this.dwHighDateTime = high;
            this.dwLowDateTime = low;
        }

        public long getLowDateTime() {
            return dwLowDateTime.longValue();
        }

        public long getHighDateTime() {
            return dwHighDateTime.longValue();
        }

        public long getLongValue() {
            return (getHighDateTime() & 0xFFFFFFFFL) << 32 | (getLowDateTime() & 0xFFFFFFFFL);
        }
    }


    protected CommonFileInformation(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public abstract int getFileAttributes();
    public abstract HackyFileTime getCreationTime();
    public abstract HackyFileTime getLastAccessTime();
    public abstract HackyFileTime getLastWriteTime();
    public abstract long getFileSizeHigh();
    public abstract long getFileSizeLow();

    public int getMode(java.lang.String path) {
        int attr = getFileAttributes();
        int mode = S_IRUSR;

        if ((attr & FILE_ATTRIBUTE_READONLY) == 0) {
            mode |= S_IWUSR;

        }
        mode |= (attr & FILE_ATTRIBUTE_DIRECTORY) != 0 ? (S_IFDIR | S_IXUSR) : S_IFREG;

        path = path.toLowerCase();
        if (path != null && (mode & S_IFREG) != 0 &&
                (path.endsWith(".bat") || path.endsWith(".cmd") || path.endsWith(".com") || path.endsWith(".exe"))) {
            mode |= S_IXUSR;
        }

        mode |= (mode & 0700) >> 3;
        mode |= (mode & 0700) >> 6;

        return mode;
    }

    public long getLastWriteTimeMicroseconds() {
        return asMicroSeconds(getLastWriteTime().getLongValue()) / MICROSECONDS;
    }

    public long getLastAccessTimeMicroseconds() {
        return asMicroSeconds(getLastAccessTime().getLongValue()) / MICROSECONDS;
    }

    public long getCreationTimeMicroseconds() {
        return asMicroSeconds(getCreationTime().getLongValue()) / MICROSECONDS;
    }

    public long getFileSize() {
        return (getFileSizeHigh() << 32) | getFileSizeLow();
    }

    // FIXME: I used same equation in C to get number.  I did something wrong with the math here in Java
    //private static final int HOURS = 24;
    //private static final int MINUTES = 60;
    // private static final int SECONDS = 60;
    private static final int MICROSECONDS = 1000 * 1000;
    // on number of days a year: https://imicrothinking.wordpress.com/tag/365-2425-days/
    private static final double DAYS_BETWEEN_WINDOWS_AND_UNIX = (1970 - 1601) * 365.2425;
    private static final long MICROSECONDS_TO_UNIX_EPOCH_FROM_WINDOWS = 11644473600000000L;
           // (long) (DAYS_BETWEEN_WINDOWS_AND_UNIX * HOURS * SECONDS * MINUTES * MICROSECONDS);

    private long asMicroSeconds(long windowsNanosecondTime) {
        return (windowsNanosecondTime / 10) - MICROSECONDS_TO_UNIX_EPOCH_FROM_WINDOWS;
    }

    public static long asNanoSeconds(long seconds) {
        return (seconds * 1000 + MICROSECONDS_TO_UNIX_EPOCH_FROM_WINDOWS) * 10;
    }
}
