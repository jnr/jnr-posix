package jnr.posix;

import com.kenai.jffi.Platform;
import jnr.constants.platform.Sysconf;
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
}
