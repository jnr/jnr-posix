package jnr.posix.windows;

import jnr.ffi.*;

/**
 * WIN32_FIND_DATA.  For use with FindFirstFileW and friends (since this is for W methods
 * the filename fields are wchar_t (or on windows usigned short) in width - TCHAR is an ambiguously
 * sized type depending on which variant of method calls it).
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
        // This is epically large but any paths with //?/ can get a long name.  Also even if you do not
        // and depend on MAX_PATH (original constant this struct is supposedly defined on) then you can
        // get overflows from FindFirstFileW on reparse points.  So we will waste memory on allocation
        // to avoid the potential for buffer overflows.
        // This number specifically is the actual physical limit of a file size in NTFS.  So although this
        // the functions using this struct cannot handle something this long the internet seems to think
        // it is possible to get these long values copied into this field.
        cFileName = new Padding(NativeType.USHORT, 32767);
        cAlternateFileName = new Padding(NativeType.USHORT, 14);
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
