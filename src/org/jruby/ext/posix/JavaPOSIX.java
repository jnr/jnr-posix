package org.jruby.ext.posix;

import static com.kenai.constantine.platform.Errno.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.jruby.ext.posix.util.Platform;

final class JavaPOSIX implements POSIX {
    private final POSIXHandler handler;
    private final JavaLibCHelper helper;

    JavaPOSIX(POSIXHandler handler) {
        this.handler = handler;
        this.helper = new JavaLibCHelper(handler);
    }
    
    public FileStat allocateStat() {
        return new JavaFileStat(this, handler);
    }

    public int chmod(String filename, int mode) {
        return helper.chmod(filename, mode);
    }

    public int chown(String filename, int user, int group) {
        return helper.chown(filename, user, group);
    }
    
    public FileStat fstat(FileDescriptor descriptor) {
        handler.unimplementedError("fstat unimplemented");
        
        return null;
    }

    public int getegid() {
        return LoginInfo.GID;
    }
    
    public int geteuid() {
        return LoginInfo.UID;
    }
    
    public int getgid() {
        return LoginInfo.GID;
    }
    
    public String getlogin() {
        return helper.getlogin();
    }

    public int getpgid() {
        return unimplementedInt("getpgid");
    }

    public int getpgrp() {
        return unimplementedInt("getpgrp");
    }

    public int getpid() {
        return helper.getpid();
    }

    public int getppid() {
        return unimplementedInt("getppid");
    }

    public Passwd getpwent() {
        return helper.getpwent();
    }

    public Passwd getpwuid(int which) {
        return helper.getpwuid(which);
    }

    public Group getgrgid(int which) {
        handler.unimplementedError("getgrgid unimplemented");
        return null;
    }

    public Passwd getpwnam(String which) {
        handler.unimplementedError("getpwnam unimplemented");
        return null;
    }
    public Group getgrnam(String which) {
        handler.unimplementedError("getgrnam unimplemented");
        return null;
    }

    public Group getgrent() {
        handler.unimplementedError("getgrent unimplemented");
        return null;
    }

    public int setpwent() {
        return helper.setpwent();
    }

    public int endpwent() {
        return helper.endpwent();
    }

    public int setgrent() {
        return unimplementedInt("setgrent");
    }

    public int endgrent() {
        return unimplementedInt("endgrent");
    }

    public int getuid() {
        return LoginInfo.UID;
    }
    
    public int fork() {
        return -1;
    }

    public boolean isatty(FileDescriptor fd) {
        return (fd == FileDescriptor.in
                || fd == FileDescriptor.out
                || fd == FileDescriptor.err);
    }

    public int kill(int pid, int signal) {
        return unimplementedInt("kill");    // FIXME: Can be implemented
    }

    public int lchmod(String filename, int mode) {
        return unimplementedInt("lchmod");    // FIXME: Can be implemented
    }

    public int lchown(String filename, int user, int group) {
        return unimplementedInt("lchown");     // FIXME: Can be implemented
    }

    public int link(String oldpath, String newpath) {
        return helper.link(oldpath, newpath);
    }

    public FileStat lstat(String path) {
        FileStat stat = allocateStat();

        if (helper.lstat(path, stat) < 0) handler.error(ENOENT, path);
        
        return stat;
    }

    public int mkdir(String path, int mode) {
        return helper.mkdir(path, mode);
    }

    public String readlink(String path) throws IOException {
        // TODO: this should not be hardcoded to 256 bytes
        ByteBuffer buffer = ByteBuffer.allocateDirect(256);
        int result = helper.readlink(path, buffer, buffer.capacity());
        
        if (result == -1) return null;
        
        buffer.position(0);
        buffer.limit(result);
        return Charset.forName("ASCII").decode(buffer).toString();
    }

    public FileStat stat(String path) {
        FileStat stat = allocateStat(); 

        if (helper.stat(path, stat) < 0) handler.error(ENOENT, path);
        
        return stat;
    }

    public int symlink(String oldpath, String newpath) {
        return helper.symlink(oldpath, newpath);
    }

