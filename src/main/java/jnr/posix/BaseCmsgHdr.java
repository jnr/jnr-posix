package jnr.posix;

import jnr.ffi.Memory;
import jnr.ffi.Pointer;

import java.nio.ByteBuffer;

/**
 * @author Bob McWhirter
 */
abstract class BaseCmsgHdr implements CmsgHdr {

    protected final NativePOSIX posix;
    final Pointer memory;

    protected BaseCmsgHdr(NativePOSIX posix, Pointer memory) {
        this.posix = posix;
        this.memory = memory;
    }

    protected BaseCmsgHdr(NativePOSIX posix, Pointer memory, int totalLen) {
        this.posix = posix;
        this.memory = memory;
        setLen( totalLen );
    }

    public void setData(ByteBuffer data) {
        byte[] bytes = new byte[data.capacity() - data.position()];
        data.get(bytes);
        posix.socketMacros().CMSG_DATA(this.memory).put(0, bytes, 0, bytes.length);
    }

    public ByteBuffer getData() {
        int dataLen =  getLen() - posix.socketMacros().CMSG_LEN(0);
        if ( dataLen == 0 ) {
            return null;
        }
        byte[] bytes = new byte[dataLen];

        posix.socketMacros().CMSG_DATA(this.memory).get(0, bytes, 0, bytes.length);

        ByteBuffer buf = ByteBuffer.allocate(bytes.length);
        buf.put(bytes);
        buf.flip();
        return buf;
    }

    abstract void setLen(int len);

}
