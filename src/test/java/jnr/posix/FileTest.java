package jnr.posix;

import jnr.constants.platform.Access;
import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Errno;
import jnr.constants.platform.OpenFlags;
import jnr.ffi.Pointer;
import jnr.posix.util.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

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
    public void utimesTest() throws Throwable {
        // FIXME: On Windows this is working but providing wrong numbers and therefore getting wrong results.
        if (!Platform.IS_WINDOWS) {
            File f = File.createTempFile("utimes", null);

            int rval = posix.utimes(f.getAbsolutePath(), new long[]{800, 200}, new long[]{900, 300});
            assertEquals("utimes did not return 0", 0, rval);

            FileStat stat = posix.stat(f.getAbsolutePath());

            assertEquals("atime seconds failed", 800, stat.atime());
            assertEquals("mtime seconds failed", 900, stat.mtime());

            // The nano secs part is available in other stat implementations. We really just want to verify that the
            // nsec portion of the timeval is passed through to the POSIX call.
            // Mac seems to fail this test sporadically.
            if (stat instanceof NanosecondFileStat && !Platform.IS_MAC) {
                NanosecondFileStat linuxStat = (NanosecondFileStat) stat;

                assertEquals("atime useconds failed", 200000, linuxStat.aTimeNanoSecs());
                assertEquals("mtime useconds failed", 300000, linuxStat.mTimeNanoSecs());
            }

            f.delete();
        }
    }

    @Test
    public void utimesDefaultValuesTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            File f = File.createTempFile("utimes", null);

            long oldTime = posix.stat(f.getAbsolutePath()).mtime();
            Thread.sleep(2000);

            int rval = posix.utimes(f.getAbsolutePath(), null, null);
            assertEquals("utimes did not return 0", 0, rval);

            FileStat stat = posix.stat(f.getAbsolutePath());

            assertTrue("atime failed", stat.atime() > oldTime);
            assertTrue("mtime failed", stat.mtime() > oldTime);

            f.delete();
        }
    }

    @Test
    public void utimesPointerTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            File f = File.createTempFile("utimes", null);

            Pointer times = jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().allocateDirect(8 * 4); // long[2][2] == 4 longs.
            times.putLong(0, 800);
            times.putLong(8, 200);
            times.putLong(16, 900);
            times.putLong(24, 300);

            int rval = posix.utimes(f.getAbsolutePath(), times);
            assertEquals("utimes did not return 0", 0, rval);

            FileStat stat = posix.stat(f.getAbsolutePath());

            assertEquals("atime seconds failed", 800, stat.atime());
            assertEquals("mtime seconds failed", 900, stat.mtime());

            // The nano secs part is available in other stat implementations.  We use Linux x86_64 because it's
            // representative.  We really just want to verify that the usec portion of the timeval is passed throug
            // to the POSIX call.
            if (hasNanosecondPrecision(stat)) {
                NanosecondFileStat linuxStat = (NanosecondFileStat) stat;

//                assertEquals("atime useconds failed", 200000, linuxStat.aTimeNanoSecs());
                assertEquals("mtime useconds failed", 300000, linuxStat.mTimeNanoSecs());
            }

            f.delete();
        }
    }

    @Test
    public void utimensatAbsolutePath() throws Throwable {
        File file = File.createTempFile("utimensat", null);
        utimensat(file, 0);
    }

    @Test
    public void utimensatRelativePath() throws Throwable {
        String path = "utimensat";
        File file = new File(path);
        file.createNewFile();
        int parentFd = posix.open(".", OpenFlags.O_DIRECTORY.intValue(), 0444);
        utimensat(file, parentFd);
    }

    @Test
    public void futimens() throws Throwable {
        File file = File.createTempFile("futimens", null);
        FileStat fileStat = posix.stat(file.getPath());
        if (!hasNanosecondPrecision(fileStat)) {
            file.delete();
            return;
        }

        long atimeSeconds = fileStat.atime()+1;
        long mtimeSeconds = fileStat.mtime()-1;
        long atimeNanoSeconds = 123456789;
        long mtimeNanoSeconds = 135;
        int fd = posix.open(file.getAbsolutePath(), OpenFlags.O_RDWR.intValue(), 0444);
        posix.futimens(fd,
                new long[] {atimeSeconds, atimeNanoSeconds},
                new long[] {mtimeSeconds, mtimeNanoSeconds});
        assertStatNanoSecond(file, atimeSeconds, atimeNanoSeconds, mtimeSeconds, mtimeNanoSeconds);
    }

    @Test
    public void lutimesTest() throws Throwable {
        // FIXME: On Windows this is working but providing wrong numbers and therefore getting wrong results.
        if (!Platform.IS_WINDOWS) {
            File f1 = File.createTempFile("lutimes", null);
            File f2 = new File(f1.getParentFile(), "lutimes-link");
            posix.symlink(f1.getAbsolutePath(), f2.getAbsolutePath());

            int rval = posix.utimes(f1.getAbsolutePath(), new long[]{800, 0}, new long[]{900, 0});
            assertEquals("utimes did not return 0", 0, rval);

            rval = posix.lutimes(f2.getAbsolutePath(), new long[]{1800, 0}, new long[]{1900, 0});
            assertEquals("lutimes did not return 0", 0, rval);

            FileStat stat = posix.stat(f1.getAbsolutePath());
            assertEquals("atime seconds failed", 800, stat.atime());
            assertEquals("mtime seconds failed", 900, stat.mtime());

            stat = posix.lstat(f2.getAbsolutePath());
            assertEquals("atime seconds failed", 1800, stat.atime());
            assertEquals("mtime seconds failed", 1900, stat.mtime());

            f1.delete();
            f2.delete();
        }
    }

    @Test
    public void futimeTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
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
        if (!Platform.IS_WINDOWS) {
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
    }

    @Test
    public void dupTest() throws Throwable {
        File tmp = File.createTempFile("dupTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        FileChannel fileChannel = raf.getChannel();
        int fileDescriptor = getFdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(fileChannel));

        byte[] outContent = "foo".getBytes();

        FileDescriptor newFileDescriptor = toDescriptor(posix.dup(fileDescriptor));

        new FileOutputStream(toDescriptor(fileDescriptor)).write(outContent);
        raf.seek(0);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(newFileDescriptor).read(inContent, 0, 3);

        assertArrayEquals(inContent, outContent);
    }

    @Test
    public void dup2Test() throws Throwable {
        File tmp = File.createTempFile("dupTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");
        int oldFd = getFdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(new RandomAccessFile(tmp, "rw").getChannel()));
        int newFd = getFdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(raf.getChannel()));

        byte[] outContent = "foo".getBytes();

        // NB: Windows differs a bit from POSIX's return value.  Both will return -1 on failure, but Windows will return
        // 0 upon success, while POSIX will return the new FD.  Since we already know what the FD will be if the call
        // is successful, it's easy to make code that works with both forms.  But it is something to watch out for.
        assertNotEquals(-1, posix.dup2(oldFd, newFd));
        FileDescriptor newFileDescriptor = toDescriptor(newFd);

        new FileOutputStream(toDescriptor(oldFd)).write(outContent);
        raf.seek(0);

        byte[] inContent = new byte[outContent.length];
        new FileInputStream(newFileDescriptor).read(inContent, 0, 3);

        assertArrayEquals(inContent, outContent);
    }

    @Test
    public void fcntlDupfdTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
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
    }

    @Test
    public void fcntlDupfdWithArgTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
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
    }

    @Test
    public void closeTest() throws Throwable {
        File tmp = File.createTempFile("closeTest", "tmp");
        int fd = getFdFromDescriptor(JavaLibCHelper.getDescriptorFromChannel(new RandomAccessFile(tmp, "rw").getChannel()));

        int result;

        result = posix.close(fd);
        assertEquals(0, result);

        result = posix.close(fd);
        assertEquals(-1, result);

        // TODO (nirvdrum 06-May-15) We're not getting the correct errno value from Windows currently, so we need to skip this test.
        if (!Platform.IS_WINDOWS) {
            assertEquals(Errno.EBADF.intValue(), posix.errno());
        }
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
        if (!Platform.IS_WINDOWS) {
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
        posix.lseekLong(fd, 0, 0);
        byte[] buf = new byte[21];
        int read = posix.read(fd, buf, 31);
        assertEquals(21, read);
        assertArrayEquals(buf, "Beware the Jabberwock".getBytes());
    }

    @Test
    public void fcntlIntTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            int[] fds = new int[2];
            int ret = posix.pipe(fds);
            assertEquals(0, ret);
            int flags = posix.fcntlInt(fds[0], Fcntl.F_GETFD, 0);
            posix.fcntlInt(fds[0], Fcntl.F_SETFD, flags | 1); // FD_CLOEXEC
            assertEquals(flags | 1, posix.fcntlInt(fds[0], Fcntl.F_GETFD, 0));
        }
    }

    @Test
    public void fchmodTest() throws IOException {
        if (!Platform.IS_WINDOWS) {
            File tmp = File.createTempFile("jnr-posix-chmod-test", "tmp");
            int fd = posix.open(tmp.getAbsolutePath(), OpenFlags.O_RDWR.intValue(), 0600);

            assertEquals("chmod: ", 0, posix.fchmod(fd, 0));
            assertEquals("chmod: ", 0, posix.fchmod(fd, 0777));
            tmp.delete();
        }
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
        if (!Platform.IS_WINDOWS) {
            File file = File.createTempFile("jnr-pøsix-réådlînk-tést", "tmp");
            File link = new File(file.getAbsolutePath() + "link");

            posix.symlink(file.getAbsolutePath(), link.getAbsolutePath());

            byte[] fileBytes = file.getAbsolutePath().getBytes();
            int fileLength = fileBytes.length;

            byte[] buffer = new byte[fileLength];

            assertEquals(fileLength, posix.readlink(link.getAbsolutePath(), buffer, buffer.length));

            assertArrayEquals(fileBytes, buffer);

            link.delete();
            file.delete();
        }
    }

    @Test
    public void readlinkByteBufferTest() throws IOException {
        if (!Platform.IS_WINDOWS) {
            File file = File.createTempFile("jnr-pøsix-réådlînk-tést", "tmp");
            File link = new File(file.getAbsolutePath() + "link");

            posix.symlink(file.getAbsolutePath(), link.getAbsolutePath());

            byte[] fileBytes = file.getAbsolutePath().getBytes();
            int fileLength = fileBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(fileLength);

            assertEquals(fileLength, posix.readlink(link.getAbsolutePath(), buffer, buffer.capacity()));

            assertArrayEquals(buffer.array(), file.getAbsolutePath().getBytes());

            link.delete();
            file.delete();
        }
    }

    @Test
    public void readlinkPointerTest() throws IOException {
        if (!Platform.IS_WINDOWS) {
            File file = File.createTempFile("jnr-pøsix-réådlînk-tést", "tmp");
            File link = new File(file.getAbsolutePath() + "link");

            int bufSize = 1024;

            Pointer buffer = jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().allocateDirect(bufSize);

            posix.symlink(file.getAbsolutePath(), link.getAbsolutePath());
            
            byte[] fileBytes = file.getAbsolutePath().getBytes();
            int fileLength = fileBytes.length;

            assertEquals(fileLength, posix.readlink(link.getAbsolutePath(), buffer, bufSize));

            assertEquals(file.getAbsolutePath(), buffer.getString(0, fileLength, Charset.defaultCharset()));

            link.delete();
            file.delete();
        }
    }

    @Test
    public void mkfifoTest() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            File tmp = File.createTempFile("mkfifoTest", "tmp");
            tmp.deleteOnExit();

            int ret = posix.mkfifo(tmp.getAbsolutePath(), 0666);

            FileInputStream fis = new FileInputStream(tmp);
            FileOutputStream fos = new FileOutputStream(tmp);

            byte[] content = "hello".getBytes();

            fos.write(content);
            fis.read(content);

            assertArrayEquals("hello".getBytes(), content);
        }
    }

    @Test
    public void lseekTest() throws Throwable {
        if (Platform.IS_MAC) {
            int fd = posix.open("/dev/zero", 0, 0);

            // use 2^33 to ensure we're out of int range
            long offset = (long) Math.pow(2, 33);
            long seek = posix.lseekLong(fd, offset, 0);

            assertEquals(seek, offset);
        }
    }

    private int getFdFromDescriptor(FileDescriptor descriptor) {
        if (Platform.IS_WINDOWS) {
            HANDLE handle = JavaLibCHelper.gethandle(descriptor);
            return ((WindowsLibC) posix.libc())._open_osfhandle(handle, 0);
        } else {
            return JavaLibCHelper.getfdFromDescriptor(descriptor);
        }
    }

    private FileDescriptor toDescriptor(int fd) {
        if (Platform.IS_WINDOWS) {
            HANDLE handle = ((WindowsLibC) posix.libc())._get_osfhandle(fd);
            return JavaLibCHelper.toFileDescriptor(handle);
        } else {
            return JavaLibCHelper.toFileDescriptor(fd);
        }
    }

    private boolean hasNanosecondPrecision(FileStat fileStat) {
        return fileStat instanceof NanosecondFileStat && !Platform.IS_MAC;
    }

    private void utimensat(File file, int parentFd) {
        String path = file.getAbsolutePath();
        FileStat fileStat = posix.stat(path);
        if (!hasNanosecondPrecision(fileStat)) {
            file.delete();
            return;
        }

        long atimeSeconds = fileStat.atime()+2;
        long mtimeSeconds = fileStat.mtime()-2;
        long atimeNanoSeconds = 123456789;
        long mtimeNanoSeconds = 135;
        posix.utimensat(parentFd,
                path,
                new long[] {atimeSeconds, atimeNanoSeconds},
                new long[] {mtimeSeconds, mtimeNanoSeconds},
                0);
        assertStatNanoSecond(file, atimeSeconds, atimeNanoSeconds, mtimeSeconds, mtimeNanoSeconds);
    }

    private void assertStatNanoSecond(File file, long atimeSeconds, long atimeNanoSeconds, long mtimeSeconds, long mtimeNanoSeconds) {
        NanosecondFileStat nanosecondFileStat = (NanosecondFileStat) posix.stat(file.getPath());
        assertEquals("Access timestamp should be updated", atimeSeconds, nanosecondFileStat.atime());
        assertEquals("Modification timestamp should be updated", mtimeSeconds, nanosecondFileStat.mtime());
        assertEquals("Access time precision should be in ns", atimeNanoSeconds, nanosecondFileStat.aTimeNanoSecs());
        assertEquals("Modification time precision should be in ns", mtimeNanoSeconds, nanosecondFileStat.mTimeNanoSecs());
        file.delete();
    }
}
