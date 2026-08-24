package jnr.posix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class POSIXFactoryTest {
    @Test
    void testGetNativePOSIX() {
        POSIX posix = POSIXFactory.getNativePOSIX();
        assertTrue(posix.isNative());
    }

    @Test
    void testGetJavaPOSIX() {
        POSIX posix = POSIXFactory.getJavaPOSIX();
        assertFalse(posix.isNative());
    }
}
