
package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class PasswdTest {

    public PasswdTest() {
    }
    private static POSIX posix;
    private static Class passwdClass;
    @BeforeAll
    public static void setUpClass() throws Exception {
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

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test public void getpwnam() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "root";
            Passwd pwd = posix.getpwnam(LOGIN);
            assertNotNull(pwd);
            assertEquals(LOGIN, pwd.getLoginName(), "Login name not equal");

            assertTrue(pwd.getClass().equals(passwdClass));
        }
    }
    @Test public void nonExistantUserReturnsNull() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "dkjhfjkdsfhjksdhfsdjkhfsdkjhfdskj";
            assertNull(posix.getpwnam(LOGIN), "getpwnam for non-existant user should return null");
        }
    }
}
