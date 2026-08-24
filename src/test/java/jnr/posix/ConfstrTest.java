package jnr.posix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.ByteBuffer;
import jnr.constants.platform.Confstr;
import jnr.constants.platform.Errno;
import jnr.posix.util.Platform;

public class ConfstrTest {
    private static POSIX posix;
    @BeforeAll
    public static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void confstr() {
        if (Platform.IS_WINDOWS) {
            int len = posix.confstr(Confstr._CS_PATH, null, 0);
            assertEquals(-1, len);
            assertEquals(Errno.EOPNOTSUPP.intValue(), posix.errno());
        } else {
            int len = posix.confstr(Confstr._CS_PATH, null, 0);
            assertTrue(len > 0, "bad strlen");

            ByteBuffer buf = ByteBuffer.allocate(len);
            posix.confstr(Confstr._CS_PATH, buf, len);
            String str = new String(buf.array());
            assertFalse(str.isEmpty(), "CS_PATH is blank");
        }
    }
}
