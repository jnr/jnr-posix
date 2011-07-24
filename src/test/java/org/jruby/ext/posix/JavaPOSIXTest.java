/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wayne
 */
public class JavaPOSIXTest {
    POSIX posix;
    public JavaPOSIXTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        posix = new JavaPOSIX(new DummyPOSIXHandler());
    }

    @After
    public void tearDown() {
        posix = null;
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test public void uid() {
        
    }
    @Test public void getpwuid() {
        Passwd pwd = posix.getpwuid(posix.getuid());
        assertNotNull("getpwuid failed", pwd);
        
    }
    @Test public void isNative() {
        assertFalse("JavaPOSIX isNative should be false", posix.isNative());
    }
}
