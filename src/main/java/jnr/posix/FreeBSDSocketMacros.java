package jnr.posix;

import jnr.ffi.*;
import jnr.ffi.Runtime;

/**
 * @author Bob McWhirter
 */
public class FreeBSDSocketMacros implements SocketMacros {

    public static final FreeBSDSocketMacros INSTANCE = new FreeBSDSocketMacros();

    public int CMSG_ALIGN(int len) {
        int sizeof_size_t = Runtime.getSystemRuntime().findType(TypeAlias.size_t).size();
        return (len + sizeof_size_t - 1) & ~(sizeof_size_t - 1);
    }

    public int CMSG_SPACE(int l) {
        return CMSG_ALIGN(FreeBSDCmsgHdr.layout.size()) + CMSG_ALIGN(l);
    }

    public int CMSG_LEN(int l) {
        return CMSG_ALIGN( FreeBSDCmsgHdr.layout.size() ) + (l);
    }

    public Pointer CMSG_DATA(Pointer cmsg) {
        return cmsg.slice(CMSG_ALIGN(FreeBSDCmsgHdr.layout.size()));
    }
}
