
package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class PasswdTest {

    public PasswdTest() {
    }
    private static POSIX posix;
    private static Class passwdClass;
    @BeforeClass
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

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test public void getpwnam() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "root";
            Passwd pwd = posix.getpwnam(LOGIN);
            assertNotNull(pwd);
            assertEquals("Login name not equal", LOGIN, pwd.getLoginName());

            assertTrue(pwd.getClass().equals(passwdClass));
        }
    }
    @Test public void nonExistantUserReturnsNull() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "dkjhfjkdsfhjksdhfsdjkhfsdkjhfdskj";
            assertNull("getpwnam for non-existant user should return null", posix.getpwnam(LOGIN));
        }
    }
}
