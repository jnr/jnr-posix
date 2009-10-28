package org.jruby.ext.posix;

import com.kenai.jaffl.Library;
import com.kenai.jaffl.LibraryOption;
import java.util.HashMap;
import org.jruby.ext.posix.util.Platform;
import java.util.Map;

public class POSIXFactory {
    static final String LIBC = Platform.IS_LINUX ? "libc.so.6" : Platform.IS_WINDOWS ? "msvcrt" : "c";
    static final Map<LibraryOption, Object> defaultOptions = new HashMap<LibraryOption, Object>() {{
        put(LibraryOption.TypeMapper, POSIXTypeMapper.INSTANCE);
        put(LibraryOption.LoadNow, Boolean.TRUE);
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
                } else if (Platform.IS_SOLARIS) {
                    posix = loadSolarisPOSIX(handler);
                } else if (Platform.IS_32_BIT) {// No 64 bit structures defined yet.
                    if (Platform.IS_WINDOWS) {
                        posix = loadWindowsPOSIX(handler);
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
        return new LinuxPOSIX(LIBC, new LinuxLibCProvider(), handler);
    }

    public static POSIX loadMacOSPOSIX(POSIXHandler handler) {
        return new MacOSPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadSolarisPOSIX(POSIXHandler handler) {
        return new SolarisPOSIX(LIBC, new SolarisLibCProvider(), handler);
    }

    public static POSIX loadFreeBSDPOSIX(POSIXHandler handler) {
        return new FreeBSDPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadOpenBSDPOSIX(POSIXHandler handler) {
        return new OpenBSDPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadWindowsPOSIX(POSIXHandler handler) {
        return new WindowsPOSIX(LIBC, new WindowsLibCProvider(), handler);
    }


    private static final class DefaultLibCProvider implements LibCProvider {

        private static final class SingletonHolder {
            public static LibC libc = Library.loadLibrary(LibC.class, defaultOptions, "c");
        }

        public final LibC getLibC() {
            return SingletonHolder.libc;
        }
    }

    private static final class LinuxLibCProvider implements LibCProvider {

        private static final class SingletonHolder {
            public static LibC libc = Library.loadLibrary(LinuxLibC.class, defaultOptions, "libc.so.6");
        }

        public final LibC getLibC() {
            return SingletonHolder.libc;
        }
    }

    private static final class SolarisLibCProvider implements LibCProvider {

        private static final class SingletonHolder {
            public static LibC libc = Library.loadLibrary(LibC.class, defaultOptions, "socket", "nsl", "c");
        }

        public final LibC getLibC() {
            return SingletonHolder.libc;
        }
    }

    private static final class WindowsLibCProvider implements LibCProvider {
        
        static final class SingletonHolder {
            public static LibC libc = Library.loadLibrary(WindowsLibC.class, getOptions(),  "msvcrt", "kernel32");
        }

        public final LibC getLibC() {
            return SingletonHolder.libc;
        }

        static final Map<LibraryOption, Object> getOptions() {
            Map<LibraryOption, Object> options = new HashMap<LibraryOption, Object>(defaultOptions);
            options.put(LibraryOption.FunctionMapper, new WindowsLibCFunctionMapper());
            return options;
        }
    }
}
