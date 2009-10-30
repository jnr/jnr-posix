
package org.jruby.ext.posix;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class GroupTest {

    public GroupTest() {
    }

    private static POSIX posix;
    @BeforeClass
    public static void setUpClass() throws Exception {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test public void getgrnam() {
        final String LOGIN = "nogroup";
        Group grp = posix.getgrnam(LOGIN);
        assertNotNull(grp);
        assertEquals("Login name not equal", LOGIN, grp.getName());
    }

    @Test public void nonExistantGroupReturnsNull() {
        final String LOGIN = "dkjhfjkdsfhjksdhfsdjkhfsdkjhfdskj";
        assertNull("getpwnam for non-existant group should return null", posix.getgrnam(LOGIN));
    }

    @Test public void getgrent() {
        ArrayList<Group> grps = new ArrayList<Group>();
        while (true) {
            Group grp = posix.getgrent();
            if (grp == null) {
                break;
            }
            grps.add(grp);
        }
        for (Group grp : grps) {
            assertNotNull(grp.getName());
            assertNotNull(grp.getPassword());
            assertNotNull(grp.getGID());
        }
    }
}
