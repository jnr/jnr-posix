package jnr.posix.junit;

import jnr.posix.FileStat;

public interface Modes {
    int RO = FileStat.S_IRUSR; // 0400
    int RA = FileStat.ALL_READ; // 0444
    int RW = FileStat.S_IRUSR | FileStat.S_IWUSR; // 0600
    int RWGO = FileStat.S_IRGRP | FileStat.S_IWGRP | FileStat.S_IROTH | FileStat.S_IWOTH; // 066
    int RWA = FileStat.ALL_READ | FileStat.ALL_WRITE; // 0666
    int RWXA = FileStat.ALL_READ | FileStat.ALL_WRITE | FileStat.S_IXUGO; // 0777
}
