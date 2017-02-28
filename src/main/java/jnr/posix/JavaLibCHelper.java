 /*
 **** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2007 
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package jnr.posix;

import static jnr.constants.platform.Errno.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import jnr.constants.platform.Errno;
import jnr.posix.util.Chmod;
import jnr.posix.util.ExecIt;
import jnr.posix.util.FieldAccess;
import jnr.posix.util.JavaCrypt;
import jnr.posix.util.Platform;

 /**
 * This libc implementation is created one per runtime instance versus the others which
 * are expected to be one static instance for whole JVM.  Because of this it is no big
 * deal to make reference to a POSIXHandler directly.
 */
// FIXME: we ignore all exceptions with shell launcher...should we do something better
public class JavaLibCHelper {
    public static final int STDIN = 0;
    public static final int STDOUT = 1;
    public static final int STDERR = 2;

    private static final ThreadLocal<Integer> errno = new ThreadLocal<Integer>();

    private final POSIXHandler handler;
    private final Map<String, String> env;
    
    private static final Class SEL_CH_IMPL;
    private static final Method SEL_CH_IMPL_GET_FD;
    private static final Class FILE_CHANNEL_IMPL;
    private static final Field FILE_CHANNEL_IMPL_FD;
    private static final Field FILE_DESCRIPTOR_FD;
    private static final Field FILE_DESCRIPTOR_HANDLE;

    public JavaLibCHelper(POSIXHandler handler) {
        this.env = new HashMap<String, String>();
        this.handler = handler;
    }
    
    static {
        Method getFD;
        Class selChImpl;
        try {
            selChImpl = Class.forName("sun.nio.ch.SelChImpl");
            try {
                getFD = selChImpl.getMethod("getFD");
                getFD.setAccessible(true);
            } catch (Exception e) {
                getFD = null;
            }
        } catch (Exception e) {
            selChImpl = null;
            getFD = null;
        }
        SEL_CH_IMPL = selChImpl;
        SEL_CH_IMPL_GET_FD = getFD;
        
        Field fd;
        Class fileChannelImpl;
        try {
            fileChannelImpl = Class.forName("sun.nio.ch.FileChannelImpl");
            try {
                fd = fileChannelImpl.getDeclaredField("fd");
                fd.setAccessible(true);
            } catch (Exception e) {
                fd = null;
            }
        } catch (Exception e) {
            fileChannelImpl = null;
            fd = null;
        }
        FILE_CHANNEL_IMPL = fileChannelImpl;
        FILE_CHANNEL_IMPL_FD = fd;
        
        Field ffd;
        try {
            ffd = FileDescriptor.class.getDeclaredField("fd");
            ffd.setAccessible(true);
        } catch (Exception e) {
            ffd = null;
        }
        FILE_DESCRIPTOR_FD = ffd;

        if (Platform.IS_WINDOWS) {
            Field handle;
            try {
                handle = FileDescriptor.class.getDeclaredField("handle");
                handle.setAccessible(true);
            } catch (Exception e) {
                handle = null;
            }
            FILE_DESCRIPTOR_HANDLE = handle;
        } else {
            FILE_DESCRIPTOR_HANDLE = null;
        }
    }
    
    public static FileDescriptor getDescriptorFromChannel(Channel channel) {
        if (SEL_CH_IMPL_GET_FD != null && SEL_CH_IMPL.isInstance(channel)) {
            // Pipe Source and Sink, Sockets, and other several other selectable channels
            try {
                return (FileDescriptor)SEL_CH_IMPL_GET_FD.invoke(channel);
            } catch (Exception e) {
                // return bogus below
            }
        } else if (FILE_CHANNEL_IMPL_FD != null && FILE_CHANNEL_IMPL.isInstance(channel)) {
            // FileChannels
            try {
                return (FileDescriptor)FILE_CHANNEL_IMPL_FD.get(channel);
            } catch (Exception e) {
                // return bogus below
            }
        } else if (FILE_DESCRIPTOR_FD != null) {
            // anything else that implements a getFD method that returns an int
            FileDescriptor unixFD = new FileDescriptor();
            
                try {
                    Method getFD = channel.getClass().getMethod("getFD");
                    FILE_DESCRIPTOR_FD.set(unixFD, (Integer)getFD.invoke(channel));
                    return unixFD;
                } catch (Exception e) {
                    // return bogus below
                }
        }
        return new FileDescriptor();
    }

