package jnr.posix;

import com.kenai.jffi.MemoryIO;
import jnr.ffi.Pointer;
import jnr.posix.util.Platform;
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
        if (!Platform.IS_WINDOWS) {
            final String path = posix.getenv("PATH");
            int result = posix.setenv("PATH", "new value", 1);

            assertEquals(0, result);
            assertNotEquals(path, posix.getenv("PATH"));
        }
    }

    @Test
    public void testSetEnvNewVar() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            int result = posix.setenv("MY_NEW_SETENV_VAR", "Yo", 0);

            assertEquals(0, result);
            assertEquals("Yo", posix.getenv("MY_NEW_SETENV_VAR"));
        }
    }

    @Test
    public void testUnsetenv() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            posix.setenv("MY_UNSETENV_VAR", "Yo", 1);
            assertEquals("Yo", posix.getenv("MY_UNSETENV_VAR"));

            int result = posix.unsetenv("MY_UNSETENV_VAR");
            assertEquals(0, result);

            assertNull(posix.getenv("MY_UNSETENV_VAR"));
        }
    }

    @Test
    public void testEnv() throws Throwable {
        final Pointer env = posix.environ();

        int offset = 0;
        while (env.getPointer(offset) != null) {
            final Pointer entryPointer = env.getPointer(offset);
            final String entry = new String(MemoryIO.getInstance().getZeroTerminatedByteArray(entryPointer.address()));

            assertTrue(entry.contains("="));

            offset += jnr.ffi.Runtime.getSystemRuntime().addressSize();
        }
    }
}
