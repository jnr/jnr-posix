package org.jruby.ext.posix;


import com.kenai.jaffl.Library;
import com.kenai.jaffl.LibraryOption;
import java.util.HashMap;
import org.jruby.ext.posix.util.Platform;
import java.util.Map;

public class POSIXFactory {
    static final String LIBC = "c";
    static LibC libc = null;
    static final Map<LibraryOption, Object> defaultOptions = new HashMap<LibraryOption, Object>() {{
        put(LibraryOption.TypeMapper, POSIXTypeMapper.INSTANCE);
    }};

    public static POSIX getPOSIX(POSIXHandler handler, boolean useNativePOSIX) {
        POSIX posix = null;

        if (useNativePOSIX) {
            try {
                if (Platform.IS_MAC) {
                    posix = loadMacOSPOSIX(handler);
                } else if (Platform.IS_LINUX) {
                    posix = loadLinuxPOSIX(handler);
                } else if (Platform.IS_FREEBSD) {
                    posix = loadFreeBSDPOSIX(handler);
                } else if (Platform.IS_OPENBSD) {
                    posix = loadOpenBSDPOSIX(handler);
                } else if (Platform.IS_32_BIT) {// No 64 bit structures defined yet.
                    if (Platform.IS_WINDOWS) {
                        posix = loadWindowsPOSIX(handler);
                    } else if (Platform.IS_SOLARIS) {
                        posix = loadSolarisPOSIX(handler);
                    }
                }

                // ENEBO: Should printing be done through a handler+log method?
                if (handler.isVerbose()) {
                    if (posix != null) {
                        System.err.println("Successfully loaded native POSIX impl.");
                    } else {
                        System.err.println("Failed to load native POSIX impl; falling back on Java impl. Unsupported OS.");
                    }
                }
            } catch (Throwable t) {
                if (handler.isVerbose()) {
                    System.err.println("Failed to load native POSIX impl; falling back on Java impl. Stacktrace follows.");
                    t.printStackTrace();
                }
            }
        }

        if (posix == null) {
            posix = getJavaPOSIX(handler);
        }

        return posix;
    }

    public static POSIX getJavaPOSIX(POSIXHandler handler) {
        return new JavaPOSIX(handler);
    }

    public static POSIX loadLinuxPOSIX(POSIXHandler handler) {
        return new LinuxPOSIX(LIBC, loadLibC(LIBC, LinuxLibC.class, defaultOptions), handler);
    }

    public static POSIX loadMacOSPOSIX(POSIXHandler handler) {
        return new MacOSPOSIX(LIBC, loadLibC(LIBC, LibC.class, defaultOptions), handler);
    }

    public static POSIX loadSolarisPOSIX(POSIXHandler handler) {
        return new SolarisPOSIX(LIBC, loadLibC(LIBC, LibC.class, defaultOptions), handler);
    }

    public static POSIX loadFreeBSDPOSIX(POSIXHandler handler) {
        return new FreeBSDPOSIX(LIBC, loadLibC(LIBC, LibC.class, defaultOptions), handler);
    }

    public static POSIX loadOpenBSDPOSIX(POSIXHandler handler) {
        return new OpenBSDPOSIX(LIBC, loadLibC(LIBC, LibC.class, defaultOptions), handler);
    }

    public static POSIX loadWindowsPOSIX(POSIXHandler handler) {
        String name = "msvcrt";

        Map<LibraryOption, Object> options = new HashMap<LibraryOption, Object>();
        options.put(LibraryOption.FunctionMapper, new WindowsLibCFunctionMapper());

        return new WindowsPOSIX(name, loadLibC(name, LibC.class, options), handler);
    }

    public static LibC loadLibC(String libraryName, Class<? extends LibC> libCClass, Map<LibraryOption, Object> options) {
        if (libc != null) return libc;

        libc = Library.loadLibrary(libraryName, libCClass, options);

        return libc;
    }
    
    
    
}
