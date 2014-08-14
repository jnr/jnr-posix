package jnr.posix;

import jnr.ffi.Runtime;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.StructLayout;

import java.nio.ByteBuffer;

/**
 * @author Bob McWhirter
 */
public class BaseIovec implements Iovec {


    public static class Layout extends StructLayout {
        protected Layout(Runtime runtime) {
            super(runtime);
        }

        public final Pointer iov_base = new Pointer();
        public final size_t iov_len = new size_t();
    }

    public static final Layout layout = new Layout(Runtime.getSystemRuntime());

    private final NativePOSIX posix;
    protected final Pointer memory;

    public String toString(String indent) {
        StringBuffer buf = new StringBuffer();
        buf.append( indent ).append( "iovec {\n" );
        buf.append(indent).append( "  iov_base=" ).append(layout.iov_base.get(this.memory)).append( ",\n" );
        buf.append( indent ).append( "  iov_len=" ).append( layout.iov_len.get( this.memory ) ).append(",\n");
        buf.append(indent).append( "}" );

        return buf.toString();
    }

    protected BaseIovec(NativePOSIX posix) {
        this.posix = posix;
        this.memory = Memory.allocate(posix.getRuntime(), layout.size());
    }

    BaseIovec(NativePOSIX posix, Pointer memory) {
        this.posix = posix;
        this.memory = memory;
    }

    public ByteBuffer get() {
        int len = getLen();
        byte[] bytes = new byte[len];
        layout.iov_base.get( this.memory ).get(0, bytes, 0, len );
        return ByteBuffer.wrap( bytes );
    }

    public void set(ByteBuffer buf) {
        int len = buf.remaining();
        layout.iov_base.set( this.memory, Pointer.wrap( posix.getRuntime(), buf ) );
        setLen(len);
    }

    protected void setLen(int len) {
        layout.iov_len.set( this.memory, len );
    }

    protected int getLen() {
        return (int) layout.iov_len.get( this.memory );
    }
}
