package jnr.posix;

import jnr.ffi.*;
import jnr.ffi.Runtime;

/**
 * @author Bob McWhirter
 */
public class LinuxSocketMacros implements SocketMacros {

    public static final LinuxSocketMacros INSTANCE = new LinuxSocketMacros();

    public int CMSG_ALIGN(int len) {
        int sizeof_size_t = Runtime.getSystemRuntime().findType(TypeAlias.size_t).size();
        return (len + sizeof_size_t - 1) & ~(sizeof_size_t - 1);
    }

    public int CMSG_SPACE(int l) {
        return CMSG_ALIGN(l) + CMSG_ALIGN(LinuxCmsgHdr.layout.size());
    }

    public int CMSG_LEN(int l) {
        return CMSG_ALIGN( LinuxCmsgHdr.layout.size() + l );
    }

    public Pointer CMSG_DATA(Pointer cmsg) {
        return cmsg.slice(CMSG_ALIGN(LinuxCmsgHdr.layout.size()));
    }
}
