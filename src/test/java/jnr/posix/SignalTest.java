package jnr.posix;

import jnr.constants.platform.Signal;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SignalTest {
    private static POSIX posix;
    private static POSIX javaPosix;
    
    @BeforeClass
    public static void setupClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
        javaPosix = new JavaPOSIX(new DummyPOSIXHandler());
    }
    
    @Test
    public void testBasicSignal() {
        Signal s = Signal.SIGHUP;
        final boolean[] fired = {false};
        posix.signal(s, new SignalHandler() {
            public void handle(int signal) {
                fired[0] = true;
            }
        });
        
        posix.kill(posix.getpid(), s.intValue());
        
        while (!fired[0]);
        Assert.assertTrue(fired[0]);
    }
    
    @Test
    public void testJavaSignal() {
        Signal s = Signal.SIGHUP;
        final boolean[] fired = {false};
        javaPosix.signal(s, new SignalHandler() {
            public void handle(int signal) {
                fired[0] = true;
            }
        });
        
        // have to use native here; no abstraction for kill in pure Java
        // TODO: sun.misc.Signal.raise can be used to kill current pid
        posix.kill(posix.getpid(), s.intValue());
        
        while (!fired[0]);
        Assert.assertTrue(fired[0]);
    }
}
