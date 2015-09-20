package jnr.posix.windows;

/**
 * BY_HANDLE_FILE_INFORMATION
 */
public class WindowsByHandleFileInformation extends CommonFileInformation {
    final Unsigned32 dwFileAttributes = new Unsigned32();
    // FIXME: I have no idea why I could not include FileTime here but having it do its own layout seems to change
    // something.
    final UnsignedLong chigh = new UnsignedLong();
    final UnsignedLong clow = new UnsignedLong();
    final UnsignedLong ahigh = new UnsignedLong();
    final UnsignedLong alow = new UnsignedLong();
    final UnsignedLong uhigh = new UnsignedLong();
    final UnsignedLong ulow = new UnsignedLong();
    final Unsigned32 dwVolumeSerialNumber = new Unsigned32();
    final Unsigned32 nFileSizeHigh = new Unsigned32();
    final Unsigned32 nFileSizeLow = new Unsigned32();
    final Unsigned32 nNumberOfLinks = new Unsigned32();
    final Unsigned32 nFileIndexHigh = new Unsigned32();
    final Unsigned32 nFileIndexLow = new Unsigned32();

    public WindowsByHandleFileInformation(jnr.ffi.Runtime runtime) {
        super(runtime);
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
        return nFileSizeHigh.intValue();
    }

    public long getFileSizeLow() {
        return nFileSizeLow.intValue();
    }
}
