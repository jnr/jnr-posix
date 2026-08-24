
package jnr.posix;

import java.io.FileOutputStream;
import java.io.IOException;
import jnr.posix.util.FieldAccess;
import jnr.posix.util.Platform;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class FileStatTest {

    public FileStatTest() {
    }

    private void addNBytes(File file, int amount) {
        FileOutputStream fis = null;

        try {
            fis = new FileOutputStream(file);
            byte[] buf = new byte[amount];
            fis.write(buf);
        } catch (IOException e) {
            if (fis != null) { try { fis.close(); } catch (IOException e2) {} }
        }
    }

    private static POSIX posix;

    @BeforeAll
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }


    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void filestat() throws Throwable {
        File f = File.createTempFile("stat", null);
        int size = 1567;
        //Thread.sleep(2000);
        addNBytes(f, size);
        try {
            FileStat stat = posix.stat(f.getAbsolutePath());
            assertNotNull(stat, "posix.stat failed");
            assertEquals(size, stat.st_size());
            assertEquals(1, stat.nlink());
            //assertNotEquals(stat.mtime(), stat.ctime());

            stat = posix.allocateStat();
            int result = posix.stat(f.getAbsolutePath(), stat);
            assertNotNull(stat, "posix.stat failed");
            assertEquals(0, result);
            assertEquals(size, stat.st_size());
        } finally {
            f.delete();
        }
    }

    @Test
    public void filestatDescriptor() throws Throwable {
        File f = File.createTempFile("stat", null);

        try {
            FileInputStream fis = new FileInputStream(f);
            FileStat stat = posix.allocateStat();
            int result = posix.fstat(fis.getFD(), stat);
            assertEquals(0, result);
            assertEquals(0, stat.st_size());
        } finally {
            f.delete();
        }

    }

    @Test
    public void filestatInt() throws Throwable {
        // Windows does not store fd in FileDescriptor so this test wll not work
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            File f = File.createTempFile("stat", null);
            try {
                FileInputStream fis = new FileInputStream(f);
                FileDescriptor desc = fis.getFD();
                int fd = -1;
                try {
                    Field fdField = FieldAccess.getProtectedField(FileDescriptor.class, "fd");
                    fd = fdField.getInt(desc);
                } catch (SecurityException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
                FileStat stat = posix.allocateStat();
                int result = posix.fstat(fd, stat);
                assertTrue(fd > 2); // should not be stdin, stdout, stderr
                assertEquals(0, result);
            } finally {
                f.delete();
            }
        } else {
            FileStat stat = posix.fstat(0);
            assertTrue(stat != null);
        }
    }

    // FIXME: I could guarantee this does not exist but this was very very quick :)
    private static final String NON_EXISTENT_FILENAME = "skdjlfklfsdjk";
    @Test
    public void filestatFailed() throws Throwable {
        FileStat stat = null;

        // A little wonky without adding a new posixhandler but we are using dummy so this is ok for now
        // the default handler raises on a problem in stat so we are only verifying this at the moment.
        try {
            stat = posix.stat(NON_EXISTENT_FILENAME);
        } catch (UnsupportedOperationException e) {}

        assertTrue(stat == null);
    }

    
    @Test
    public void fileStatNanoTime() throws Throwable {
        // Currently only linux file stat support nano time resolution
        jnr.ffi.Platform nativePlatform = jnr.ffi.Platform.getNativePlatform();
        if (nativePlatform.getOS() == jnr.ffi.Platform.OS.LINUX) {
            File f = File.createTempFile("stat", null);
            try {
                FileStat st = posix.stat(f.getAbsolutePath());
                assertNotNull(st, "posix.stat failed");

                FileStat stat = posix.allocateStat();
                int result = posix.stat(f.getAbsolutePath(), stat);
                assertNotNull(st, "posix.stat failed");
                assertEquals(0, result);

                NanosecondFileStat fstat32 = (NanosecondFileStat) stat;
                assertTrue(fstat32.cTimeNanoSecs() > 0);
                assertTrue(fstat32.mTimeNanoSecs() > 0);
                assertTrue(fstat32.aTimeNanoSecs() > 0);
                assertEquals(fstat32.cTimeNanoSecs(), fstat32.mTimeNanoSecs());
            } finally {
                f.delete();
            }
        }
    }

    @Test
    public void structStatSize() throws Throwable {
        if (Platform.IS_SOLARIS) {
            jnr.ffi.Runtime runtime = jnr.ffi.Runtime.getSystemRuntime();
            if (Platform.IS_32_BIT) {
                assertEquals(144, new SolarisFileStat32.Layout(runtime).size(), "struct size is wrong");
            } else {
                assertEquals(128, new SolarisFileStat64.Layout(runtime).size(), "struct size is wrong");
            }
        }

        if (Platform.IS_SOLARIS) {
            File f = File.createTempFile("stat", null);
            try {
                FileStat st = posix.stat(f.getAbsolutePath());

                if (Platform.IS_32_BIT) {
                    assertSame(SolarisFileStat32.class, st.getClass(), "incorrect stat instance returned");
                } else {
                    assertSame(SolarisFileStat64.class, st.getClass(), "incorrect stat instance returned");
                }
            } finally {
                f.delete();
            }
        }
    }

    @Test
    public void filestatDirectory() throws Throwable {
        File f = File.createTempFile("stat", null).getParentFile();
        try {
            FileStat stat = posix.stat(f.getAbsolutePath());

            assertTrue(stat.isDirectory());
        } finally {
            f.delete();
        }
    }
}
