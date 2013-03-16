package jnr.posix;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleFunctionMapper implements jnr.ffi.mapper.FunctionMapper {
    private final Map<String, String> functionNameMap;
    
    private SimpleFunctionMapper(Map<String, String> map) {
        functionNameMap = Collections.unmodifiableMap(new HashMap<String, String>(map));
    }
    
    public String mapFunctionName(String functionName, Context context) {
        String nativeFunction = functionNameMap.get(functionName);
        return nativeFunction != null ? nativeFunction : functionName;
    }
    
    public static class Builder {
        private final Map<String, String> functionNameMap = Collections.synchronizedMap(new HashMap<String, String>());

        public Builder map(String posixName, String nativeFunction) {
            functionNameMap.put(posixName, nativeFunction);
            return this;
        }
        
        public SimpleFunctionMapper build() {
            return new SimpleFunctionMapper(functionNameMap);        
        }
    } 
}
