package jnr.posix;

/**
 * Represents the additional nsec resolution on the stat struct in Linux 2.6+.
 */
public interface NanosecondFileStat extends FileStat {
    long aTimeNanoSecs();

    long cTimeNanoSecs();

    long mTimeNanoSecs();
}
