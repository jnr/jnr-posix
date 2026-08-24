/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

import java.io.File;
import java.io.IOException;

import jnr.constants.platform.Errno;
import jnr.posix.junit.Modes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author wayne
 */
public class JavaPOSIXTest {
    POSIX posix;
    public JavaPOSIXTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        posix = new JavaPOSIX(new DummyPOSIXHandler());
    }

    @AfterEach
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

    @Test public void statMissingFileTest() {
        FileStat stat = posix.allocateStat();

        int ret = posix.stat("does not exist", stat);

        assertEquals(-1, ret);
        assertEquals(Errno.ENOENT.intValue(), posix.errno());
    }

	@Test
	public void chmodTest() throws IOException {
        File file = createTestFile();

		assertEquals(0, posix.chmod(file.getName(), 0), "chmod: ");
		assertEquals(0, posix.chmod(file.getName(), Modes.RWXA), "chmod: ");

		file.delete();
	}

    @Test public void getpwuid() {
        Passwd pwd = posix.getpwuid(posix.getuid());
        assertNotNull(pwd, "getpwuid failed");
        
    }
    @Test public void isNative() {
        assertFalse(posix.isNative(), "JavaPOSIX isNative should be false");
    }

    private File createTestFile() throws IOException {
        // create a tmp file
        String fName = "test.dat";
        File file = new File(fName);
        file.createNewFile();

        return file;
    }

    @Test public void getpid() {
        assertEquals(POSIXFactory.getNativePOSIX().getpid(), posix.getpid());
    }
}
