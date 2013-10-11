package jnr.posix;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

public class DTableSizeTest {
    @Test
    public void testGetDtableSize() throws Exception
    {
        POSIX posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
        assumeTrue("getdtablesize only supported on native posix", posix.isNative());

        int dtablesize = posix.getdtablesize();
        assertThat(dtablesize, not(-1));
    }
}
