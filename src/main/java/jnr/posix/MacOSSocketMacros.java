package jnr.posix;

import jnr.ffi.Pointer;

/**
 * @author Bob McWhirter
 */
public class MacOSSocketMacros implements SocketMacros {

    public static final SocketMacros INSTANCE = new MacOSSocketMacros();

    public int __DARWIN_ALIGN32(int x) {
        return ((x + 3) & ~3);
    }

    public int CMSG_SPACE(int l) {
        return __DARWIN_ALIGN32(MacOSCmsgHdr.layout.size()) + __DARWIN_ALIGN32(l);
    }

    public int CMSG_LEN(int l) {
        return (__DARWIN_ALIGN32(MacOSCmsgHdr.layout.size())) + (l);
    }

    public Pointer CMSG_DATA(Pointer cmsg) {
        return cmsg.slice(__DARWIN_ALIGN32(MacOSCmsgHdr.layout.size()));
    }
}
