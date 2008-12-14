package org.jruby.ext.posix;

import com.sun.jna.NativeLong;

public final class DefaultNativeTimeval extends NativeTimeval {
    public NativeLong tv_sec;
    public NativeLong tv_usec;

    public DefaultNativeTimeval() {}

    public void setMicroseconds(long time) {
        tv_sec.setValue(time / 1000);
        tv_usec.setValue(time % 1000);
    }
}
