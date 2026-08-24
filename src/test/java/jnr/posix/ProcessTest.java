package jnr.posix;

import jnr.constants.platform.RLIMIT;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import jnr.posix.junit.EnabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessTest {


    private static POSIX posix;
    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.DARWIN)
    void times() {
        long hz = posix.sysconf(Sysconf._SC_CLK_TCK);
        assertEquals(100L, hz, "incorrect HZ value");
    }

    @Test
    void getcwd() {
        String propCwd = System.getProperty("user.dir");
        assertEquals(propCwd, posix.getcwd());
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.LINUX)
    void testGetRLimit() {
        RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

        // These tests may fail depending on how the system they're being run on is configured.  Since they are
        // system-defined limits that may even differ based on UID, it's hard to find something universally true.
        // Limiting the number of processes a user can create is believed to be universally enabled since without it
        // the system would be susceptible to fork bombs.
        assertTrue(rlim.rlimCur() > 0, "Bad soft limit for number of processes");
        assertTrue(rlim.rlimMax() > 0, "Bad hard limit for number of processes");
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.LINUX)
    void testGetRLimitPreallocatedRlimit() {
        RLimit rlim = new DefaultNativeRLimit(jnr.ffi.Runtime.getSystemRuntime());

        int result = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue(), rlim);
        assertEquals(0, result, "getrlimit did not return 0");

        // These tests may fail depending on how the system they're being run on is configured.  Since they are
        // system-defined limits that may even differ based on UID, it's hard to find something universally true.
        // Limiting the number of processes a user can create is believed to be universally enabled since without it
        // the system would be susceptible to fork bombs.
        assertTrue(rlim.rlimCur() > 0, "Bad soft limit for number of processes");
        assertTrue(rlim.rlimMax() > 0, "Bad hard limit for number of processes");
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.LINUX)
    void testGetRLimitPointer() {
        Pointer rlim = jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().allocateDirect(8 * 2); // 2 longs.

        int result = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue(), rlim);
        assertEquals(0, result, "getrlimit did not return 0");

        // These tests may fail depending on how the system they're being run on is configured.  Since they are
        // system-defined limits that may even differ based on UID, it's hard to find something universally true.
        // Limiting the number of processes a user can create is believed to be universally enabled since without it
        // the system would be susceptible to fork bombs.
        assertTrue(rlim.getLong(0) > 0, "Bad soft limit for number of processes");
        assertTrue(rlim.getLong(8) > 0, "Bad hard limit for number of processes");
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.LINUX)
    void testSetRlimitLinux() {
        RLimit originalRlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

        int result = posix.setrlimit(RLIMIT.RLIMIT_NPROC.intValue(), originalRlim.rlimCur() - 1, originalRlim.rlimMax() - 1);
        assertEquals(0, result, "setrlimit did not return 0");

        RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());
        assertEquals(originalRlim.rlimCur() - 1, rlim.rlimCur(), "soft limit didn't update");
        assertEquals(originalRlim.rlimMax() - 1, rlim.rlimMax(), "hard limit didn't update");
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.LINUX)
    void testSetRlimitPreallocatedLinux() {
        RLimit originalRlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

        RLimit updatedRlim = new DefaultNativeRLimit(jnr.ffi.Runtime.getSystemRuntime());
        updatedRlim.init(originalRlim.rlimCur() - 1, originalRlim.rlimMax() - 1);

        int result = posix.setrlimit(RLIMIT.RLIMIT_NPROC.intValue(), updatedRlim);
        assertEquals(0, result, "setrlimit did not return 0");

        RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());
        assertEquals(originalRlim.rlimCur() - 1, rlim.rlimCur(), "soft limit didn't update");
        assertEquals(originalRlim.rlimMax() - 1, rlim.rlimMax(), "hard limit didn't update");
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.LINUX)
    void testSetRlimitPointerLinux() {
        RLimit originalRlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

        Pointer updatedRlim = jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().allocateDirect(8 * 2); // 2 longs.
        updatedRlim.putLong(0, originalRlim.rlimCur() - 1);
        updatedRlim.putLong(8, originalRlim.rlimMax() - 1);

        int result = posix.setrlimit(RLIMIT.RLIMIT_NPROC.intValue(), updatedRlim);
        assertEquals(0, result, "setrlimit did not return 0");

        RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());
        assertEquals(originalRlim.rlimCur() - 1, rlim.rlimCur(), "soft limit didn't update");
        assertEquals(originalRlim.rlimMax() - 1, rlim.rlimMax(), "hard limit didn't update");
    }
}
