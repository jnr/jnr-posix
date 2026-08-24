package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DTableSizeTest {
    @Test
    void testGetDtableSize() {
        if (!Platform.IS_WINDOWS) { // FIXME: I have seen window's impl so this can be impld
            POSIX posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
            assumeTrue(posix.isNative(), "getdtablesize only supported on native posix");

            int dtablesize = posix.getdtablesize();
            assertNotEquals(-1, dtablesize);
        }
    }
}
