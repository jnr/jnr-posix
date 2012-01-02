
package jnr.posix;

import java.lang.reflect.Field;
import jnr.posix.util.FieldAccess;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.File;

import jnr.ffi.Struct;
import jnr.posix.util.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
            assertEquals(0,result);
        } finally {
            f.delete();
        }
    }

    
    @Test public void structStatSize() throws Throwable {
        if (Platform.IS_SOLARIS) {
            if (Platform.IS_32_BIT) {
                assertEquals("struct size is wrong", 144, Struct.size(new SolarisHeapFileStat((SolarisPOSIX) posix)));
            } else {
                assertEquals("struct size is wrong", 128, Struct.size(new Solaris64FileStat()));
            }
        }
        
        if (Platform.IS_SOLARIS) {
            File f = File.createTempFile("stat", null);
            try {
                FileStat st = posix.stat(f.getAbsolutePath());
                if (Platform.IS_32_BIT) {
                    assertEquals("struct size is wrong", 144, Struct.size((SolarisHeapFileStat)posix.stat(f.getAbsolutePath())));
                } else {
                    assertEquals("struct size is wrong", 128, Struct.size((Solaris64FileStat)posix.stat(f.getAbsolutePath())));
                }
            } finally {
                f.delete();
            }
        }
    }
}