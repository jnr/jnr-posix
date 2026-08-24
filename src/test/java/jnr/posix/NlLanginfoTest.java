package jnr.posix;

import jnr.constants.platform.LangInfo;
import jnr.ffi.Platform;
import jnr.posix.junit.DisabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NlLanginfoTest {
    private static POSIX posix;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testNlLanginfo() throws Throwable {
        try (InputStreamReader isr = new InputStreamReader(
            Runtime.getRuntime().exec("locale charmap")
                .getInputStream()); BufferedReader reader = new BufferedReader(isr)) {

            String localeCharmap = reader.readLine();
            assertEquals(localeCharmap, posix.nl_langinfo(LangInfo.CODESET.intValue()));
        }
    }

}
