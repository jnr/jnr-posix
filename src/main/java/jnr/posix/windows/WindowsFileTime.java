package jnr.posix.windows;

/**
 * FILETIME
 */
public class WindowsFileTime extends jnr.ffi.Struct {
    final Unsigned32 lowDateTime = new Unsigned32();
    final Unsigned32 highDateTime = new Unsigned32();

    public WindowsFileTime(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public int getLowDateTime() {
        return lowDateTime.intValue();
    }

    public int getHighDateTime() {
        return highDateTime.intValue();
    }

    public long getLongValue() {
        return getHighDateTime() << 32 + getLowDateTime();
    }
}
