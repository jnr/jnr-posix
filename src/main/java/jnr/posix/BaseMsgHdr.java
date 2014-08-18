package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.StructLayout;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bob McWhirter
 */
public abstract class BaseMsgHdr implements MsgHdr {

    protected final NativePOSIX posix;
    protected final Pointer memory;

    protected BaseMsgHdr(NativePOSIX posix, StructLayout layout) {
        this.posix = posix;
        this.memory = posix.getRuntime().getMemoryManager().allocateTemporary(layout.size(), true);
    }

    public void setName(String name) {
        if (name == null) {
            setNamePointer(null);
            setNameLen(0);
            return;
        }
        byte[] nameBytes = name.getBytes(Charset.forName("US-ASCII"));
        Pointer p = Runtime.getSystemRuntime().getMemoryManager().allocateTemporary(nameBytes.length, true);
        p.put(0, nameBytes, 0, nameBytes.length);
        setNamePointer(p);
        setNameLen(nameBytes.length);
    }

    public String getName() {
        Pointer ptr = getNamePointer();
        if (ptr == null) {
            return null;
        }
        return ptr.getString(0, getNameLen(), Charset.forName( "US-ASCII" ));
    }

    public CmsgHdr allocateControl(int dataLength) {
        CmsgHdr[] controls = allocateControls(new int[]{dataLength});
        return controls[0];
    }

    public CmsgHdr[] allocateControls(int[] dataLengths) {
        CmsgHdr[] cmsgs = new CmsgHdr[dataLengths.length];

        int totalSize = 0;
        for (int i = 0; i < dataLengths.length; ++i) {
            totalSize += posix.socketMacros().CMSG_SPACE(dataLengths[i]);
        }

        Pointer ptr = posix.getRuntime().getMemoryManager().allocateDirect(totalSize);

        int offset = 0;
        for (int i = 0; i < dataLengths.length; ++i) {
            int eachLen = posix.socketMacros().CMSG_SPACE(dataLengths[i]);
            CmsgHdr each = allocateCmsgHdrInternal(posix, ptr.slice(offset, eachLen), eachLen);
            cmsgs[i] = each;
            offset += eachLen;
        }

        setControlPointer(ptr);
        setControlLen(totalSize);

        return cmsgs;
    }

    public CmsgHdr[] getControls() {
        int len = getControlLen();
        if (len == 0) {
            return new CmsgHdr[0];
        }

        List<CmsgHdr> control = new ArrayList<CmsgHdr>();

        int offset = 0;

        Pointer controlPtr = getControlPointer();

        while (offset < len) {
            CmsgHdr each = allocateCmsgHdrInternal(posix, controlPtr.slice(offset), -1);
            offset += each.getLen();
            control.add(each);
        }

        return control.toArray(new CmsgHdr[control.size()]);
    }

    public void setIov(ByteBuffer[] buffers) {
        Pointer iov = Runtime.getSystemRuntime().getMemoryManager().allocateDirect(BaseIovec.layout.size() * buffers.length);

        for (int i = 0; i < buffers.length; ++i) {
            Pointer eachIovecPtr = iov.slice(BaseIovec.layout.size() * i);
            BaseIovec eachIovec = new BaseIovec(posix, eachIovecPtr);
            eachIovec.set(buffers[i]);
        }

        setIovPointer(iov);
        setIovLen(buffers.length);
    }

    public ByteBuffer[] getIov() {
        int len = getIovLen();

        ByteBuffer[] buffers = new ByteBuffer[len];

        Pointer iov = getIovPointer();

        for (int i = 0; i < len; ++i) {
            Pointer eachPtr = iov.slice(BaseIovec.layout.size() * i);
            BaseIovec eachIov = new BaseIovec(posix, eachPtr);
            buffers[i] = eachIov.get();
        }

        return buffers;
    }

    abstract void setNamePointer(Pointer name);

    abstract Pointer getNamePointer();

    abstract void setNameLen(int len);

    abstract int getNameLen();

    abstract void setIovPointer(Pointer iov);

    abstract Pointer getIovPointer();

    abstract int getIovLen();

    abstract void setIovLen(int len);

    abstract CmsgHdr allocateCmsgHdrInternal(NativePOSIX posix, Pointer pointer, int len);

    abstract void setControlPointer(Pointer control);

    abstract Pointer getControlPointer();

    abstract void setControlLen(int len);

}
