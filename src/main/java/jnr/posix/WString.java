package jnr.posix;

import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.ToNativeContext;
import jnr.ffi.mapper.ToNativeConverter;
import jnr.posix.util.WindowsHelpers;

public final class WString {
    static final jnr.ffi.Runtime runtime = jnr.ffi.Runtime.getSystemRuntime();

    private final byte[] bytes;

    WString(String string) {
        bytes = WindowsHelpers.toWString(string);
    }

    private WString(byte[] bytes) {
        this.bytes = bytes;
    }

    public static WString path(String path) {
        return new WString(path(path, false));
    }

    public static byte[] path(String path, boolean longPathExtensionNeeded) {
        if (longPathExtensionNeeded && path.length() > 240) { // FIXME: This is not right value.  Needs tests around actual char boundary.
            if (path.startsWith("//")) { // UNC Path
                path = "//?/UNC/" + path.substring(2);
            } else if (path.startsWith("\\\\")) {
                path = "\\\\?\\UNC\\" + path.substring(2);
            } else if (WindowsHelpers.isDriveLetterPath(path)) {
                if (path.contains("/")) {
                    path = "//?/" + path;
                } else {
                    path = "\\\\?\\" + path;
                }
            }
        }

        return WindowsHelpers.toWPath(path);
    }

    public static final ToNativeConverter<WString, Pointer> Converter = new ToNativeConverter<WString, Pointer>() {

        public Pointer toNative(WString value, ToNativeContext context) {
            if (value == null) {
                return null;
            }

            Pointer memory = Memory.allocateDirect(runtime, value.bytes.length + 1, true);
            memory.put(0, value.bytes, 0, value.bytes.length);
            return memory;
        }

        public Class<Pointer> nativeType() {
            return Pointer.class;
        }
    };
}
