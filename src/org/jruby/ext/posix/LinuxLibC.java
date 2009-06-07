package org.jruby.ext.posix;

import com.kenai.jaffl.annotations.In;
import com.kenai.jaffl.annotations.NulTerminate;
import com.kenai.jaffl.annotations.Out;
import com.kenai.jaffl.annotations.Transient;
import java.nio.ByteBuffer;

public interface LinuxLibC extends LibC {
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
