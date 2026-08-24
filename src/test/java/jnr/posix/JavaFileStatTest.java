/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.posix;

import jnr.posix.JavaFileStat;
import org.junit.jupiter.api.Test;

import jnr.posix.util.Platform;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 * @author nicksieger
 */
public class JavaFileStatTest {
    @Test
    public void testSetup() {
        JavaFileStat fs = new JavaFileStat(null, null);
        if (Platform.IS_WINDOWS) {
            fs.setup("c:/");
        } else {
            fs.setup("/");
        }
        assertFalse(fs.isSymlink());
    }
}
