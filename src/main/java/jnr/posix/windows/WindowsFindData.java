package jnr.posix.windows;

import jnr.ffi.*;
import jnr.ffi.Runtime;

/**
 * Created by enebo on 9/20/2015.
 */
public class WindowsFindData extends CommonFileInformation {
    public static final int MAX_PATH = 260;
    private class s8 extends jnr.ffi.Struct {
        private Signed8 signed8 = new Signed8();
        public s8(Runtime runtime) {
            super(runtime);
        }
    }
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
    final s8[] cFileName;
    final s8[] cAlternateFileName;

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
        cFileName = Struct.arrayOf(getRuntime(), s8.class, MAX_PATH);
        cAlternateFileName = Struct.arrayOf(getRuntime(), s8.class, 14);
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
