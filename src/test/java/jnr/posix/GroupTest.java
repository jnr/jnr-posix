
package jnr.posix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import jnr.posix.util.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static jnr.ffi.Platform.getNativePlatform;
import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    private static POSIX posix;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    void getgrnam() {
        if (getNativePlatform().isUnix()) {
            final String LOGIN = "daemon";
            Group grp = posix.getgrnam(LOGIN);
            assertNotNull(grp);
            assertEquals(LOGIN, grp.getName(), "Login name not equal");
        }
    }

    @Test
    void nonExistantGroupReturnsNull() {
        if (getNativePlatform().isUnix()) {
            final String LOGIN = "dkjhfjkdsfhjksdhfsdjkhfsdkjhfdskj";
            assertNull(posix.getgrnam(LOGIN), "getpwnam for non-existant group should return null");
        }
    }

    @Test
    void getgrent() {
        if (getNativePlatform().isUnix()) {
            ArrayList<Group> grps = new ArrayList<>();
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
                for (String member : grp.getMembers()) {
                    assertNotNull(member);
                }
            }
        }
    }

    @Test
    void getgroups() throws Throwable {
        if (getNativePlatform().isUnix() && !Platform.IS_MAC) {
            String[] groupIdsAsStrings = exec("id", "-G").split(" ");
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
                long effectiveGroupId = Long.parseLong(exec("id", "-g"));
                expectedGroupIds = removeElement(expectedGroupIds, effectiveGroupId);
            }

            Arrays.sort(expectedGroupIds);
            Arrays.sort(actualGroupIds);

            assertArrayEquals(expectedGroupIds, actualGroupIds);
        }
    }

    private String exec(String... command) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(Runtime.getRuntime().exec(command)
            .getInputStream()); BufferedReader reader = new BufferedReader(isr)) {

            return reader.readLine();
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
