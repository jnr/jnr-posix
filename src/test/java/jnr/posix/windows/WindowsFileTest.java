package jnr.posix.windows;

import java.io.File;
import java.io.RandomAccessFile;
import jnr.posix.DummyPOSIXHandler;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.util.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WindowsFileTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void unlinkTestWindows() throws Throwable {
        File tmp = File.createTempFile("unlinkTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");

        raf.write("hello".getBytes());

        // Windows won't allow you to delete open files, so we must
        // close the handle before trying to delete it.  Unfortunately,
        // this also means we're unable to write to the handle afterwards
        // as we do with the non-Windows test.
        raf.close();

        int res = posix.unlink(tmp.getCanonicalPath());

        assertEquals(0, res);
        assertFalse(tmp.exists());
    }
}
