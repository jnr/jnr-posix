package jnr.posix;

import jnr.constants.platform.PosixFadvise;

/**
 * Linux-specific POSIX-like functions.
 */
public interface Linux extends POSIX {
    int ioprio_get(int which, int who);
    int ioprio_set(int which, int who, int ioprio);
    int posix_fadvise(int fd, long offset, long len, PosixFadvise advise);
}
