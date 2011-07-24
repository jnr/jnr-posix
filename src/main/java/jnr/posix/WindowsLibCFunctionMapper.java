
package jnr.posix;

import jnr.ffi.mapper.FunctionMapper;
import java.util.HashMap;
import java.util.Map;

final class WindowsLibCFunctionMapper implements FunctionMapper {

    static final FunctionMapper INSTANCE = new WindowsLibCFunctionMapper();

    private final Map<String, String> methodNameMap;

    WindowsLibCFunctionMapper() {
        methodNameMap = new HashMap<String, String>();

        methodNameMap.put("getpid", "_getpid");
        methodNameMap.put("chmod", "_chmod");
        methodNameMap.put("fstat", "_fstat64");
        methodNameMap.put("stat", "_stat64");
        methodNameMap.put("umask", "_umask");
	methodNameMap.put("isatty", "_isatty");
	methodNameMap.put("read", "_read");
	methodNameMap.put("write", "_write");
	methodNameMap.put("close", "_close");
    }
  
    public String mapFunctionName(String originalName, Context context) {
        String name = methodNameMap.get(originalName);
        
        return name != null ? name : originalName; 
    }
}
