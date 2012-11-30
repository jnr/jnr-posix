
package jnr.posix;

import jnr.posix.util.FieldAccess;
import jnr.posix.util.Platform;
import org.junit.*;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class FileStatTest {

    public FileStatTest() {
    }

    private static POSIX posix;
    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }


    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test public void filestat() throws Throwable {
        File f = File.createTempFile("stat", null);
        try {
            FileStat st = posix.stat(f.getAbsolutePath());
            assertNotNull("posix.stat failed", st);
        
            FileStat stat = posix.allocateStat();
            int result = posix.stat(f.getAbsolutePath(), stat);
            assertNotNull("posix.stat failed", st);
            assertEquals(0, result);
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
        }
    }

    
    @Test public void structStatSize() throws Throwable {
        if (Platform.IS_SOLARIS) {
            jnr.ffi.Runtime runtime = jnr.ffi.Runtime.getSystemRuntime();
            if (Platform.IS_32_BIT) {
                assertEquals("struct size is wrong", 144, new SolarisFileStat32.Layout(runtime).size());
            } else {
                assertEquals("struct size is wrong", 128, new SolarisFileStat64.Layout(runtime).size());
            }
        }
        
        if (Platform.IS_SOLARIS) {
            File f = File.createTempFile("stat", null);
            try {
                FileStat st = posix.stat(f.getAbsolutePath());

                if (Platform.IS_32_BIT) {
                    assertSame("incorrect stat instance returned", SolarisFileStat32.class, st.getClass());
                } else {
                    assertSame("incorrect stat instance returned", SolarisFileStat64.class, st.getClass());
                }
            } finally {
                f.delete();
            }
        }
    }
}
