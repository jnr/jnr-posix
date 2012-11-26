package jnr.posix;

import org.junit.Test;
import static org.junit.Assert.*;

public class HANDLETest {

    @Test
    public void invalidHandleIsInValid() {
        assertFalse(HANDLE.valueOf(-1L).isValid());
    }
}
