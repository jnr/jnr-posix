
package jnr.posix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import jnr.constants.platform.LangInfo;
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
    @Test
    public void getgrnam() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "nogroup";
            Group grp = posix.getgrnam(LOGIN);
            assertNotNull(grp);
            assertEquals("Login name not equal", LOGIN, grp.getName());
        }
    }

    @Test
    public void nonExistantGroupReturnsNull() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            final String LOGIN = "dkjhfjkdsfhjksdhfsdjkhfsdkjhfdskj";
            assertNull("getpwnam for non-existant group should return null", posix.getgrnam(LOGIN));
        }
    }

    @Test
    public void getgrent() {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
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
                for (String member : grp.getMembers()) {
                    assertNotNull(member);
                }
            }
        }
    }

    @Test
    public void getgroups() throws Throwable {
        if (jnr.ffi.Platform.getNativePlatform().isUnix()) {
            InputStreamReader isr = null;
            BufferedReader reader = null;

            try {
                isr = new InputStreamReader(Runtime.getRuntime().exec("id -G").getInputStream());
                reader = new BufferedReader(isr);

                String[] groupIdsAsStrings = reader.readLine().split(" ");
                long[] expectedGroupIds = new long[groupIdsAsStrings.length];

                for (int i = 0; i < groupIdsAsStrings.length; i++) {
                    expectedGroupIds[i] = Long.parseLong(groupIdsAsStrings[i]);
                }

                long[] actualGroupIds = posix.getgroups();

                Arrays.sort(expectedGroupIds);
                Arrays.sort(actualGroupIds);

                assertArrayEquals(expectedGroupIds, actualGroupIds);
            } finally {
                if (reader != null) {
                    reader.close();
                }

                if (isr != null) {
                    isr.close();
                }
            }
        }
    }
}