    public int setegid(int egid) {
        return unimplementedInt("setegid");
    }

    public int seteuid(int euid) {
        return unimplementedInt("seteuid");
    }

    public int setgid(int gid) {
        return unimplementedInt("setgid");
    }

    public int getpgid(int pid) {
        return unimplementedInt("getpgid");
    }

    public int setpgid(int pid, int pgid) {
        return unimplementedInt("setpgid");
    }

    public int setpgrp(int pid, int pgrp) {
        return unimplementedInt("setpgrp");
    }

    public int setsid() {
        return unimplementedInt("setsid");
    }

    public int setuid(int uid) {
        return unimplementedInt("setuid");
    }

    public int umask(int mask) {
        // TODO: We can possibly maintain an internal mask and try and apply it to individual
        // libc methods.  
        return 0;
    }

    public int utimes(String path, long[] atimeval, long[] mtimeval) {
        long mtimeMillis;
        if (mtimeval != null) {
            assert mtimeval.length == 2;
            mtimeMillis = (mtimeval[0] * 1000) + (mtimeval[1] / 1000);
        } else {
            mtimeMillis = System.currentTimeMillis();
        }
        new File(path).setLastModified(mtimeMillis);
        return 0;
    }
    
    public int wait(int[] status) {
        return unimplementedInt("wait");
    }
    
    public int waitpid(int pid, int[] status, int flags) {
        return unimplementedInt("waitpid");
    }

    public int getpriority(int which, int who) {
        return unimplementedInt("getpriority");
    }
    
    public int setpriority(int which, int who, int prio) {
        return unimplementedInt("setpriority");
    }

    public int errno() {
        return 0;
    }

    public void errno(int value) {
        // do nothing, errno is unsupported
    }

    public int execv(String path, String... argv) {
        handler.unimplementedError("No execv in Java (yet)");
        
        return -1;
    }

    public boolean isNative() {
        return false;
    }

    public LibC libc() {
        return null;
    }

    private int unimplementedInt(String message) {
        handler.unimplementedError(message);
        
        return -1;
    }
    public int aspawn(boolean overlay, String program, String[] argv, String path) {
        handler.unimplementedError("No spawn in Java");
        
        return -1;        
    }
    
    public int spawn(boolean ovelay, String command, String program, String path) {
        handler.unimplementedError("No spawn in Java");
        
        return -1;
    }
    
    static final class LoginInfo {
        public static final int UID = IDHelper.getInt("-u");
        public static final int GID = IDHelper.getInt("-g");
        public static final String USERNAME = IDHelper.getString("-un");
    }
    private static final class IDHelper {
        private static final String ID_CMD = Platform.IS_SOLARIS ? "/usr/xpg4/bin/id" : "/usr/bin/id";
        private static final int NOBODY = Platform.IS_WINDOWS ? 0 : Short.MAX_VALUE;
        public static int getInt(String option) {
            try {
                Process p = Runtime.getRuntime().exec(new String[] { ID_CMD, option });
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                return Integer.parseInt(r.readLine());
            } catch (IOException ex) {
                return NOBODY;
            } catch (NumberFormatException ex) {
                return NOBODY;
            } catch (SecurityException ex) {
                return NOBODY;
            }
        }
        public static String getString(String option) {
            try {
                Process p = Runtime.getRuntime().exec(new String[] { ID_CMD, option });
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                return r.readLine();
            } catch (IOException ex) {
                return null;
            }
        }
    }
    private static final class FakePasswd implements Passwd {

        public String getLoginName() {
            return LoginInfo.USERNAME;
        }

        public String getPassword() {
            return "";
        }

        public long getUID() {
            return LoginInfo.UID;
        }

        public long getGID() {
            return LoginInfo.GID;
        }

        public int getPasswdChangeTime() {
            return 0;
        }

        public String getAccessClass() {
            return "";
        }

        public String getGECOS() {
            return getLoginName();
        }

        public String getHome() {
            return "/";
        }

        public String getShell() {
            return "/bin/sh";
        }

        public int getExpire() {
            return ~0;
        }

    }
}
