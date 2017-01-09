package jnr.posix;

public final class OpenBSDTimespec extends Timespec {
    public final Signed64 ts_sec = new Signed64();
    public final SignedLong ts_nsec = new SignedLong();

    public OpenBSDTimespec(jnr.ffi.Runtime runtime) {
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
