
package jnr.posix;

import java.io.BufferedReader;
import java.io.IOException;
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
            String[] groupIdsAsStrings = exec("id -G").split(" ");
            long[] expectedGroupIds = new long[groupIdsAsStrings.length];

            for (int i = 0; i < groupIdsAsStrings.length; i++) {
                expectedGroupIds[i] = Long.parseLong(groupIdsAsStrings[i]);
            }

            long[] actualGroupIds = posix.getgroups();

            // getgroups does not specify whether the effective group ID is included along with the supplementary
            // group IDs. However, `id -G` always includes all group IDs. So, we must do something of a fuzzy match.
            // If the actual list is shorter than the expected list by 1, alter the expected list by removing the
            // effective group ID before comparing the two arrays.
            if (actualGroupIds.length == expectedGroupIds.length - 1) {
                long effectiveGroupId = Long.parseLong(exec("id -g"));
                expectedGroupIds = removeElement(expectedGroupIds, effectiveGroupId);
            }

            Arrays.sort(expectedGroupIds);
            Arrays.sort(actualGroupIds);

            assertArrayEquals(expectedGroupIds, actualGroupIds);
        }
    }

    private String exec(String command) throws IOException {
        InputStreamReader isr = null;
        BufferedReader reader = null;

        try {
            isr = new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream());
            reader = new BufferedReader(isr);

            return reader.readLine();
        } finally {
            if (reader != null) {
                reader.close();
            }

            if (isr != null) {
                isr.close();
            }
        }
    }

    private long[] removeElement(long[] array, long value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                long[] ret = new long[array.length - 1];
                System.arraycopy(array, 0, ret, 0, i);
                System.arraycopy(array, i + 1, ret, i, array.length - i - 1);
                return ret;
            }
        }

        return array;
    }
}
