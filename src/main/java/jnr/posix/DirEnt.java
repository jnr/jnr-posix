package jnr.posix;

/**
 * One entry of a directory stream, as returned by {@link POSIX#readdir(jnr.ffi.Pointer)}.
 *
 * The values are copied out of the platform's struct dirent, so they stay valid after the next call to readdir or
 * closedir.
 */
public interface DirEnt {
    // The d_type values are the same on every platform that has d_type.

    /** The file system didn't report a type, so it has to be found with lstat. */
    int DT_UNKNOWN = 0;
    int DT_FIFO = 1;
    int DT_CHR = 2;
    int DT_DIR = 4;
    int DT_BLK = 6;
    int DT_REG = 8;
    int DT_LNK = 10;
    int DT_SOCK = 12;

    /** The inode number. */
    long d_ino();

    /** The type of the entry, one of the DT_* constants. A symbolic link is {@link #DT_LNK}, not its target's type. */
    int d_type();

    /** The name of the entry, without its directory. */
    String d_name();
}
