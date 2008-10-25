/*
 * POSIXFunctionMapper.java
 */

package org.jruby.ext.posix;

import com.kenai.jaffl.mapper.FunctionMapper;

public class POSIXFunctionMapper implements FunctionMapper {

    public POSIXFunctionMapper() {}
  
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
