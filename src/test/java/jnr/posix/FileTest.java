package jnr.posix;

import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void utimeTest() throws Throwable {
        File f = File.createTempFile("utime", null);
        long oldTime = posix.stat(f.getAbsolutePath()).mtime();
        Thread.sleep(2000);
        int rval = posix.utimes(f.getAbsolutePath(), null, null);
        assertEquals("utime did not return 0", 0, rval);
        long newTime = posix.stat(f.getAbsolutePath()).mtime();
        f.delete();
        assertTrue("mtime failed", newTime > oldTime);
    }

    @Test
    public void linkTest() throws Throwable {
        File f1 = File.createTempFile("utime", null);
        File f2 = new File(f1.getAbsolutePath() + "link");
        int rval = posix.link(f1.getAbsolutePath(), f2.getAbsolutePath());
        assertEquals("link did not return 0", 0, rval);
        assertTrue("Link was not created", f2.exists());
        f1.delete();
        f2.delete();
    }

    @Test
    public void mkdirRelativeTest() throws Throwable {
        File dir = new File("tmp");
        int rval = posix.mkdir(dir.getPath(), 0);
        assertEquals("mkdir did not return 0", 0, rval);
        assertTrue("Directory was not created", dir.exists());
        dir.delete();
    }

    @Test
    public void mkdirAbsoluteTest() throws Throwable {
        File dir = new File("tmp");
        int rval = posix.mkdir(dir.getAbsolutePath(), 0);
        assertEquals("mkdir did not return 0", 0, rval);
        assertTrue("Directory was not created", dir.exists());
        dir.delete();
    }
}
