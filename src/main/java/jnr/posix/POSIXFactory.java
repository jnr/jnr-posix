package jnr.posix;

import jnr.ffi.Library;
import jnr.ffi.LibraryOption;
import jnr.ffi.Struct;

import java.util.Collections;
import java.util.HashMap;

import jnr.ffi.mapper.FunctionMapper;
import jnr.posix.util.DefaultPOSIXHandler;
import jnr.posix.util.Platform;

import java.util.Map;

public class POSIXFactory {
    // Weird inner-class resolution problem work-around FIXME: JRUBY-5889.  Someone fix JAFFL!
    private static final Class<Struct> BOGUS_HACK = Struct.class;
    static final String LIBC = Platform.IS_LINUX ? "libc.so.6" : Platform.IS_WINDOWS ? "msvcrt" : "c";
    
    public static POSIX getPOSIX(POSIXHandler handler, boolean useNativePOSIX) {
        return new LazyPOSIX(handler, useNativePOSIX);
    }

    /**
     * This will use {@link DefaultPOSIXHandler} and the native POSIX implementation
     *
     * @return a POSIX implementation
     */
    public static POSIX getPOSIX() {
        return getPOSIX(new DefaultPOSIXHandler(), true);
    }

    static POSIX loadPOSIX(POSIXHandler handler, boolean useNativePOSIX) {
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
                } else if (Platform.IS_WINDOWS) {
                    posix = loadWindowsPOSIX(handler);
                } else if (jnr.ffi.Platform.OS.AIX.equals(jnr.ffi.Platform.getNativePlatform().getOS())) {
                    posix = loadAixPOSIX(handler);
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
        return new LinuxPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadMacOSPOSIX(POSIXHandler handler) {
        return new MacOSPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadSolarisPOSIX(POSIXHandler handler) {
        return new SolarisPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadFreeBSDPOSIX(POSIXHandler handler) {
        return new FreeBSDPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadOpenBSDPOSIX(POSIXHandler handler) {
        return new OpenBSDPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadWindowsPOSIX(POSIXHandler handler) {
        return new WindowsPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }

    public static POSIX loadAixPOSIX(POSIXHandler handler) {
        return new AixPOSIX(LIBC, new DefaultLibCProvider(), handler);
    }
    
    private static String[] libraries() {
        switch (jnr.ffi.Platform.getNativePlatform().getOS()) {
            case LINUX:
                return new String[] { "libc.so.6" };
            
            case SOLARIS:
                return new String[] { "socket", "nsl", "c" };
            
            case AIX:
                return new String[] { "libc.a(shr.o)" };
            
            case WINDOWS:
                return new String[] { "msvcrt", "kernel32" };
            
            default:
                return new String[] { "c" };
        }
    }
    
    private static Class<? extends LibC> libraryInterface() {
        switch (jnr.ffi.Platform.getNativePlatform().getOS()) {
            case LINUX:
                return LinuxLibC.class;
            
            case AIX:
                return AixLibC.class;
            
            case SOLARIS:
                return SolarisLibC.class;

            case WINDOWS:
                return WindowsLibC.class;

            default:
                return UnixLibC.class;
        }
    }

    private static FunctionMapper functionMapper() {
        switch (jnr.ffi.Platform.getNativePlatform().getOS()) {
            case AIX:
                return new SimpleFunctionMapper.Builder()
                        .map("stat", "stat64x")
                        .map("fstat", "fstat64x")
                        .map("lstat", "lstat64x")
                        .map("stat64", "stat64x")
                        .map("fstat64", "fstat64x")
                        .map("lstat64", "lstat64x")
                        .build();
            
            case WINDOWS:
                return new SimpleFunctionMapper.Builder()
                        .map("getpid", "_getpid")
                        .map("chmod", "_chmod")
                        .map("fstat", "_fstat64")
                        .map("stat", "_stat64")
                        .map("umask", "_umask")
                        .map("isatty", "_isatty")
                        .map("read", "_read")
                        .map("write", "_write")
                        .map("close", "_close")
                        .build();
            default:
                return null;
        }
    }
    
    private static Map<LibraryOption, Object> options() {
        Map<LibraryOption, Object> options = new HashMap<LibraryOption, Object>();
        
        FunctionMapper functionMapper = functionMapper();
        if (functionMapper != null) {
            options.put(LibraryOption.FunctionMapper, functionMapper);
        }

        options.put(LibraryOption.TypeMapper, POSIXTypeMapper.INSTANCE);
        options.put(LibraryOption.LoadNow, Boolean.TRUE);
        
        return Collections.unmodifiableMap(options);
    }


    private static final class DefaultLibCProvider implements LibCProvider {

        private static final class SingletonHolder {
            public static LibC libc = Library.loadLibrary(libraryInterface(), options(), libraries());
        }

        public final LibC getLibC() {
            return SingletonHolder.libc;
        }
    }
}
