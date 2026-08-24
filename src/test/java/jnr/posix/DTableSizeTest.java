package jnr.posix;

import jnr.ffi.Platform;
import jnr.posix.junit.DisabledOnJnrOs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DTableSizeTest {
    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS) // FIXME: I have seen window's impl so this can be impld
    void testGetDtableSize() {
        POSIX posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
        assumeTrue(posix.isNative(), "getdtablesize only supported on native posix");

        int dtablesize = posix.getdtablesize();
        assertNotEquals(-1, dtablesize);
    }
}
