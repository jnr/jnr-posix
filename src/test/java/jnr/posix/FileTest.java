package jnr.posix;

import jnr.constants.platform.Access;
import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Errno;
import jnr.constants.platform.OpenFlags;
import jnr.posix.util.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
    public void futimeTest() throws Throwable {
        File f = File.createTempFile("jnr-posix-futime", "tmp");
        long oldTime = posix.stat(f.getAbsolutePath()).mtime();
        Thread.sleep(2000);
        int fd = posix.open(f.getAbsolutePath(), OpenFlags.O_RDWR.intValue(), 0666);
        int rval = posix.futimes(fd, null, null);
        assertEquals("futime did not return 0", 0, rval);
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
        int oldFd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(
                new RandomAccessFile(tmp, "rw").getChannel()));
        int newFd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(
                new RandomAccessFile(tmp, "rw").getChannel()));

        byte[] outContent = "foo".getBytes();

        int dupFd = posix.fcntl(oldFd, Fcntl.F_DUPFD, newFd);

        new FileOutputStream(JavaLibCHelper.toFileDescriptor(newFd)).write(outContent);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(JavaLibCHelper.toFileDescriptor(dupFd)).read(inContent, 0, 3);

        assertTrue(dupFd > newFd);
        assertArrayEquals(inContent, outContent);
    }

    @Test
    public void closeTest() throws Throwable {
        File tmp = File.createTempFile("closeTest", "tmp");
        int fd = JavaLibCHelper.getfdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(new RandomAccessFile(tmp, "rw").getChannel()));

        int result;

        result = posix.close(fd);
        assertEquals(0, result);

        result = posix.close(fd);
        assertEquals(-1, result);

        assertEquals(Errno.EBADF.intValue(), posix.errno());
    }

    @Test
    public void unlinkTestNonWindows() throws Throwable {
        if (! Platform.IS_WINDOWS) {
            File tmp = File.createTempFile("unlinkTest", "tmp");
            RandomAccessFile raf = new RandomAccessFile(tmp, "rw");

            raf.write("hello".getBytes());

            int res = posix.unlink(tmp.getCanonicalPath());

            assertEquals(0, res);
            assertFalse(tmp.exists());

            raf.write("world".getBytes());
            raf.seek(0);

            byte[] actual = new byte[10];
            raf.read(actual);

            assertArrayEquals("helloworld".getBytes(), actual);
        }
    }

    @Test
    public void unlinkTestWindows() throws Throwable {
        if (Platform.IS_WINDOWS) {
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

    @Test
    public void openTest() throws Throwable {
        int fd = posix.open("pom.xml", 0, 0666);

        assertNotEquals(-1, fd);

        int result = posix.close(fd);
        assertEquals(0, result);

        result = posix.close(fd);
        assertEquals(-1, result);
    }

    @Test
    public void writeTest() throws Throwable {
        String str = "To thine own self be true";
        File tmp = File.createTempFile("writeTest", "tmp");
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        int fd = posix.open(tmp.getAbsolutePath(), 1, 066);
        posix.write(fd, buffer, str.length());
        posix.close(fd);

        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        assertEquals(raf.readLine(), new String(buffer.array()));
        posix.unlink(tmp.getAbsolutePath());
    }

    @Test
    public void pwriteTest() throws Throwable {
        String str = "Now is the winter of our discontent";
        File tmp = File.createTempFile("pwriteTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        raf.write(str.getBytes());
        raf.close();

        String str2 = "summer";
        ByteBuffer buffer = ByteBuffer.wrap(str2.getBytes());

        int fd = posix.open(tmp.getAbsolutePath(), 1, 066);
        posix.pwrite(fd, buffer, str2.length(), 11);
        posix.close(fd);

        raf = new RandomAccessFile(tmp, "r");
        assertEquals(raf.readLine(), "Now is the summer of our discontent");
        posix.unlink(tmp.getAbsolutePath());
    }

    @Test
    public void truncateTest() throws Throwable {
        String str = "Beware the Jabberwock, my son!";
        File tmp = File.createTempFile("truncateTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        raf.write(str.getBytes());
        raf.close();

        int shorterLength = str.length() - 10;
        int longerLength = shorterLength * 2;

        // Truncate should make a file shorter if the new length is less than the old length.
        posix.truncate(tmp.getAbsolutePath(), shorterLength);
        assertEquals(shorterLength, tmp.length());

        // Truncate should extend a file if the new length is greater than the old length.
        posix.truncate(tmp.getAbsolutePath(), longerLength);
        assertEquals(longerLength, tmp.length());
    }

    @Test
    public void ftruncateTest() throws Throwable {
        String str = "Beware the Jabberwock, my son!";
        File tmp = File.createTempFile("ftruncateTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        raf.write(str.getBytes());
        raf.close();

        int fd = posix.open(tmp.getAbsolutePath(), OpenFlags.O_RDWR.intValue(), 0666);
        posix.ftruncate(fd, 21);
        posix.lseek(fd, 0, 0);
        byte[] buf = new byte[21];
        int read = posix.read(fd, buf, 31);
        assertEquals(21, read);
        assertArrayEquals(buf, "Beware the Jabberwock".getBytes());
    }

    @Test
    public void fcntlTest() throws Throwable {
        int[] fds = new int[2];
        int ret = posix.pipe(fds);
        assertEquals(0, ret);
        int flags = posix.fcntlInt(fds[0], Fcntl.F_GETFD, 0);
        posix.fcntlInt(fds[0], Fcntl.F_SETFD, flags | 1); // FD_CLOEXEC
        assertEquals(1, posix.fcntlInt(fds[0], Fcntl.F_GETFD, 0));
    }

    @Test
    public void fchmodTest() throws IOException {
        File tmp = File.createTempFile("jnr-posix-chmod-test", "tmp");
        int fd = posix.open(tmp.getAbsolutePath(), OpenFlags.O_RDWR.intValue(), 0600);

        assertEquals("chmod: ", 0, posix.fchmod(fd, 0));
        assertEquals("chmod: ", 0, posix.fchmod(fd, 0777));
        tmp.delete();
    }


    @Test
    public void renameTest() throws IOException {
        File oldFile = File.createTempFile("jnr-posix-rename-test", "tmp");
        File newFile = new File(oldFile.getParent() + File.separatorChar + "jnr-posix-rename-test-new");

        assertTrue(oldFile.exists());
        assertFalse(newFile.exists());

        posix.rename(oldFile.getCanonicalPath(), newFile.getCanonicalPath());

        assertFalse(oldFile.exists());
        assertTrue(newFile.exists());

        newFile.delete();
    }

    @Test
    public void accessTest() throws IOException {
        File tmp = File.createTempFile("jnr-posix-access-test", "tmp");

        jnr.constants.platform.Access.F_OK.intValue();

        // Set permissions to read-only and verify we don't have permissions to write.
        posix.chmod(tmp.getCanonicalPath(), 0400);
        assertEquals("access: ", -1, posix.access(tmp.getCanonicalPath(), Access.W_OK.intValue()));
        assertEquals("access: ", 0, posix.access(tmp.getCanonicalPath(), Access.R_OK.intValue()));

        // Reset the permissions to read-wrinte and verify we now have permissions to write.
        posix.chmod(tmp.getCanonicalPath(), 0600);
        assertEquals("access: ", 0, posix.access(tmp.getCanonicalPath(), Access.W_OK.intValue()));
        assertEquals("access: ", 0, posix.access(tmp.getCanonicalPath(), Access.R_OK.intValue()));

        // F_OK just checks the file exists and should pass.
        assertEquals("access: ", 0, posix.access(tmp.getCanonicalPath(), Access.F_OK.intValue()));
    }

    @Test
    public void readlinkTest() throws IOException {
        File file = File.createTempFile("jnr-posix-readlink-test", "tmp");
        File link = new File(file.getAbsolutePath() + "link");

        posix.symlink(file.getAbsolutePath(), link.getAbsolutePath());

        byte[] buffer = new byte[file.getAbsolutePath().length()];
        assertEquals(buffer.length, posix.readlink(link.getAbsolutePath(), buffer, buffer.length));

        assertArrayEquals(buffer, file.getAbsolutePath().getBytes());

        link.delete();
        file.delete();
    }

    @Test
    public void readlinkByteBufferTest() throws IOException {
        File file = File.createTempFile("jnr-posix-readlink-test", "tmp");
        File link = new File(file.getAbsolutePath() + "link");

        posix.symlink(file.getAbsolutePath(), link.getAbsolutePath());

        ByteBuffer buffer = ByteBuffer.allocate(file.getAbsolutePath().length());
        assertEquals(buffer.capacity(), posix.readlink(link.getAbsolutePath(), buffer, buffer.capacity()));

        assertArrayEquals(buffer.array(), file.getAbsolutePath().getBytes());

        link.delete();
        file.delete();
    }
}
