package jnr.posix;


public interface Times {
    public abstract long utime();
    public abstract long stime();
    public abstract long cutime();
    public abstract long cstime();
}
