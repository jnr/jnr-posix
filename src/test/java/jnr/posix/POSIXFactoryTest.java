package jnr.posix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class POSIXFactoryTest {
    @Test
    public void testGetNativePOSIX() throws Throwable {
        POSIX posix = POSIXFactory.getNativePOSIX();
        assertTrue(posix.isNative());
    }

    @Test
    public void testGetJavaPOSIX() throws Throwable {
        POSIX posix = POSIXFactory.getJavaPOSIX();
        assertFalse(posix.isNative());
    }
}
