
package jnr.posix;

import jnr.ffi.mapper.FunctionMapper;

/**
 * No longer used.  It used to map function names from libc names to
 * msvcrt names.
 *
 * @deprecated Use SimpleFunctionMapper instead.
 */
@Deprecated
final class POSIXFunctionMapper implements FunctionMapper {
    public static final FunctionMapper INSTANCE = new POSIXFunctionMapper();

    private POSIXFunctionMapper() {}
  
    public String mapFunctionName(String name, Context ctx) {
        if (ctx.getLibrary().getName().equals("msvcrt")) {
            // FIXME: We should either always _ name for msvcrt or get good list of _ methods
            if (name.equals("getpid") || name.equals("chmod")) {
                name = "_" + name;
            }
        }
        return name;
    }

}
