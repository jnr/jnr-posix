package jnr.posix;

import java.nio.ByteBuffer;

/**
 * @author Bob McWhirter
 */
public interface MsgHdr {

    void setName(String name);
    String getName();

    void setIov(ByteBuffer[] buffers);
    ByteBuffer[] getIov();


    void setFlags(int flags);
    int getFlags();

    CmsgHdr allocateControl(int dataLength);
    CmsgHdr[] allocateControls(int[] dataLengths);

    CmsgHdr[] getControls();
    int getControlLen();
}
