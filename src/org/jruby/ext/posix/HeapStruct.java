/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * 
 *  
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
/**
 * $Id: $
 */

package org.jruby.ext.posix;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 */
public class HeapStruct implements com.sun.jna.NativeMapped {
    private static final String arch = System.getProperty("os.arch").toLowerCase();
    private static final boolean isSPARC = "sparc".equals(arch);
    /*
     * Most arches align long/double on the same size as a native long (or a pointer)
     * Sparc (32bit) requires it to be aligned on an 8 byte boundary
     */
    private static final int LONG_SIZE = (Platform.isWindows() ? 4 : Pointer.SIZE) * 8;
    private static final int LONG_ALIGN = isSPARC ? 64 : LONG_SIZE;
    private static final long LONG_MASK = LONG_SIZE == 32 ? 0x7FFFFFFFL : 0x7FFFFFFFFFFFFFFFL;
    private static final int DOUBLE_ALIGN = isSPARC ? 64 : LONG_SIZE;
    private static final int FLOAT_ALIGN = isSPARC ? 64 : 32;
    private ByteBuffer buffer;
    private int size = 0;
    
    public Object fromNative(Object arg0, FromNativeContext arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object toNative() {
        return getByteBuffer();
    }

    public Class nativeType() {
        return ByteBuffer.class;
    }
    protected final ByteBuffer getByteBuffer() {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
        }
        return buffer;
    }
    public final int getStructSize() {
        return size;
    }
    protected final int addField(int size, int align) {
        int mask = (align / 8) - 1;
        if ((this.size & mask) != 0) {
            this.size = (this.size & ~mask) + (align / 8);
        }
        int off = this.size;  
        this.size += size / 8;
        return off;
    }
    protected abstract class Field {
        public final int size;
        public final int align;
        public final int offset;
        public Field(int size) {
            this(size, size);
        }
        public Field(int size, int align) {
            this.size = size;
            this.align = align;
            this.offset = addField(size, align);
        }
    }
    protected class Int8 extends Field {
        public Int8() {
            super(8);
        }
        public Int8(byte value) {
            this();
            set(value);
        }
        public final byte get() {
            return getByteBuffer().get(offset);
        }
        public final void set(byte value) {
            getByteBuffer().put(offset, value);
        }
    }
    protected class UInt8 extends Field {
        public UInt8() {
            super(8);
        }
        public UInt8(short value) {
            this();
            set(value);
        }
        public final short get() {
            final short value = getByteBuffer().get(offset);
            return value < 0 ? (short) ((value & 0x7F) + 0x80) : value;
        }
        public final void set(short value) {
            getByteBuffer().put(offset, (byte) value);
        }
    }
    protected final class Byte extends Int8 {
        public Byte() { }
        public Byte(byte value) {
            super(value);
        }
    }
    protected class Int16 extends Field {
        public Int16() {
            super(16);
        }
        public Int16(short value) {
            this();
            set(value);
        }
        public final short get() {
            return getByteBuffer().getShort(offset);
        }
        public final void set(short value) {
            getByteBuffer().putShort(offset, value);
        }
    }
    protected class UInt16 extends Field {
        public UInt16() {
            super(16);
        }
        public UInt16(short value) {
            this();
            set(value);
        }
        public final int get() {
            final int value = getByteBuffer().getShort(offset);
            return value < 0 ? (int)((value & 0x7FFF) + 0x8000) : value;
        }
        public final void set(int value) {
            getByteBuffer().putShort(offset, (short) value);
        }
    }
    protected class Short extends Int16 {
        public Short() {}
        public Short(short value) {
            super(value);
        }
    }
    protected class Int32 extends Field {
        public Int32() {
            super(32);
        }
        public Int32(int value) {
            this();
            set(value);
        }
        public final int get() {
            return getByteBuffer().getInt(offset);
        }
        public final void set(int value) {
            getByteBuffer().putInt(offset, value);
        }
    }
    protected class UInt32 extends Field {
        public UInt32() {
            super(32);
        }
        public UInt32(long value) {
            this();
            set(value);
        }
        public final long get() {
            final long value = getByteBuffer().getInt(offset);
            return value < 0 ? (long)((value & 0x7FFFFFFFL) + 0x80000000L) : value;
        }
        public final void set(long value) {
            getByteBuffer().putInt(offset, (int) value);
        }
    }
    protected class Integer extends Int32 {
        public Integer() {}
        public Integer(int value) {
            super(value);
        }
    }
    protected class Int64 extends Field {
        public Int64() {
            super(64, LONG_ALIGN);
        }
        public Int64(long value) {
            this();
            set(value);
        }
        public final long get() {
            return getByteBuffer().getLong(offset);
        }
        public final void set(long value) {
            getByteBuffer().putLong(offset, value);
        }
    }
    
    protected class Long extends Field {
        public Long() {
            super(LONG_SIZE, LONG_ALIGN);
        }
        public Long(long value) {
            this();
            set(value);
        }
        public final long get() {
            return LONG_SIZE == 32 
                    ? getByteBuffer().getInt(offset) : getByteBuffer().getLong(offset);
        }
        public final void set(long value) {
            if (LONG_SIZE == 32) {
                getByteBuffer().putInt(offset, (int) value);
            } else {
                getByteBuffer().putLong(offset, value);
            }
        }
    }
    protected class ULong extends Field {
        public ULong() {
            super(LONG_SIZE, LONG_ALIGN);
        }
        public ULong(long value) {
            this();
            set(value);
        }
        public final long get() {
            final long value = LONG_SIZE == 32 
                    ? getByteBuffer().getInt(offset) : getByteBuffer().getLong(offset);
            return value < 0
                    ? (long) ((value & LONG_MASK) + LONG_MASK + 1) 
                    : value;
        }
        public final void set(long value) {
            if (LONG_SIZE == 32) {
                getByteBuffer().putInt(offset, (int) value);
            } else {
                getByteBuffer().putLong(offset, value);
            }
        }
    }
    protected class LongLong extends Int64 {
        public LongLong() { }
        public LongLong(long value) {
            super(value);
        }
    }
}
