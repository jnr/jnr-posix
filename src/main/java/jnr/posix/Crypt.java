package jnr.posix;

import jnr.ffi.Pointer;

public interface Crypt {
    CharSequence crypt(CharSequence key, CharSequence salt);
    Pointer crypt(byte[] key, byte[] salt);
}
