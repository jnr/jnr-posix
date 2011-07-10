package org.jruby.ext.posix;

import jnr.ffi.struct.Struct;

public class FileTime extends Struct {
  public final Unsigned32 dwLowDateTime = new Unsigned32();
  public final Unsigned32 dwHighDateTime = new Unsigned32();
}
