package jnr.posix;

import jnr.constants.platform.Signal;
import jnr.ffi.Platform;
import jnr.posix.junit.DisabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignalTest {
    private static POSIX posix;
    private static POSIX javaPosix;
    
    @BeforeAll
    static void setupClass() {
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
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testBasicSignal() {
        Signal s = Signal.SIGHUP;
        final AtomicBoolean fired = new AtomicBoolean(false);
        posix.signal(s, signal -> fired.set(true));

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(fired, 200);
        assertTrue(fired.get());
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testSignalSurvivesGarbageCollection() {
        Signal s = Signal.SIGHUP;
        final AtomicBoolean fired = new AtomicBoolean(false);
        posix.signal(s, signal -> fired.set(true));

        System.gc();
        System.gc();

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(fired, 200);
        assertTrue(fired.get(), "signal handler should still fire after a GC run");
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testSignalOfDroppedInstanceSurvivesGarbageCollection() {
        // the handler stays installed for the rest of the JVM, so this uses a signal that no other test uses
        Signal s = Signal.SIGURG;
        final AtomicBoolean fired = new AtomicBoolean(false);
        POSIXFactory.getNativePOSIX(new DummyPOSIXHandler()).signal(s, signal -> fired.set(true));

        System.gc();
        System.gc();

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(fired, 1000);
        assertTrue(fired.get(), "signal handler should still fire after its POSIX instance is collected");
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testRestoreSignalHandlers() {
        Signal s = Signal.SIGWINCH;
        final AtomicBoolean original = new AtomicBoolean(false);
        javaPosix.signal(s, signal -> original.set(true));
        POSIX instance = POSIXFactory.getNativePOSIX(new DummyPOSIXHandler());
        final AtomicBoolean fired = new AtomicBoolean(false);
        instance.signal(s, signal -> fired.set(true));

        instance.restoreSignalHandlers();

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(original, 1000);
        assertTrue(original.get(), "the replaced handler should be put back");
        assertFalse(fired.get());
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testRestoreSignalHandlersAfterReplacingOwnHandler() {
        Signal s = Signal.SIGWINCH;
        final AtomicBoolean original = new AtomicBoolean(false);
        javaPosix.signal(s, signal -> original.set(true));
        POSIX instance = POSIXFactory.getNativePOSIX(new DummyPOSIXHandler());
        final AtomicBoolean firstFired = new AtomicBoolean(false);
        final AtomicBoolean secondFired = new AtomicBoolean(false);
        SignalHandler first = signal -> firstFired.set(true);
        instance.signal(s, first);
        assertSame(first, instance.signal(s, signal -> secondFired.set(true)));

        instance.restoreSignalHandlers();

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(original, 1000);
        assertTrue(original.get(), "the handler replaced by the first signal() call should be put back");
        assertFalse(firstFired.get());
        assertFalse(secondFired.get());
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testRestoreSignalHandlersInInstallationOrder() {
        Signal s = Signal.SIGWINCH;
        final AtomicBoolean original = new AtomicBoolean(false);
        javaPosix.signal(s, signal -> original.set(true));
        POSIX first = POSIXFactory.getNativePOSIX(new DummyPOSIXHandler());
        POSIX second = POSIXFactory.getNativePOSIX(new DummyPOSIXHandler());
        final AtomicBoolean firstFired = new AtomicBoolean(false);
        final AtomicBoolean secondFired = new AtomicBoolean(false);
        first.signal(s, signal -> firstFired.set(true));
        second.signal(s, signal -> secondFired.set(true));

        first.restoreSignalHandlers();

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(secondFired, 1000);
        assertTrue(secondFired.get(), "a handler installed later should stay installed");

        second.restoreSignalHandlers();

        posix.kill(posix.getpid(), s.intValue());
        waitUntilTrue(original, 1000);
        assertTrue(original.get(), "the handler replaced by the first instance should be put back");
        assertFalse(firstFired.get());
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testJavaSignal() {
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
