package jnr.posix;

import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;
import jnr.ffi.annotations.Version;

public interface FreeBSDLibC extends UnixLibC {
    @Version("FBSD_1.0")
    int fstat(int fd, @Out @Transient FileStat stat);
    @Version("FBSD_1.0")
    int fstat64(int fd, @Out @Transient FileStat stat);
    @Version("FBSD_1.0")
    int lstat(CharSequence path, @Out @Transient FileStat stat);
    @Version("FBSD_1.0")
    int lstat64(CharSequence path, @Out @Transient FileStat stat);
    @Version("FBSD_1.0")
    int stat(CharSequence path, @Out @Transient FileStat stat);
    @Version("FBSD_1.0")
    int stat64(CharSequence path, @Out @Transient FileStat stat);
}
