package jnr.posix;


import jnr.ffi.mapper.FromNativeConverter;
import jnr.ffi.mapper.ToNativeConverter;
import jnr.ffi.mapper.TypeMapper;
import jnr.posix.util.Platform;

final class POSIXTypeMapper implements TypeMapper {
    public static final TypeMapper INSTANCE = new POSIXTypeMapper();
    
    private POSIXTypeMapper() {}
    
    public FromNativeConverter getFromNativeConverter(Class klazz) {
        if (Passwd.class.isAssignableFrom(klazz)) {
            if (Platform.IS_MAC) {
                return MacOSPOSIX.PASSWD;
            } else if (Platform.IS_LINUX) {
                return LinuxPOSIX.PASSWD;
            } else if (Platform.IS_SOLARIS) {
                return SolarisPOSIX.PASSWD;
            } else if (Platform.IS_FREEBSD) {
                return FreeBSDPOSIX.PASSWD;
            } else if (Platform.IS_OPENBSD) {
                return OpenBSDPOSIX.PASSWD;
            }
            return null;
        } else if (Group.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.GROUP;
        }
        
        return null;
    }
    
    public ToNativeConverter getToNativeConverter(Class klazz) {
        if (FileStat.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.FileStatConverter;

        } else if (NativeTimes.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.TimesConverter;
        }

        return null;
    }
}
