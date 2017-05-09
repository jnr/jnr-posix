package jnr.posix;

/**
 * Linux-specific POSIX-like functions.
 */
public interface Linux extends POSIX {
    int ioprio_get(int which, int who);
    int ioprio_set(int which, int who, int ioprio);
}
