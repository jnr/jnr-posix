/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

import java.io.File;
import java.io.IOException;

import jnr.posix.JavaPOSIX;
import jnr.posix.POSIX;
import jnr.posix.Passwd;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wayne
 */
public class JavaPOSIXTest {
    POSIX posix;
    public JavaPOSIXTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        posix = new JavaPOSIX(new DummyPOSIXHandler());
    }

    @After
    public void tearDown() {
        posix = null;
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test public void uid() {
        
    }

    @Test public void lstatTest() throws IOException {
        File file = createTestFile();

        FileStat stat = posix.lstat(file.getName());

        assertTrue(stat.isEmpty());

        stat = posix.allocateStat();
        int ret = posix.lstat(file.getPath(), stat);

        assertTrue(ret >= 0);
        assertTrue(stat.isEmpty());

        file.delete();

        stat = posix.allocateStat();
        ret = posix.lstat(file.getPath(), stat);

        assertTrue(ret < 0);
    }

	@Test
	public void chmodTest() throws IOException {
        File file = createTestFile();

		assertEquals("chmod: ", 0, posix.chmod(file.getName(), 0));
		assertEquals("chmod: ", 0, posix.chmod(file.getName(), 0777));

		file.delete();
	}

    @Test public void getpwuid() {
        Passwd pwd = posix.getpwuid(posix.getuid());
        assertNotNull("getpwuid failed", pwd);
        
    }
    @Test public void isNative() {
        assertFalse("JavaPOSIX isNative should be false", posix.isNative());
    }

    private File createTestFile() throws IOException {
        // create a tmp file
        String fName = "test.dat";
        File file = new File(fName);
        file.createNewFile();

        return file;
    }
}
