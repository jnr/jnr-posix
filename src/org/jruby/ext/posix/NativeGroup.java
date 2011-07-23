package org.jruby.ext.posix;

import jnr.ffi.Struct;

public abstract class NativeGroup extends Struct implements Group {
    NativeGroup(jnr.ffi.Runtime runtime) {
        super(runtime);
    }
}
