package jnr.posix;

import jnr.constants.platform.Signal;
import jnr.posix.util.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignalTest {
    private static POSIX posix;
    private static POSIX javaPosix;
    
    @BeforeAll
    public static void setupClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
        javaPosix = new JavaPOSIX(new DummyPOSIXHandler());
    }
    
    private static void waitUntilTrue(AtomicBoolean var, long maxWait) {
        long start = System.currentTimeMillis();
        while (!var.get() && (System.currentTimeMillis() - start) < maxWait) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    @Test
    public void testBasicSignal() {
        if (!Platform.IS_WINDOWS) {
            Signal s = Signal.SIGHUP;
            final AtomicBoolean fired = new AtomicBoolean(false);
            posix.signal(s, signal -> fired.set(true));

            posix.kill(posix.getpid(), s.intValue());
            waitUntilTrue(fired, 200);
            assertTrue(fired.get());
        }
    }

    @Test
    public void testJavaSignal() {
        if (!Platform.IS_WINDOWS) {
            Signal s = Signal.SIGHUP;
            final AtomicBoolean fired = new AtomicBoolean(false);
            SignalHandler previousHandler = javaPosix.signal(s, signal -> fired.set(true));

            assertNotNull(previousHandler);

            // have to use native here; no abstraction for kill in pure Java
            // TODO: sun.misc.Signal.raise can be used to kill current pid
            posix.kill(posix.getpid(), s.intValue());

            waitUntilTrue(fired, 200);
            assertTrue(fired.get());
        }
    }
}
