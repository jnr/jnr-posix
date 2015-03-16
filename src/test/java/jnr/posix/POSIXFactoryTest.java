package jnr.posix;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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
