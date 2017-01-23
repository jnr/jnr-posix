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

    /**
     * Get a POSIX instance. If useNativePosix is true, this works just like
     * POSIXFactory#getPOSIX(). If useNativePosix is false, this works like
     * POSIXFactory#getJavaPOSIX()
     *
     * @param handler a POSIXHandler implementation
     * @param useNativePOSIX whether to attempt to use native code for better functionality
     * @return a POSIX implementation, attempting to use native code if useNativePosix is true
     */
    public static POSIX getPOSIX(POSIXHandler handler, boolean useNativePOSIX) {
        return new LazyPOSIX(handler, useNativePOSIX);
    }

    /**
     * This will use {@link DefaultPOSIXHandler} and the native POSIX implementation,
     * falling back on the pure-Java implementation if native support is not available.
     *
     * @return a POSIX implementation, native if possible and pure-Java otherwise.
     */
    public static POSIX getPOSIX() {
        return getPOSIX(new DefaultPOSIXHandler(), true);
    }

    /**
     * Get a pure-Java POSIX instance. Functionality will be limited to that which can
     * be provided by pure-Java/JDK features or shelling out to external commands.
     *
     * @param handler a POSIXHandler implementation
     * @return a pure-Java POSIX implementation
     */
    public static POSIX getJavaPOSIX(POSIXHandler handler) {
        return new JavaPOSIX(handler);
    }

    /**
     * Get a pure-Java POSIX instance. Functionality will be limited to that which can
     * be provided by pure-Java/JDK features or shelling out to external commands.
     *
     * @return a pure-Java POSIX implementation
     */
    public static POSIX getJavaPOSIX() {
        return getJavaPOSIX(new DefaultPOSIXHandler());
    }

    /**
     * Get a POSIX instance. If a true native implementation can't be loaded, allow that
     * error to propagate rather than falling back on the pure-Java version.
     *
     * @param handler a POSIXHandler implementation
     * @return a native POSIX implementation, raising errors if the native version can't load
     */
    public static POSIX getNativePOSIX(POSIXHandler handler) {
        return loadNativePOSIX(handler);
    }

    /**
     * Get a POSIX instance. If a true native implementation can't be loaded, allow that
     * error to propagate rather than falling back on the pure-Java version.
     *
     * @return a native POSIX implementation, raising errors if the native version can't load
     */
    public static POSIX getNativePOSIX() {
        return getNativePOSIX(new DefaultPOSIXHandler());
    }

    static POSIX loadPOSIX(POSIXHandler handler, boolean useNativePOSIX) {
        POSIX posix = null;

        if (useNativePOSIX) {
            try {
                posix = loadNativePOSIX(handler);
                posix = posix != null ? new CheckedPOSIX(posix, handler) : null;
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
    
    private static POSIX loadNativePOSIX(POSIXHandler handler) {
        switch (jnr.ffi.Platform.getNativePlatform().getOS()) {
            case DARWIN:
                return loadMacOSPOSIX(handler);

            case LINUX:
                return loadLinuxPOSIX(handler);

            case FREEBSD:
                return loadFreeBSDPOSIX(handler);
            
            case OPENBSD:
                return loadOpenBSDPOSIX(handler);

            case SOLARIS:
                return loadSolarisPOSIX(handler);
            
            case AIX:
                return loadAixPOSIX(handler);
            
            case WINDOWS:
                return loadWindowsPOSIX(handler);
        }

        return null;
    }

    public static POSIX loadLinuxPOSIX(POSIXHandler handler) {
        return new LinuxPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }

    public static POSIX loadMacOSPOSIX(POSIXHandler handler) {
        return new MacOSPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }

    public static POSIX loadSolarisPOSIX(POSIXHandler handler) {
        return new SolarisPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }

    public static POSIX loadFreeBSDPOSIX(POSIXHandler handler) {
        return new FreeBSDPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }

    public static POSIX loadOpenBSDPOSIX(POSIXHandler handler) {
        return new OpenBSDPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }

    public static POSIX loadWindowsPOSIX(POSIXHandler handler) {
        return new WindowsPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }

    public static POSIX loadAixPOSIX(POSIXHandler handler) {
        return new AixPOSIX(DefaultLibCProvider.INSTANCE, handler);
    }
    
    private static String[] libraries() {
        switch (jnr.ffi.Platform.getNativePlatform().getOS()) {
            case LINUX:
                return new String[] { jnr.ffi.Platform.getNativePlatform().getStandardCLibraryName(), "libcrypt.so.1" };
            
            case SOLARIS:
                return new String[] { "socket", "nsl", jnr.ffi.Platform.getNativePlatform().getStandardCLibraryName() };

            case FREEBSD:
            case NETBSD:
                return new String[] { jnr.ffi.Platform.getNativePlatform().getStandardCLibraryName(), "crypt" };
            
            case AIX:
                return jnr.ffi.Runtime.getSystemRuntime().addressSize() == 4
                    ? new String[] { "libc.a(shr.o)" }
                    : new String[] { "libc.a(shr_64.o)" };
            
            case WINDOWS:
                return new String[] { "msvcrt", "kernel32" };
            
            default:
                return new String[] { jnr.ffi.Platform.getNativePlatform().getStandardCLibraryName() };
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
                        .map("getcwd", "_getcwd")
                        .map("unlink", "_unlink")
                        .map("access", "_access")
                        .map("open", "_open")
                        .map("dup", "_dup")
                        .map("dup2", "_dup2")
                        .map("lseek", "_lseek")
                        .map("ftruncate", "_chsize")
                        .build();
            
            case SOLARIS:
                return Platform.IS_32_BIT 
                    ? new SimpleFunctionMapper.Builder()
                        .map("stat", "stat64")
                        .map("fstat", "fstat64")
                        .map("lstat", "lstat64")
                        .build()
                    : null;
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
        public static final LibCProvider INSTANCE = new DefaultLibCProvider();

        private static final class SingletonHolder {
            public static LibC libc = Library.loadLibrary(libraryInterface(), options(), libraries());
        }

        public final LibC getLibC() {
            return SingletonHolder.libc;
        }
    }
}
