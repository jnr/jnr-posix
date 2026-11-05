package jnr.posix;

import jnr.constants.platform.Errno;
import jnr.ffi.Platform;
import jnr.posix.junit.EnabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ClockTest {
    // These ids are the same on Linux and macOS.
    private static final int CLOCK_REALTIME = 0;
    private static final int INVALID_CLOCK = 0x7fff;

    private static POSIX posix;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    private static int clockMonotonic() {
        return Platform.getNativePlatform().getOS() == Platform.OS.DARWIN ? 6 : 1;
    }

    private static long nanos(Timespec ts) {
        return TimeUnit.SECONDS.toNanos(ts.sec()) + ts.nsec();
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void clockGettimeRealtime() {
        Timespec ts = posix.allocateTimespec();
        long before = System.currentTimeMillis();
        assertEquals(0, posix.clock_gettime(CLOCK_REALTIME, ts));
        long after = System.currentTimeMillis();

        assertTrue(ts.nsec() >= 0 && ts.nsec() < 1_000_000_000L, "nsec out of range: " + ts.nsec());
        long millis = TimeUnit.NANOSECONDS.toMillis(nanos(ts));
        assertTrue(millis >= before - 1 && millis <= after + 1,
                "CLOCK_REALTIME " + millis + " not within [" + before + ", " + after + "]");
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void clockGettimeMonotonic() throws InterruptedException {
        Timespec ts = posix.allocateTimespec();
        assertEquals(0, posix.clock_gettime(clockMonotonic(), ts));
        long first = nanos(ts);
        assertTrue(first > 0);

        Thread.sleep(10);

        assertEquals(0, posix.clock_gettime(clockMonotonic(), ts));
        assertTrue(nanos(ts) - first >= TimeUnit.MILLISECONDS.toNanos(10),
                "CLOCK_MONOTONIC advanced only " + (nanos(ts) - first) + "ns across a 10ms sleep");
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void clockGetres() {
        Timespec res = posix.allocateTimespec();
        assertEquals(0, posix.clock_getres(clockMonotonic(), res));
        assertEquals(0, res.sec());
        assertTrue(res.nsec() > 0 && res.nsec() <= 1_000_000L, "unexpected resolution: " + res.nsec() + "ns");
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void clockGettimeInvalidClock() {
        Timespec ts = posix.allocateTimespec();
        assertEquals(-1, posix.clock_gettime(INVALID_CLOCK, ts));
        assertEquals(Errno.EINVAL.intValue(), posix.errno());
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.WINDOWS)
    void clockGettimeWindows() {
        Timespec ts = posix.allocateTimespec();
        assertEquals(-1, posix.clock_gettime(CLOCK_REALTIME, ts));
        assertEquals(Errno.EOPNOTSUPP.intValue(), posix.errno());
    }
}
