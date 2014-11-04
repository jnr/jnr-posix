package jnr.posix;

public final class OpenBSDTimeval extends Timeval {
    public final SignedLong tv_sec = new SignedLong();
    public final SignedLong tv_usec = new SignedLong();

    public OpenBSDTimeval(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public void setTime(long[] timeval) {
        assert timeval.length == 2;
        tv_sec.set(timeval[0]);
        tv_usec.set(timeval[1]);
    }
}
