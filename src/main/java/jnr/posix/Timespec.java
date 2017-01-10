package jnr.posix;

public interface Timespec {
    abstract public void sec(long sec);
    abstract public void nsec(long nsec);
    abstract public long sec();
    abstract public long nsec();
}
