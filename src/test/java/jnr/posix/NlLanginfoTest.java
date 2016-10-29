package jnr.posix;

import jnr.constants.platform.LangInfo;
import jnr.posix.util.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

public class NlLanginfoTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void testNlLanginfo() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            InputStreamReader isr = null;
            BufferedReader reader = null;

            try {
                isr = new InputStreamReader(Runtime.getRuntime().exec("locale charmap").getInputStream());
                reader = new BufferedReader(isr);

                String localeCharmap = reader.readLine();
                assertEquals(localeCharmap, posix.nl_langinfo(LangInfo.CODESET.intValue()));
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
