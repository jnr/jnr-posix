package jnr.posix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.Scanner;

class HostnameTest {
    private static POSIX posix;
    private static POSIX jPosix;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
        jPosix = POSIXFactory.getJavaPOSIX();
    }

    @Test
    void testHostnameWorks() {
        assertNotNull(posix.gethostname());
    }

    @Test
    void jPosixIsReasonable() {
        assumeTrue(System.getenv().containsKey("HOSTNAME") || System.getenv().containsKey("COMPUTERNAME"));
        assertNotNull(jPosix.gethostname());
    }

    @Test
    void testHostnameIsResonable() {
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
