package jnr.posix;

import jnr.ffi.annotations.In;
import jnr.ffi.annotations.NulTerminate;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;
import java.nio.ByteBuffer;

public interface LinuxLibC extends UnixLibC {
    public int __fxstat(int version, int fd, @Out @Transient FileStat stat);
    public int __lxstat(int version, CharSequence path, @Out @Transient FileStat stat);
    public int __lxstat(int version, @NulTerminate @In ByteBuffer path, @Out @Transient FileStat stat);
    public int __xstat(int version, CharSequence path, @Out @Transient FileStat stat);
    public int __xstat(int version, @NulTerminate @In ByteBuffer path, @Out @Transient FileStat stat);
    public int __fxstat64(int version, int fd, @Out @Transient FileStat stat);
    public int __lxstat64(int version, CharSequence path, @Out @Transient FileStat stat);
    public int __lxstat64(int version, @NulTerminate @In ByteBuffer path, @Out @Transient FileStat stat);
    public int __xstat64(int version, CharSequence path, @Out @Transient FileStat stat);
    public int __xstat64(int version, @NulTerminate @In ByteBuffer path, @Out @Transient FileStat stat);
}
