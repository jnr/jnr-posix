package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.StructLayout;

import java.nio.ByteBuffer;

/**
 * @author Bob McWhirter
 */
class MacOSMsgHdr extends BaseMsgHdr {

    public static class Layout extends StructLayout {
        protected Layout(Runtime runtime) {
            super(runtime);
        }

        public final Pointer msg_name = new Pointer();
        public final socklen_t msg_namelen = new socklen_t();
        public final Pointer msg_iov = new Pointer();
        public final Signed32 msg_iovlen = new Signed32();
        public final Pointer msg_control = new Pointer();
        public final socklen_t msg_controllen = new socklen_t();
        public final Signed32 msg_flags = new Signed32();
    }

    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    protected MacOSMsgHdr(NativePOSIX posix) {
        super(posix, layout);
        setName(null);
    }

    CmsgHdr allocateCmsgHdrInternal(NativePOSIX posix, Pointer pointer, int len) {
        if (len > 0) {
            return new MacOSCmsgHdr(posix, pointer, len);
        } else {
            return new MacOSCmsgHdr(posix, pointer);
        }
    }

    @Override
    void setControlPointer(Pointer control) {
        layout.msg_control.set(this.memory, control);
    }

    @Override
    void setControlLen(int len) {
        layout.msg_controllen.set(this.memory, len);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("msghdr {\n");
        buf.append("  msg_name=").append(getName()).append(",\n");
        buf.append("  msg_namelen=").append(getNameLen()).append(",\n");

        buf.append("  msg_iov=[\n");
        Pointer iovp = layout.msg_iov.get(this.memory);

        int numIov = getIovLen();
        for (int i = 0; i < numIov; ++i) {
            Pointer eachp = iovp.slice(i * BaseIovec.layout.size());
            buf.append(new BaseIovec(posix, eachp).toString("    "));
            if (i < (numIov - 1)) {
                buf.append(",\n");
            } else {
                buf.append("\n");
            }
        }
        buf.append("  ],\n");

        buf.append("  msg_control=[\n");

        CmsgHdr[] controls = getControls();
        for (int i = 0; i < controls.length; ++i) {
            buf.append(((MacOSCmsgHdr) controls[i]).toString("    "));
            if (i < controls.length - 1) {
                buf.append(",\n");
            } else {
                buf.append("\n");
            }
        }
        buf.append("  ],\n");
        buf.append("  msg_controllen=").append(layout.msg_controllen.get(this.memory)).append("\n");

        buf.append("  msg_iovlen=").append(getIovLen()).append(",\n");
        buf.append("  msg_flags=").append(getFlags()).append(",\n");
        buf.append("}");
        return buf.toString();
    }

    @Override
    void setNamePointer(Pointer name) {
        layout.msg_name.set( this.memory, name );
    }

    @Override
    Pointer getNamePointer() {
        return layout.msg_name.get( this.memory );
    }


    @Override
    void setNameLen(int len) {
        layout.msg_namelen.set(this.memory, len);
    }

    @Override
    int getNameLen() {
        return (int) layout.msg_namelen.get(this.memory);
    }

    @Override
    void setIovPointer(Pointer iov) {
        layout.msg_iov.set(this.memory, iov);
    }

    @Override
    Pointer getIovPointer() {
        return layout.msg_iov.get( this.memory );
    }

    @Override
    void setIovLen(int len) {
        layout.msg_iovlen.set(this.memory, len);
    }

    @Override
    int getIovLen() {
        return layout.msg_iovlen.get(this.memory);
    }

    @Override
    Pointer getControlPointer() {
        return layout.msg_control.get(this.memory);
    }

    public int getControlLen() {
        return (int) layout.msg_controllen.get(this.memory);
    }

    public void setFlags(int flags) {
        layout.msg_flags.set(this.memory, flags);
    }

    public int getFlags() {
        return layout.msg_flags.get(this.memory);
    }
}
