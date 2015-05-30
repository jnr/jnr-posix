package jnr.posix;

import com.kenai.jffi.Platform;
import jnr.constants.platform.RLIMIT;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.posix.util.FieldAccess;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.util.Arrays;
import java.util.Collections;

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
        if (Platform.getPlatform().getOS() == Platform.OS.DARWIN) {
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
        RLimit rlim = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue());

        // These tests may fail depending on how the system they're being run on is configured.  Since they are
        // system-defined limits that may even differ based on UID, it's hard to find something universally true.
        // Limiting the number of processes a user can create is believed to be universally enabled since without it
        // the system would be susceptible to fork bombs.
        assertTrue("Bad soft limit for number of processes", rlim.getCurrent() > 0);
        assertTrue("Bad hard limit for number of processes", rlim.getMax() > 0);
    }

    @Test
    public void testGetRLimitPreallocatedRlimit() {
        RLimit rlim = new DefaultNativeRLimit(jnr.ffi.Runtime.getSystemRuntime());

        int result = posix.getrlimit(RLIMIT.RLIMIT_NPROC.intValue(), rlim);
        assertEquals("getrlimit did not return 0", 0, result);

        // These tests may fail depending on how the system they're being run on is configured.  Since they are
        // system-defined limits that may even differ based on UID, it's hard to find something universally true.
        // Limiting the number of processes a user can create is believed to be universally enabled since without it
        // the system would be susceptible to fork bombs.
        assertTrue("Bad soft limit for number of processes", rlim.getCurrent() > 0);
        assertTrue("Bad hard limit for number of processes", rlim.getMax() > 0);
    }

    @Test
    public void testGetRLimitPointer() {
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
