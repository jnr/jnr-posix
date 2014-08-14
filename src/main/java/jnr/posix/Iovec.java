package jnr.posix;

import java.nio.ByteBuffer;

/**
 * @author Bob McWhirter
 */
public interface Iovec {

    ByteBuffer get();
    void set(ByteBuffer buf);
}
