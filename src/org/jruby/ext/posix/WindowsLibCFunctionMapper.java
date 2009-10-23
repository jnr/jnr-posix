/*
 * POSIXFunctionMapper.java
 */

package org.jruby.ext.posix;

import com.kenai.jaffl.mapper.FunctionMapper;
import java.util.HashMap;
import java.util.Map;

public class WindowsLibCFunctionMapper implements FunctionMapper {
    private Map<String, String> methodNameMap;

    public WindowsLibCFunctionMapper() {
        methodNameMap = new HashMap<String, String>();
        
        methodNameMap.put("getpid", "_getpid");
        methodNameMap.put("chmod", "_chmod");
        methodNameMap.put("fstat", "_fstat64");
        methodNameMap.put("stat", "_stat64");
        methodNameMap.put("mkdir", "_mkdir");
        methodNameMap.put("umask", "_umask");
	methodNameMap.put("isatty", "_isatty");
    }
  
    public String mapFunctionName(String originalName, Context context) {
        String name = methodNameMap.get(originalName);
        
        return name != null ? name : originalName; 
    }
}
