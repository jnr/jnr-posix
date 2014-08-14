package jnr.posix;


import jnr.constants.Constant;
import jnr.ffi.mapper.*;
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
            } else if (Platform.IS_WINDOWS) {
                return WindowsPOSIX.PASSWD;
            } else if (jnr.ffi.Platform.getNativePlatform().getOS().equals(jnr.ffi.Platform.OS.AIX)) {
                return AixPOSIX.PASSWD;
            }
            return null;
        } else if (Group.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.GROUP;

        } else if (HANDLE.class.isAssignableFrom(klazz)) {
            return HANDLE.Converter;
        }
        
        return null;
    }
    
    public ToNativeConverter getToNativeConverter(Class klazz) {
        if (FileStat.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.FileStatConverter;

        } else if (NativeTimes.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.TimesConverter;

        } else if (Constant.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.ConstantConverter;

        } else if (WString.class.isAssignableFrom(klazz)) {
            return WString.Converter;

        } else if (HANDLE.class.isAssignableFrom(klazz)) {
            return HANDLE.Converter;
        } else if (MsgHdr.class.isAssignableFrom(klazz)) {
            return BaseNativePOSIX.MsgHdrConverter;
        }

        return null;
    }

    public final ToNativeConverter getToNativeConverter(Class klazz, ToNativeContext context) {
        return getToNativeConverter(klazz);
    }

    public final FromNativeConverter getFromNativeConverter(Class klazz, FromNativeContext context) {
        return getFromNativeConverter(klazz);
    }
}
