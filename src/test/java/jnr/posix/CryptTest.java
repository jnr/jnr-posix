package jnr.posix;

import jnr.ffi.Platform;
import jnr.posix.junit.DisabledOnJnrOs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by headius on 3/24/15.
 */
class CryptTest {
    @BeforeEach
    void before() {
        posix = POSIXFactory.getPOSIX();
    }
    @Test
    @DisabledOnJnrOs(Platform.OS.WINDOWS)
    void testCrypt() {
        String str1 = "blahblahblah";
        String salt1 = "saltysalty";

        CharSequence result1 = posix.crypt(str1, salt1);
        assertNotNull(result1);

        byte[] str1bytes = Arrays.copyOfRange(str1.getBytes(), 0, str1.length() + 1);
        byte[] salt1bytes = Arrays.copyOfRange(salt1.getBytes(), 0, salt1.length() + 1);
        byte[] result2 = posix.crypt(str1bytes, salt1bytes);

        assertNotNull(result2);

        String result2str = new String(result2, 0, result2.length - 1);

        assertEquals(result1, result2str);
    }

    private POSIX posix;
}
