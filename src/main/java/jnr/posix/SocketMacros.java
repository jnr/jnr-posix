package jnr.posix;

import jnr.ffi.Pointer;

/**
 * @author Bob McWhirter
 */
public interface SocketMacros {
    public int CMSG_SPACE(int l);
    public int CMSG_LEN(int l);
    public Pointer CMSG_DATA(Pointer cmsg);
}
