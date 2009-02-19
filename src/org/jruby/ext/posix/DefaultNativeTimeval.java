package org.jruby.ext.posix;

import com.sun.jna.NativeLong;

public final class DefaultNativeTimeval extends Timeval {
    public NativeLong tv_sec;
    public NativeLong tv_usec;

    public DefaultNativeTimeval() {}

    public void setTime(long[] timeval) {
        assert timeval.length == 2;
        tv_sec.setValue(timeval[0]);
        tv_usec.setValue(timeval[1]);
    }
}
