package jnr.posix;

public final class DefaultNativeTimespec extends Timespec {
    public final SignedLong ts_sec = new SignedLong();
    public final SignedLong ts_nsec = new SignedLong();

    public DefaultNativeTimespec(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public void sec(long sec) {
        ts_sec.set(sec);
    }

    public void nsec(long nsec) {
        ts_nsec.set(nsec);
    }

    public long sec() {
        return ts_sec.get();
    }

    public long nsec() {
        return ts_nsec.get();
    }
}
