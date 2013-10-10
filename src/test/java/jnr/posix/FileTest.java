package jnr.posix;

import jnr.constants.platform.Fcntl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import static org.junit.Assert.assertArrayEquals;
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
    
    @Test
    public void flockTest() throws Throwable {
        File tmp = File.createTempFile("flockTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        RandomAccessFile raf2 = new RandomAccessFile(tmp, "rw");
        FileChannel fc = raf.getChannel();
        FileChannel fc2 = raf2.getChannel();
        FileDescriptor FD = JavaLibCHelper.getDescriptorFromChannel(fc);
        FileDescriptor FD2 = JavaLibCHelper.getDescriptorFromChannel(fc2);
        int fd = JavaLibCHelper.getfdFromDescriptor(FD);
        int fd2 = JavaLibCHelper.getfdFromDescriptor(FD2);
        
        assertEquals(0, posix.flock(fd, 1)); // LOCK_SH
        assertEquals(0, posix.flock(fd, 8)); // LOCK_UN
        assertEquals(0, posix.flock(fd, 2)); // LOCK_EX
        assertEquals(-1, posix.flock(fd2, 2 | 4)); // LOCK_EX | LOCK_NB
        assertEquals(0, posix.flock(fd, 8)); // LOCK_UN
    }

    @Test
    public void dupTest() throws Throwable {
        File tmp = File.createTempFile("dupTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        FileChannel fileChannel = raf.getChannel();
        int fileDescriptor = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(fileChannel));

        byte[] outContent = "foo".getBytes();

        FileDescriptor newFileDescriptor = JavaLibCHelper.toFileDescriptor(posix.dup(fileDescriptor));

        new FileOutputStream(JavaLibCHelper.toFileDescriptor(fileDescriptor)).write(outContent);
        raf.seek(0);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(newFileDescriptor).read(inContent, 0, 3);

        assertArrayEquals(inContent, outContent);
    }

    @Test
    public void dup2Test() throws Throwable {
        File tmp = File.createTempFile("dupTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        int oldFd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(new RandomAccessFile(tmp, "rw").getChannel()));
        int newFd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(raf.getChannel()));

        byte[] outContent = "foo".getBytes();

        FileDescriptor newFileDescriptor = JavaLibCHelper.toFileDescriptor(posix.dup2(oldFd, newFd));

        new FileOutputStream(JavaLibCHelper.toFileDescriptor(oldFd)).write(outContent);
        raf.seek(0);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(newFileDescriptor).read(inContent, 0, 3);

        assertArrayEquals(inContent, outContent);
    }

    @Test
    public void fcntlDupfdTest() throws Throwable {
        File tmp = File.createTempFile("fcntlTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        int fd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(raf.getChannel()));

        byte[] outContent = "foo".getBytes();

        int newFd = posix.fcntl(fd, Fcntl.F_DUPFD);

        new FileOutputStream(JavaLibCHelper.toFileDescriptor(fd)).write(outContent);
        raf.seek(0);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(JavaLibCHelper.toFileDescriptor(newFd)).read(inContent, 0, 3);

        assertArrayEquals(inContent, outContent);
    }

    @Test
    public void fcntlDupfdWithArgTest() throws Throwable {
        File tmp = File.createTempFile("dupTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        int oldFd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(new RandomAccessFile(tmp, "rw").getChannel()));
        int newFd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(raf.getChannel()));

        byte[] outContent = "foo".getBytes();

        FileDescriptor newFileDescriptor = JavaLibCHelper.toFileDescriptor(posix.fcntl(oldFd, Fcntl.F_DUPFD, newFd));

        new FileOutputStream(JavaLibCHelper.toFileDescriptor(newFd)).write(outContent);
        raf.seek(0);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(newFileDescriptor).read(inContent, 0, 3);

        assertArrayEquals(inContent, outContent);
    }
}
