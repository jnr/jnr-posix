package jnr.posix;

import jnr.constants.platform.OpenFlags;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by headius on 5/31/14.
 */
public class IOTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void testOpenReadWrite() throws Throwable {
        File tmp = File.createTempFile("IOTest", "testOpen");
        int fd = posix.open(tmp.getPath(), OpenFlags.O_RDWR.intValue(), 0666);
        Assert.assertTrue(fd > 0);

        byte[] hello = "hello".getBytes();
        int written = posix.write(fd, hello, 5);
        Assert.assertEquals(5, written);

        byte[] buf = new byte[5];
        posix.lseek(fd, 0, 0); // no jnr-constants for SEEK_SET
        int read = posix.read(fd, buf, 5);
        Assert.assertEquals(5, read);
        Assert.assertArrayEquals(hello, buf);

        byte[] goodbye = "goodbye".getBytes();
        written = posix.pwrite(fd, goodbye, 7, 3);
        Assert.assertEquals(7, written);
        Assert.assertEquals(5, posix.lseek(fd, 0, 1)); // SEEK_CUR

        byte[] bye = new byte[3];
        read = posix.pread(fd, bye, 3, 7);
        Assert.assertEquals(3, read);
        Assert.assertEquals(5, posix.lseek(fd, 0, 1)); // SEEK_CUR
        Assert.assertArrayEquals("bye".getBytes(), bye);
    }

    @Test
    public void testPipe() throws Throwable {
        int[] fds = {0,0};
        int ret = posix.pipe(fds);
        Assert.assertTrue(ret >= 0);
        Assert.assertTrue(fds[0] > 0);
        Assert.assertTrue(fds[1] > 0);

        byte[] hello = "hello".getBytes();
        int written = posix.write(fds[1], hello, 5);
        Assert.assertEquals(5, written);

        byte[] buf = new byte[5];
        int read = posix.read(fds[0], buf, 5);
        Assert.assertEquals(5, read);
        Assert.assertArrayEquals(buf, hello);
    }
}
