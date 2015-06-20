package jnr.posix;

import com.kenai.jffi.MemoryIO;
import jnr.ffi.Pointer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnvTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void testGetenv() throws Throwable {
        assertNotNull(posix.getenv("PATH"));
        assertNull(posix.getenv("SOME_NON_EXISTENT_ENV"));
    }

    @Test
    public void testSetenvNonOverwrite() throws Throwable {
        final String path = posix.getenv("PATH");
        int result = posix.setenv("PATH", "new value", 0);

        assertEquals(0, result);
        assertEquals(path, posix.getenv("PATH"));
    }

    @Test
    public void testSetenvOverwrite() throws Throwable {
        final String path = posix.getenv("PATH");
        int result = posix.setenv("PATH", "new value", 1);

        assertEquals(0, result);
        assertNotEquals(path, posix.getenv("PATH"));
    }

    @Test
    public void testSetEnvNewVar() throws Throwable {
        int result = posix.setenv("MY_NEW_SETENV_VAR", "Yo", 0);

        assertEquals(0, result);
        assertEquals("Yo", posix.getenv("MY_NEW_SETENV_VAR"));
    }

    @Test
    public void testUnsetenv() throws Throwable {
        posix.setenv("MY_UNSETENV_VAR", "Yo", 1);
        assertEquals("Yo", posix.getenv("MY_UNSETENV_VAR"));

        int result = posix.unsetenv("MY_UNSETENV_VAR");
        assertEquals(0, result);

        assertNull(posix.getenv("MY_UNSETENV_VAR"));
    }
}
