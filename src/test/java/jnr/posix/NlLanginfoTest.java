package jnr.posix;

import jnr.constants.platform.LangInfo;
import jnr.posix.util.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NlLanginfoTest {
    private static POSIX posix;

    @BeforeAll
    public static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void testNlLanginfo() throws Throwable {
        if (!Platform.IS_WINDOWS) {

            try (InputStreamReader isr = new InputStreamReader(
                Runtime.getRuntime().exec("locale charmap")
                    .getInputStream()); BufferedReader reader = new BufferedReader(isr)) {

                String localeCharmap = reader.readLine();
                assertEquals(localeCharmap, posix.nl_langinfo(LangInfo.CODESET.intValue()));
            }
        }
    }

}
