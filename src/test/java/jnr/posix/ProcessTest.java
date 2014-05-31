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

    @Test
    public void posix_spawn() throws Throwable {
        long pid = posix.posix_spawnp("ls", null, null, Arrays.asList("."), Arrays.asList("FOO=BAR"));

        assertTrue(pid > 0);

        Pipe pipe = Pipe.open();
        pipe.sink().configureBlocking(false);
        int fd1, fd2;

        fd1 = (Integer)FieldAccess.getProtectedFieldValue(Class.forName("sun.nio.ch.SourceChannelImpl"), "fdVal", pipe.source());
        fd2 = (Integer)FieldAccess.getProtectedFieldValue(Class.forName("sun.nio.ch.SinkChannelImpl"), "fdVal", pipe.sink());
        pid = posix.posix_spawnp("yes", Arrays.asList(SpawnFileAction.dup(fd2, 1), SpawnFileAction.close(fd1)), null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        posix.close(fd1);
        posix.waitpid(pid, null, 0);
    }
}
