package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HANDLETest {

    private static POSIX posix;
    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void invalidHandleIsInValid() {
        assertFalse(HANDLE.valueOf(-1L).isValid());
    }

    private static WindowsLibC kernel32() {
        return (WindowsLibC) posix.libc();
    }

    @Test
    public void stdinHandle() {
        if (Platform.IS_WINDOWS) {
            assertTrue(kernel32().GetStdHandle(WindowsLibC.STD_INPUT_HANDLE).isValid());
            assertEquals(WindowsLibC.FILE_TYPE_CHAR, kernel32().GetFileType(kernel32().GetStdHandle(WindowsLibC.STD_INPUT_HANDLE)));
        }
    }

    @Test
    public void stdoutHandle() {
        if (Platform.IS_WINDOWS) {
            assertTrue(kernel32().GetStdHandle(WindowsLibC.STD_OUTPUT_HANDLE).isValid());
            assertEquals(WindowsLibC.FILE_TYPE_CHAR, kernel32().GetFileType(kernel32().GetStdHandle(WindowsLibC.STD_OUTPUT_HANDLE)));
        }
    }

    @Test
    public void stderrHandle() {
        if (Platform.IS_WINDOWS) {
            assertTrue(kernel32().GetStdHandle(WindowsLibC.STD_ERROR_HANDLE).isValid());
            assertEquals(WindowsLibC.FILE_TYPE_CHAR, kernel32().GetFileType(kernel32().GetStdHandle(WindowsLibC.STD_ERROR_HANDLE)));
        }
    }
}
