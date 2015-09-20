package jnr.posix.windows;

/**
 * WIN32_FILE_ATTRIBUTE_DATA
 */

public class WindowsFileInformation extends CommonFileInformation {

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

    public WindowsFileInformation(jnr.ffi.Runtime runtime) {
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
