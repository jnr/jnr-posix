package jnr.posix;

import jnr.ffi.Platform;
import jnr.posix.junit.DisabledOnJnrOs;
import jnr.posix.junit.EnabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.ByteBuffer;
import jnr.constants.platform.Confstr;
import jnr.constants.platform.Errno;

class ConfstrTest {
    private static POSIX posix;
    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.WINDOWS)
    void confstrWindows() {
        int len = posix.confstr(Confstr._CS_PATH, null, 0);
        assertEquals(-1, len);
        assertEquals(Errno.EOPNOTSUPP.intValue(), posix.errno());
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void confstr() {
        int len = posix.confstr(Confstr._CS_PATH, null, 0);
        assertTrue(len > 0, "bad strlen");

        ByteBuffer buf = ByteBuffer.allocate(len);
        posix.confstr(Confstr._CS_PATH, buf, len);
        String str = new String(buf.array());
        assertFalse(str.isEmpty(), "CS_PATH is blank");
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void confstrLongLength() {
        long len = posix.confstr(Confstr._CS_PATH, null, 0L);
        assertEquals((long) posix.confstr(Confstr._CS_PATH, null, 0), len);

        ByteBuffer buf = ByteBuffer.allocate((int) len);
        assertEquals(len, posix.confstr(Confstr._CS_PATH, buf, len));
        assertFalse(new String(buf.array()).isEmpty(), "CS_PATH is blank");
    }
}
