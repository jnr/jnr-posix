package jnr.posix;

import com.kenai.jffi.MemoryIO;
import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import jnr.posix.junit.DisabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvTest {
    private static POSIX posix;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    void testGetenv() {
        assertNotNull(posix.getenv("PATH"));
        assertNull(posix.getenv("SOME_NON_EXISTENT_ENV"));
    }

    @Test
    void testSetenvNonOverwrite() {
        final String path = posix.getenv("PATH");
        int result = posix.setenv("PATH", "new value", 0);

        assertEquals(0, result);
        assertEquals(path, posix.getenv("PATH"));
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testSetenvOverwrite() {
        final String path = posix.getenv("PATH");
        int result = posix.setenv("PATH", "new value", 1);

        assertEquals(0, result);
        assertNotEquals(path, posix.getenv("PATH"));
        posix.setenv("PATH", path, 1);
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testSetEnvNewVar() {
        int result = posix.setenv("MY_NEW_SETENV_VAR", "Yo", 0);

        assertEquals(0, result);
        assertEquals("Yo", posix.getenv("MY_NEW_SETENV_VAR"));
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testUnsetenv() {
        posix.setenv("MY_UNSETENV_VAR", "Yo", 1);
        assertEquals("Yo", posix.getenv("MY_UNSETENV_VAR"));

        int result = posix.unsetenv("MY_UNSETENV_VAR");
        assertEquals(0, result);

        assertNull(posix.getenv("MY_UNSETENV_VAR"));
    }

    @Test
    void testEnv() {
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
