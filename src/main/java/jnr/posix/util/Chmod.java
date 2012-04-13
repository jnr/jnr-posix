package jnr.posix.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Honor semantics of chmod as best we can in pure Java.  Note, this uses reflection to be 
 * more tolerant of different Java versions. 
 */
public class Chmod {
    private static final boolean CHMOD_API_AVAILABLE;
    private static final Method setWritable;
    private static final Method setReadable;
    private static final Method setExecutable;
    
    static {
        boolean apiAvailable = false;
        Method setWritableVar = null;
        Method setReadableVar = null;
        Method setExecutableVar = null;
        try {
            setWritableVar = File.class.getMethod("setWritable", new Class[] {Boolean.TYPE, Boolean.TYPE});
            setReadableVar = File.class.getMethod("setReadable", new Class[] {Boolean.TYPE, Boolean.TYPE});
            setExecutableVar = File.class.getMethod("setExecutable", new Class[] {Boolean.TYPE, Boolean.TYPE});
            apiAvailable = true;
        } catch (Exception e) {
            // failed to load methods, no chmod API available
        }
        setWritable = setWritableVar;
        setReadable = setReadableVar;
        setExecutable = setExecutableVar;
        CHMOD_API_AVAILABLE = apiAvailable;
    }
    
    public static int chmod(File file, String mode) {
        if (CHMOD_API_AVAILABLE) {
            // fast version
            char other = '0';
            if (mode.length() >= 1) {
                other = mode.charAt(mode.length() - 1);
            }
            //char group = mode.charAt(mode.length() - 2);
            char user = '0';
            if (mode.length() >= 3) {
                user = mode.charAt(mode.length() - 3);
            }
            //char setuidgid = mode.charAt(mode.length() - 3);
            
            // group and setuid/gid are ignored, no way to do them fast. Should we fall back on slow?
            if (!setPermissions(file, other, false)) return -1;
            if (!setPermissions(file, user, true)) return -1;
            return 0;
        } else {
            // slow version
            try {
                Process chmod = Runtime.getRuntime().exec("/bin/chmod " + mode + " " + file.getAbsolutePath());
                chmod.waitFor();
                return chmod.exitValue();
            } catch (IOException ioe) {
                // FIXME: ignore?
            } catch (InterruptedException ie) {
                // FIXME: ignore?
            }
        }
        return -1;
    }
    
    private static boolean setPermissions(File file, char permChar, boolean userOnly) {
        int permValue = Character.digit(permChar, 8);
        
        try {
            if ((permValue & 1) != 0) {
                setExecutable.invoke(file, new Object[] {Boolean.TRUE, Boolean.valueOf(userOnly)});
            } else {
                setExecutable.invoke(file, new Object[] {Boolean.FALSE, Boolean.valueOf(userOnly)});
            }
            
            if ((permValue & 2) != 0) {
                setWritable.invoke(file, new Object[] {Boolean.TRUE, Boolean.valueOf(userOnly)});
            } else {
                setWritable.invoke(file, new Object[] {Boolean.FALSE, Boolean.valueOf(userOnly)});
            }
            
            if ((permValue & 4) != 0) {
                setReadable.invoke(file, new Object[] {Boolean.TRUE, Boolean.valueOf(userOnly)});
            } else {
                setReadable.invoke(file, new Object[] {Boolean.FALSE, Boolean.valueOf(userOnly)});
            }
            
            return true;
        } catch (IllegalAccessException iae) {
            // ignore, return false below
        } catch (InvocationTargetException ite) {
            // ignore, return false below
        }
        
        return false;
    }
}
