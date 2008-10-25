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
 */
public class HeapStructTest {

    public HeapStructTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private static final class Unsigned8Test extends HeapStruct {
        public final Unsigned8 u8 = new Unsigned8();
    }
    @Test
    public void unsigned8() {
        Unsigned8Test s = new Unsigned8Test();
        final short MAGIC = (short) Byte.MAX_VALUE + 1;
        s.u8.set(MAGIC);
        assertEquals("Incorrect unsigned byte value", MAGIC, s.u8.get());
    }
    private static final class Unsigned16Test extends HeapStruct {
        public final Unsigned16 u16 = new Unsigned16();
    }
    @Test
    public void unsigned16() {
        Unsigned16Test s = new Unsigned16Test();
        final int MAGIC = (int) Short.MAX_VALUE + 1;
        s.u16.set(MAGIC);
        assertEquals("Incorrect unsigned short value", MAGIC, s.u16.get());
    }
    private static final class Unsigned32Test extends HeapStruct {
        public final UInt32 u32 = new UInt32();
    }
    @Test
    public void unsigned32() {
        Unsigned32Test s = new Unsigned32Test();
        final long MAGIC = (long) Integer.MAX_VALUE + 1;
        s.u32.set(MAGIC);
        assertEquals("Incorrect unsigned int value", MAGIC, s.u32.get());
    }
    
   
    private static final class UnsignedLongTest extends HeapStruct {
        public final UnsignedLong ul = new UnsignedLong();
    }
    @Test
    public void unsignedLong() {
        UnsignedLongTest s = new UnsignedLongTest();
        final long MAGIC = (long) Integer.MAX_VALUE + 1;
        s.ul.set(MAGIC);
        assertEquals("Incorrect unsigned long value", MAGIC, s.ul.get());
    }
}