    static int errno() {
        Integer errno = JavaLibCHelper.errno.get();
        return errno != null ? errno : 0;
    }

    static void errno(int errno) {
        JavaLibCHelper.errno.set(errno);
    }

    static void errno(Errno errno) {
        JavaLibCHelper.errno.set(errno.intValue());
    }
    
    public int chmod(String filename, int mode) {
        return Chmod.chmod(new JavaSecuredFile(filename), Integer.toOctalString(mode));
    }

    public int chown(String filename, int user, int group) {
        PosixExec launcher = new PosixExec(handler);
        int chownResult = -1;
        int chgrpResult = -1;
        
        try {
            if (user != -1) chownResult = launcher.runAndWait("chown", "" + user, filename);
            if (group != -1) chgrpResult = launcher.runAndWait("chgrp ", "" + user, filename);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
        
        return chownResult != -1 && chgrpResult != -1 ? 0 : 1;
    }

    public static CharSequence crypt(CharSequence original, CharSequence salt) {
        return JavaCrypt.crypt(original, salt);
    }

    // FIXME: This version has no idea what charset you want, so it just uses default.
    public static byte[] crypt(byte[] original, byte[] salt) {
        return JavaCrypt.crypt(new String(original), new String(salt)).toString().getBytes();
    }

    public int getfd(FileDescriptor descriptor) {
        return getfdFromDescriptor(descriptor);
    }

    public static int getfdFromDescriptor(FileDescriptor descriptor) {
        if (descriptor == null || FILE_DESCRIPTOR_FD == null) return -1;
        try {
            return FILE_DESCRIPTOR_FD.getInt(descriptor);
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        return -1;
    }

     public static HANDLE gethandle(FileDescriptor descriptor) {
         if (descriptor == null || FILE_DESCRIPTOR_HANDLE == null) return HANDLE.valueOf(-1);
         try {
             return gethandle(FILE_DESCRIPTOR_HANDLE.getLong(descriptor));
         } catch (SecurityException e) {
         } catch (IllegalArgumentException e) {
         } catch (IllegalAccessException e) {
         }

         return HANDLE.valueOf(-1);
     }

     public static HANDLE gethandle(long descriptor) {
         return HANDLE.valueOf(descriptor);
     }

    public String getlogin() {
        return System.getProperty("user.name");
    }

    public int getpid() {
        return handler.getPID();
    }
    ThreadLocal<Integer> pwIndex = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    public Passwd getpwent() {
        Passwd retVal = pwIndex.get().intValue() == 0 ? new JavaPasswd(handler) : null;
        pwIndex.set(pwIndex.get() + 1);
        return retVal;
    }

    public int setpwent() {
        return 0;
    }

    public int endpwent() {
        pwIndex.set(0);
        return 0;
    }
    public Passwd getpwuid(int which) {
        return which == JavaPOSIX.LoginInfo.UID ? new JavaPasswd(handler) : null;
    }
    public int isatty(int fd) {
        return (fd == STDOUT || fd == STDIN || fd == STDERR) ? 1 : 0;
    }

    public int link(String oldpath, String newpath) {
        try {
            return new PosixExec(handler).runAndWait("ln", oldpath, newpath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
        errno(EINVAL);
        return -1;  // We tried and failed for some reason. Indicate error.
    }
    
    public int lstat(String path, FileStat stat) {
        File file = new JavaSecuredFile(path);

        if (!file.exists()) {
            errno(ENOENT);
            return -1;
        }
        
        // FIXME: Bulletproof this or no?
        JavaFileStat jstat = (JavaFileStat) stat;
        
        jstat.setup(path);

        // TODO: Add error reporting for cases we can calculate: ENOTDIR, ENAMETOOLONG, ENOENT
        // EACCES, ELOOP, EFAULT, EIO

        return 0;
    }
    
    public int mkdir(String path, int mode) {
        File dir = new JavaSecuredFile(path);
        
        if (!dir.mkdir()) return -1;

        chmod(path, mode);
        
        return 0;
    }

    public int rmdir(String path) {
        return new JavaSecuredFile(path).delete() ? 0 : -1;
    }

    public static int chdir(String path) {
        System.setProperty("user.dir", path);
        return 0;
    }
    
    public int stat(String path, FileStat stat) {
        // FIXME: Bulletproof this or no?
        JavaFileStat jstat = (JavaFileStat) stat;
        
        try {
            File file = new JavaSecuredFile(path);
            
            if (!file.exists()) return -1;
                
            jstat.setup(file.getCanonicalPath());
        } catch (IOException e) {
            // TODO: Throw error when we have problems stat'ing canonicalizing
        }

        // TODO: Add error reporting for cases we can calculate: ENOTDIR, ENAMETOOLONG, ENOENT
        // EACCES, ELOOP, EFAULT, EIO

        return 0;
    }

    public int symlink(String oldpath, String newpath) {
        try {
            return new PosixExec(handler).runAndWait("ln", "-s", oldpath, newpath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
        errno(EEXIST);
        return -1;  // We tried and failed for some reason. Indicate error.

    }

    public int readlink(String oldpath, ByteBuffer buffer, int length) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new PosixExec(handler).runAndWait(baos, "readlink", oldpath);
            
            byte[] bytes = baos.toByteArray();
            
            if (bytes.length > length || bytes.length == 0) return -1;
            buffer.put(bytes, 0, bytes.length - 1); // trim off \n

            
            return buffer.position();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        errno(ENOENT);
        return -1; // We tried and failed for some reason. Indicate error.
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public static FileDescriptor toFileDescriptor(int fileDescriptor) {
        FileDescriptor descriptor = new FileDescriptor();
        try {
            FILE_DESCRIPTOR_FD.set(descriptor, fileDescriptor);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return descriptor;
    }

    public static FileDescriptor toFileDescriptor(HANDLE fileDescriptor) {
        FileDescriptor descriptor = new FileDescriptor();
        try {
            FILE_DESCRIPTOR_HANDLE.set(descriptor, fileDescriptor.toPointer().address());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return descriptor;
    }

    private static class PosixExec extends ExecIt {
        private final AtomicReference<Errno> errno = new AtomicReference<Errno>(Errno.EINVAL);
        private final ErrnoParsingOutputStream errorStream = new ErrnoParsingOutputStream(errno);

        public PosixExec(POSIXHandler handler) {
            super(handler);
        }

        private int parseResult(int result) {
            if (result == 0) {
                return result;
            }
            errno(errno.get());
            return -1;
        }

        public int runAndWait(String... args) throws IOException, InterruptedException {
            return runAndWait(handler.getOutputStream(), errorStream, args);
        }

        public int runAndWait(OutputStream output, String... args) throws IOException, InterruptedException {
            return runAndWait(output, errorStream, args);
        }

        public int runAndWait(OutputStream output, OutputStream error, String... args) throws IOException, InterruptedException {
            return parseResult(super.runAndWait(output, error, args));
        }
    }

    private static final class ErrnoParsingOutputStream extends OutputStream {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final AtomicReference<Errno> errno;

        private ErrnoParsingOutputStream(AtomicReference<Errno> errno) {
            this.errno = errno;
        }

        @Override
        public void write(int b) throws IOException {
            if (b != '\r' && b != '\n' && b != -1) {
                baos.write(b);
            } else if (baos.size() > 0) {
                String errorString = baos.toString();
                baos.reset();
                parseError(errorString);
            }
        }

        static Map<Pattern, Errno> errorPatterns = new HashMap<Pattern, Errno>();
        static {
            errorPatterns.put(Pattern.compile("File exists"), Errno.EEXIST);
            errorPatterns.put(Pattern.compile("Operation not permitted"), Errno.EPERM);
            errorPatterns.put(Pattern.compile("No such file or directory"), Errno.ENOENT);
            errorPatterns.put(Pattern.compile("Input/output error"), Errno.EIO);
            errorPatterns.put(Pattern.compile("Not a directory"), Errno.ENOTDIR);
            errorPatterns.put(Pattern.compile("No space left on device"), Errno.ENOSPC);
            errorPatterns.put(Pattern.compile("Read-only file system"), Errno.EROFS);
            errorPatterns.put(Pattern.compile("Too many links"), Errno.EMLINK);
        }

        void parseError(String errorString) {
            for (Map.Entry<Pattern, Errno> entry : errorPatterns.entrySet()) {
                if (entry.getKey().matcher(errorString).find()) {
                    errno.set(entry.getValue());
                }
            }
        }
    }
}
