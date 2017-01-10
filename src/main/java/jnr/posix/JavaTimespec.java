package jnr.posix;

import jnr.ffi.Runtime;

public class JavaTimespec implements Timespec {
    long sec;
    long nsec;

    public void sec(long sec) {
        this.sec = sec;
    }

    public void nsec(long nsec) {
        this.nsec = nsec;
    }

    public long sec() {
        return sec;
    }

    public long nsec() {
        return nsec;
    }
}
