
package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class PasswdTest {

    private static POSIX posix;
    private static Class passwdClass;
    @BeforeAll
    static void setUpClass() {
        if (Platform.IS_MAC) {
            passwdClass = MacOSPasswd.class;
        } else if (Platform.IS_LINUX) {
            passwdClass = LinuxPasswd.class;
        } else if (Platform.IS_FREEBSD) {
            passwdClass = FreeBSDPasswd.class;
        } else if (Platform.IS_OPENBSD) {
            passwdClass = OpenBSDPasswd.class;
        } else if (Platform.IS_SOLARIS) {
            passwdClass = SolarisPasswd.class;
        } else if (Platform.IS_WINDOWS) {
            // Skipping these tests for windows
        } else {
            throw new IllegalArgumentException("Platform not supported");
        }
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test void getpwnam() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "root";
            Passwd pwd = posix.getpwnam(LOGIN);
            assertNotNull(pwd);
            assertEquals(LOGIN, pwd.getLoginName(), "Login name not equal");

            assertEquals(pwd.getClass(), passwdClass);
        }
    }
    @Test void nonExistantUserReturnsNull() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "dkjhfjkdsfhjksdhfsdjkhfsdkjhfdskj";
            assertNull(posix.getpwnam(LOGIN), "getpwnam for non-existant user should return null");
        }
    }
}
