package jnr.posix;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.ByteBuffer;
import jnr.constants.platform.Confstr;
import jnr.constants.platform.Errno;
import jnr.posix.util.Platform;

public class ConfstrTest {
    private static POSIX posix;
    @BeforeClass
    public static void setUpClass() throws Exception {
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
            assertTrue("bad strlen", len > 0);

            ByteBuffer buf = ByteBuffer.allocate(len);
            posix.confstr(Confstr._CS_PATH, buf, len);
            String str = new String(buf.array());
            assertTrue("CS_PATH is blank", str.length() > 0);
        }
    }
}
