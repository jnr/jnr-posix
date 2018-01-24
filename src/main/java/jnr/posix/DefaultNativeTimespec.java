package jnr.posix;

public final class DefaultNativeTimespec extends Timespec {
    public final SignedLong tv_sec = new SignedLong();
    public final SignedLong tv_nsec = new SignedLong();

    public DefaultNativeTimespec(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public void setTime(long[] timespec) {
        assert timespec.length == 2;
        tv_sec.set(timespec[0]);
        tv_nsec.set(timespec[1]);
    }

    public void sec(long sec) {
        this.tv_sec.set(sec);
    }

    public void nsec(long usec) {
        this.tv_nsec.set(usec);
    }

    public long sec() {
        return tv_sec.get();
    }

    public long nsec() {
        return tv_nsec.get();
    }
}
