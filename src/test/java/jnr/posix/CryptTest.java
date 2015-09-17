package jnr.posix;

import jnr.posix.util.Platform;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by headius on 3/24/15.
 */
public class CryptTest {
    @Before
    public void before() {
        posix = POSIXFactory.getPOSIX();
    }
    @Test
    public void testCrypt() {
        if (!Platform.IS_WINDOWS) {
            String str1 = "blahblahblah";
            String salt1 = "saltysalty";

            CharSequence result1 = posix.crypt(str1, salt1);
            Assert.assertNotNull(result1);

            byte[] str1bytes = Arrays.copyOfRange(str1.getBytes(), 0, str1.length() + 1);
            byte[] salt1bytes = Arrays.copyOfRange(salt1.getBytes(), 0, salt1.length() + 1);
            byte[] result2 = posix.crypt(str1bytes, salt1bytes);

            Assert.assertNotNull(result2);

            String result2str = new String(result2, 0, result2.length - 1);

            Assert.assertEquals(result1, result2str);
        }
    }

    private POSIX posix;
}
