package jnr.posix;

import jnr.constants.platform.Signal;
import jnr.posix.util.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class SignalTest {
    private static POSIX posix;
    private static POSIX javaPosix;
    
    @BeforeClass
    public static void setupClass() throws Exception {
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
            posix.signal(s, new SignalHandler() {
                public void handle(int signal) {
                    fired.set(true);
                }
            });

            posix.kill(posix.getpid(), s.intValue());
            waitUntilTrue(fired, 200);
            Assert.assertTrue(fired.get());
        }
    }
    
    @Test
    public void testJavaSignal() {
        if (!Platform.IS_WINDOWS) {
            Signal s = Signal.SIGHUP;
            final AtomicBoolean fired = new AtomicBoolean(false);
            javaPosix.signal(s, new SignalHandler() {
                public void handle(int signal) {
                    fired.set(true);
                }
            });

            // have to use native here; no abstraction for kill in pure Java
            // TODO: sun.misc.Signal.raise can be used to kill current pid
            posix.kill(posix.getpid(), s.intValue());

            waitUntilTrue(fired, 200);
            Assert.assertTrue(fired.get());
        }
    }
}
