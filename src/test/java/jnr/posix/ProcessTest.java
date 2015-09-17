package jnr.posix;

import jnr.posix.DefaultNativeRLimit;
import jnr.posix.DummyPOSIXHandler;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.RLimit;
import jnr.posix.util.Platform;
import jnr.constants.platform.RLIMIT;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessTest {


    private static POSIX posix;
    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void times() {
        long hz = posix.sysconf(Sysconf._SC_CLK_TCK);
        if (Platform.IS_MAC) {
            assertEquals("incorrect HZ value", 100L, hz);
        }
    }

    @Test
    public void getcwd() {
        String propCwd = System.getProperty("user.dir");
        assertEquals(propCwd, posix.getcwd());
    }

    @Test
    public void testGetRLimit() {
        if (Platform.IS_LINUX) {
            RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

            // These tests may fail depending on how the system they're being run on is configured.  Since they are
            // system-defined limits that may even differ based on UID, it's hard to find something universally true.
            // Limiting the number of processes a user can create is believed to be universally enabled since without it
            // the system would be susceptible to fork bombs.
            assertTrue("Bad soft limit for number of processes", rlim.rlimCur() > 0);
            assertTrue("Bad hard limit for number of processes", rlim.rlimMax() > 0);
        }
    }

    @Test
    public void testGetRLimitPreallocatedRlimit() {
        if (Platform.IS_LINUX) {
            RLimit rlim = new DefaultNativeRLimit(jnr.ffi.Runtime.getSystemRuntime());

            int result = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue(), rlim);
            assertEquals("getrlimit did not return 0", 0, result);

            // These tests may fail depending on how the system they're being run on is configured.  Since they are
            // system-defined limits that may even differ based on UID, it's hard to find something universally true.
            // Limiting the number of processes a user can create is believed to be universally enabled since without it
            // the system would be susceptible to fork bombs.
            assertTrue("Bad soft limit for number of processes", rlim.rlimCur() > 0);
            assertTrue("Bad hard limit for number of processes", rlim.rlimMax() > 0);
        }
    }

    @Test
    public void testGetRLimitPointer() {
        if (Platform.IS_LINUX) {
            Pointer rlim = jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().allocateDirect(8 * 2); // 2 longs.

            int result = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue(), rlim);
            assertEquals("getrlimit did not return 0", 0, result);

            // These tests may fail depending on how the system they're being run on is configured.  Since they are
            // system-defined limits that may even differ based on UID, it's hard to find something universally true.
            // Limiting the number of processes a user can create is believed to be universally enabled since without it
            // the system would be susceptible to fork bombs.
            assertTrue("Bad soft limit for number of processes", rlim.getLong(0) > 0);
            assertTrue("Bad hard limit for number of processes", rlim.getLong(8) > 0);
        }
    }

    @Test
    public void testSetRlimitLinux() {
        if (Platform.IS_LINUX) {
            RLimit originalRlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

            int result = posix.setrlimit(RLIMIT.RLIMIT_NPROC.intValue(), originalRlim.rlimCur() - 1, originalRlim.rlimMax() - 1);
            assertEquals("setrlimit did not return 0", 0, result);

            RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());
            assertEquals("soft limit didn't update", originalRlim.rlimCur() - 1, rlim.rlimCur());
            assertEquals("hard limit didn't update", originalRlim.rlimMax() - 1, rlim.rlimMax());
        }
    }

    @Test
    public void testSetRlimitPreallocatedLinux() {
        if (Platform.IS_LINUX) {
            RLimit originalRlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

            RLimit updatedRlim = new DefaultNativeRLimit(jnr.ffi.Runtime.getSystemRuntime());
            updatedRlim.init(originalRlim.rlimCur() - 1, originalRlim.rlimMax() - 1);

            int result = posix.setrlimit(RLIMIT.RLIMIT_NPROC.intValue(), updatedRlim);
            assertEquals("setrlimit did not return 0", 0, result);

            RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());
            assertEquals("soft limit didn't update", originalRlim.rlimCur() - 1, rlim.rlimCur());
            assertEquals("hard limit didn't update", originalRlim.rlimMax() - 1, rlim.rlimMax());
        }
    }

    @Test
    public void testSetRlimitPointerLinux() {
        if (Platform.IS_LINUX) {
            RLimit originalRlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

            Pointer updatedRlim = jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().allocateDirect(8 * 2); // 2 longs.
            updatedRlim.putLong(0, originalRlim.rlimCur() - 1);
            updatedRlim.putLong(8, originalRlim.rlimMax() - 1);

            int result = posix.setrlimit(RLIMIT.RLIMIT_NPROC.intValue(), updatedRlim);
            assertEquals("setrlimit did not return 0", 0, result);

            RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());
            assertEquals("soft limit didn't update", originalRlim.rlimCur() - 1, rlim.rlimCur());
            assertEquals("hard limit didn't update", originalRlim.rlimMax() - 1, rlim.rlimMax());
        }
    }
}
