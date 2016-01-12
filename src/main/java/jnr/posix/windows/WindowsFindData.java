package jnr.posix.windows;

import jnr.ffi.*;

/**
 * Created by enebo on 9/20/2015.
 */
public class WindowsFindData extends CommonFileInformation {
    public static final int MAX_PATH = 260;

    final UnsignedLong dwFileAttributes;
    // FIXME: I have no idea why I could not include FileTime here but having it do its own layout seems to change
    // something.
    final UnsignedLong chigh;
    final UnsignedLong clow;
    final UnsignedLong ahigh;
    final UnsignedLong alow;
    final UnsignedLong uhigh;
    final UnsignedLong ulow;
    final UnsignedLong nFileSizeHigh;
    final UnsignedLong nFileSizeLow;
    final UnsignedLong dwReserved0;
    final UnsignedLong dwReserved1;
    final Padding cFileName;
    final Padding cAlternateFileName;

    public WindowsFindData(jnr.ffi.Runtime runtime) {
        super(runtime);

        dwFileAttributes = new UnsignedLong();
        clow = new UnsignedLong();
        chigh = new UnsignedLong();
        alow = new UnsignedLong();
        ahigh = new UnsignedLong();
        ulow = new UnsignedLong();
        uhigh = new UnsignedLong();
        nFileSizeHigh = new UnsignedLong();
        nFileSizeLow = new UnsignedLong();
        dwReserved0 = new UnsignedLong();
        dwReserved1 = new UnsignedLong();
        cFileName = new Padding(NativeType.UCHAR, MAX_PATH);
        cAlternateFileName = new Padding(NativeType.UCHAR, 14);
    }

    public HackyFileTime getCreationTime() {
        return new HackyFileTime(chigh, clow);
    }

    public HackyFileTime getLastAccessTime() {
        return new HackyFileTime(ahigh, alow);
    }

    public HackyFileTime getLastWriteTime() {
        return new HackyFileTime(uhigh, ulow);
    }

    public int getFileAttributes() {
        return dwFileAttributes.intValue();
    }

    public long getFileSizeHigh() {
        return nFileSizeHigh.longValue();
    }

    public long getFileSizeLow() {
        return nFileSizeLow.longValue();
    }
}
