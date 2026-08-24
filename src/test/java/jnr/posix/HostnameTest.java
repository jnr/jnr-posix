package jnr.posix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.Scanner;

public class HostnameTest {
    private static POSIX posix;
    private static POSIX jPosix;

    @BeforeAll
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
        jPosix = POSIXFactory.getJavaPOSIX();
    }

    @Test
    public void testHostnameWorks() {
        assertNotNull(posix.gethostname());
    }

    @Test
    public void jPosixIsReasonable() {
        assumeTrue(System.getenv().containsKey("HOSTNAME") || System.getenv().containsKey("COMPUTERNAME"));
        assertNotNull(jPosix.gethostname());
    }

    @Test
    public void testHostnameIsResonable() throws IOException {
        String hostname = "";
        try {
            hostname = new Scanner(Runtime.getRuntime().exec("hostname").getInputStream()).next();
        } catch (IOException e) {
            assumeTrue(false, "hostname command failed: " + e);
        }
        assumeTrue(!hostname.isEmpty());
        assertEquals(hostname.toLowerCase(), posix.gethostname().toLowerCase());
    }
}
