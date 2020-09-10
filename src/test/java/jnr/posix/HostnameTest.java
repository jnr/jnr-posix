package jnr.posix;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.IOException;
import java.util.Scanner;

public class HostnameTest {
    private static POSIX posix;
    private static POSIX jPosix;

    @BeforeClass
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
        // cast here works around a generic resolution problem in Java 11+
        assumeThat(System.getenv().keySet(), (Matcher) anyOf(hasItem("HOSTNAME"), hasItem("COMPUTERNAME")));
        assertNotNull(jPosix.gethostname());
    }

    @Test
    public void testHostnameIsResonable() throws IOException {
        String hostname = "";
        try {
            hostname = new Scanner(Runtime.getRuntime().exec("hostname").getInputStream()).next();
        } catch (IOException e) {
            assumeNoException(e);
        }
        assumeThat(hostname, is(not(equalTo(""))));
        assertThat(posix.gethostname().toLowerCase(), equalTo(hostname.toLowerCase()));
    }
}
