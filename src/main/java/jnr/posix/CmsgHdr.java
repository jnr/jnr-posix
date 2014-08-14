package jnr.posix;

import java.nio.ByteBuffer;

/**
 * @author Bob McWhirter
 */
public interface CmsgHdr {

    void setLevel(int level);

    int getLevel();

    void setType(int type);

    int getType();

    void setData(ByteBuffer data);

    ByteBuffer getData();

    int getLen();

}
