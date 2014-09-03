package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

public class DTableSizeTest {
    @Test
    public void testGetDtableSize() throws Exception
    {
        if (!Platform.IS_WINDOWS) { // FIXME: I have seen window's impl so this can be impld
            POSIX posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
            assumeTrue("getdtablesize only supported on native posix", posix.isNative());

            int dtablesize = posix.getdtablesize();
            assertThat(dtablesize, not(-1));
        }
    }
}
