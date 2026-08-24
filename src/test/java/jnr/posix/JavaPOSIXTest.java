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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author wayne
 */
class JavaPOSIXTest {
    POSIX posix;

    @BeforeEach
    void setUp() {
        posix = new JavaPOSIX(new DummyPOSIXHandler());
    }

    @AfterEach
    void tearDown() {
        posix = null;
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test void uid() {
        
    }

    @Test void lstatTest() throws IOException {
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

    @Test void statMissingFileTest() {
        FileStat stat = posix.allocateStat();

        int ret = posix.stat("does not exist", stat);

        assertEquals(-1, ret);
        assertEquals(Errno.ENOENT.intValue(), posix.errno());
    }

	@Test
	void chmodTest() throws IOException {
        File file = createTestFile();

		assertEquals(0, posix.chmod(file.getName(), 0), "chmod: ");
		assertEquals(0, posix.chmod(file.getName(), Modes.RWXA), "chmod: ");

		file.delete();
	}

    @Test void getpwuid() {
        Passwd pwd = posix.getpwuid(posix.getuid());
        assertNotNull(pwd, "getpwuid failed");
        
    }
    @Test void isNative() {
        assertFalse(posix.isNative(), "JavaPOSIX isNative should be false");
    }

    private File createTestFile() throws IOException {
        // create a tmp file
        String fName = "test.dat";
        File file = new File(fName);
        file.createNewFile();

        return file;
    }

    @Test void getpid() {
        assertEquals(POSIXFactory.getNativePOSIX().getpid(), posix.getpid());
    }
}
