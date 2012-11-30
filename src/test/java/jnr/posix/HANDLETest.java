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
    
    // This really is matching pipe for the sake of these unit tests but in general the 
    // in/out/error handle will vary depending on how a CLI supplies it: 
    // http://stackoverflow.com/questions/9021916/how-do-i-check-if-my-delphi-console-app-is-redirected-to-a-file-or-pipe
    private boolean isValidHandleType(HANDLE handle) {
        int type = kernel32().GetFileType(handle);
        
        return type == WindowsLibC.FILE_TYPE_DISK || type == WindowsLibC.FILE_TYPE_PIPE;
    }

    @Test
    public void stdinHandle() {
        if (Platform.IS_WINDOWS) {
            assertTrue(kernel32().GetStdHandle(WindowsLibC.STD_INPUT_HANDLE).isValid());
            assertTrue(isValidHandleType(kernel32().GetStdHandle(WindowsLibC.STD_INPUT_HANDLE)));
        }
    }

    @Test
    public void stdoutHandle() {
        if (Platform.IS_WINDOWS) {
            assertTrue(kernel32().GetStdHandle(WindowsLibC.STD_OUTPUT_HANDLE).isValid());
            assertTrue(isValidHandleType(kernel32().GetStdHandle(WindowsLibC.STD_OUTPUT_HANDLE)));
        }
    }

    @Test
    public void stderrHandle() {
        if (Platform.IS_WINDOWS) {
            assertTrue(kernel32().GetStdHandle(WindowsLibC.STD_ERROR_HANDLE).isValid());
            assertTrue(isValidHandleType(kernel32().GetStdHandle(WindowsLibC.STD_ERROR_HANDLE)));
        }
    }
}
