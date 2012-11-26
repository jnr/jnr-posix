package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.mapper.DataConverter;
import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.mapper.ToNativeContext;

public final class HANDLE {
    public static final long INVALID_HANDLE_VALUE = -1L;
    private final Pointer pointer;

    public HANDLE(Pointer pointer) {
        this.pointer = pointer;
    }

    public final Pointer toPointer() {
        return pointer;
    }

    public final boolean isValid() {
        return pointer.address() != (INVALID_HANDLE_VALUE & jnr.ffi.Runtime.getSystemRuntime().addressMask());
    }

    public static HANDLE valueOf(Pointer value) {
        return new HANDLE(value);
    }

    public static HANDLE valueOf(long value) {
        return new HANDLE(jnr.ffi.Runtime.getSystemRuntime().getMemoryManager().newPointer(value));
    }

    public static final DataConverter<HANDLE, Pointer> Converter = new DataConverter<HANDLE, Pointer>() {

        public Pointer toNative(HANDLE value, ToNativeContext context) {
            return value != null ? value.pointer : null;
        }

        public HANDLE fromNative(Pointer nativeValue, FromNativeContext context) {
            return nativeValue != null ? new HANDLE(nativeValue) : null;
        }

        public Class<Pointer> nativeType() {
            return Pointer.class;
        }
    };
}
