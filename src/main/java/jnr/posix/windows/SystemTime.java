package jnr.posix.windows;

import jnr.ffi.*;

/**
 * Created by enebo on 9/18/2015.
 */
public class SystemTime extends jnr.ffi.Struct {
    Unsigned16 wYear = new Unsigned16();
    Unsigned16 wMonth = new Unsigned16();
    Unsigned16 wDayOfWeek = new Unsigned16();
    Unsigned16 wDay = new Unsigned16();
    Unsigned16 wHour = new Unsigned16();
    Unsigned16 wMinute = new Unsigned16();
    Unsigned16 wSecond = new Unsigned16();
    Unsigned16 wMilliseconds = new Unsigned16();
    
    public SystemTime(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public java.lang.String toString() {
        return "" + wYear + "/" + wMonth + "/" + wDay + " " + wHour + ":" + wMinute + ":" + wSecond;
    }
}
