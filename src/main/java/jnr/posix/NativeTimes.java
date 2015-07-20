package jnr.posix;

import jnr.constants.platform.Errno;
import jnr.ffi.LastError;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.StructLayout;

/**
 *
 */
public final class NativeTimes implements Times {
    static final class Layout extends StructLayout {
        public final clock_t tms_utime = new clock_t();
        public final clock_t tms_stime = new clock_t();
        public final clock_t tms_cutime = new clock_t();
        public final clock_t tms_cstime = new clock_t();

        Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }
    }

    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());
    final Pointer memory;

    static NativeTimes times(BaseNativePOSIX posix) {
        NativeTimes tms = new NativeTimes(posix);
        return posix.libc().times(tms) == -1 ? null : tms;
    }

    NativeTimes(NativePOSIX posix) {
        this.memory = Memory.allocate(posix.getRuntime(), layout.size());
    }

    public long utime() {
        return layout.tms_utime.get(memory);
    }

    public long stime() {
        return layout.tms_stime.get(memory);
    }

    public long cutime() {
        return layout.tms_cutime.get(memory);
    }

    public long cstime() {
        return layout.tms_cstime.get(memory);
    }
}
