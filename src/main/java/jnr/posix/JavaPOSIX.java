package jnr.posix;

import static jnr.constants.platform.Errno.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import jnr.constants.platform.Errno;
import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Sysconf;
import jnr.posix.util.Java5ProcessMaker;
import jnr.posix.util.Platform;
import jnr.posix.util.ProcessMaker;
import sun.misc.Signal;

final class JavaPOSIX implements POSIX {
    private final POSIXHandler handler;
    private final JavaLibCHelper helper;

    JavaPOSIX(POSIXHandler handler) {
        this.handler = handler;
        this.helper = new JavaLibCHelper(handler);
    }

    public ProcessMaker newProcessMaker(String... command) {
        return new Java5ProcessMaker(handler, command);
    }

    public ProcessMaker newProcessMaker() {
        return new Java5ProcessMaker(handler);
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

    public int exec(String path, String... argv) {
        handler.unimplementedError("No exec in Java (yet)");
        
        return -1;
    }

    public int exec(String path, String[] argv, String[] envp) {
        handler.unimplementedError("No exec in Java (yet)");
        
        return -1;
    }
    
    public int execv(String path, String[] argv) {
        handler.unimplementedError("No execv in Java (yet)");
        
        return -1;
    }
    
    public int execve(String path, String[] argv, String[] envp) {
        handler.unimplementedError("No execve in Java (yet)");
        
        return -1;
    }
    
    public FileStat fstat(FileDescriptor descriptor) {
        handler.unimplementedError("fstat unimplemented");
        
        return null;
    }

    public FileStat fstat(int descriptor) {
        handler.unimplementedError("fstat unimplemented");
        
        return null;
    }

    public int fstat(int fd, FileStat stat) {
        handler.unimplementedError("fstat unimplemented");
        return -1;
    }

    public int fstat(FileDescriptor descriptor, FileStat stat) {
        handler.unimplementedError("fstat unimplemented");
        return -1;
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

    public int getdtablesize() {
        handler.unimplementedError("getdtablesize unimplemented");
        return -1;
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
    
    // @see setenv for more on the environment methods
    public String getenv(String envName) {
        return helper.getEnv().get(envName);
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
    
    private static class SunMiscSignalHandler implements sun.misc.SignalHandler {
        final SignalHandler handler;
        public SunMiscSignalHandler(SignalHandler handler) {
            this.handler = handler;
        }
        
        public void handle(Signal signal) {
            handler.handle(signal.getNumber());
        }
    }
    
    public SignalHandler signal(jnr.constants.platform.Signal sig, final SignalHandler handler) {
        Signal s = new Signal(sig.name().substring("SIG".length()));
        
        sun.misc.SignalHandler oldHandler = Signal.handle(s, new SunMiscSignalHandler(handler));
        
        if (oldHandler instanceof SunMiscSignalHandler) {
            return ((SunMiscSignalHandler)oldHandler).handler;
        } else {
            return null;
        }
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

        if (lstat(path, stat) < 0) handler.error(ENOENT, "lstat", path);
        
        return stat;
    }

    public int lstat(String path, FileStat stat) {
        return helper.lstat(path, stat);
    }

    public int mkdir(String path, int mode) {
        return helper.mkdir(path, mode);
    }

    public int rmdir(String path) {
        return helper.rmdir(path);
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
    
    // At this point the environment is not being used by any methods here.
    // getenv/setenv/unsetenv do behave properly via POSIX definitions, but 
    // it is only a storage facility at the moment.  In a future release, this
    // map will be hooked up to the methods which depend on env.
    public int setenv(String envName, String envValue, int overwrite) {
        Map<String, String> env = helper.getEnv();
        
        if (envName.contains("=")) {
            handler.error(EINVAL, "setenv", envName);
            return -1;
        }
        
        // POSIX specified.  Existence is success if overwrite is 0.
        if (overwrite == 0 && env.containsKey(envName)) return 0;
        
        env.put(envName, envValue);
        
        return 0;
    }

    public FileStat stat(String path) {
        FileStat stat = allocateStat(); 

        if (helper.stat(path, stat) < 0) handler.error(ENOENT, "stat", path);
        
        return stat;
    }

    public int stat(String path, FileStat stat) {
        return helper.stat(path, stat);
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

    public int unsetenv(String envName) {
        if (helper.getEnv().remove(envName) == null) {
            handler.error(EINVAL, "unsetenv", envName);
            return -1;
        }
        
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

    public int waitpid(long pid, int[] status, int flags) {
        return unimplementedInt("waitpid");
    }

    public int getpriority(int which, int who) {
        return unimplementedInt("getpriority");
    }
    
    public int setpriority(int which, int who, int prio) {
        return unimplementedInt("setpriority");
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions, Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        return unimplementedInt("posix_spawnp");
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                             Collection<? extends SpawnAttribute> spawnAttributes,
                             Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        return unimplementedInt("posix_spawnp");
    }

    public int errno() {
        return JavaLibCHelper.errno();
    }

    public void errno(int value) {
        JavaLibCHelper.errno(value);
    }
    
    public int chdir(String path) {
        return JavaLibCHelper.chdir(path);
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

    public long sysconf(Sysconf name) {
        switch (name) {
            case _SC_CLK_TCK:
                return JavaTimes.HZ;

            default:
                errno(Errno.EOPNOTSUPP.intValue());
                return -1;
        }
    }

    public Times times() {
        return new JavaTimes();
    }
    
    public int flock(int fd, int mode) {
        return unimplementedInt("waitpid");
    }

    public int dup(int fd) {
        return unimplementedInt("dup");
    }

    public int dup2(int oldFd, int newFd) {
        return unimplementedInt("dup2");
    }

    public int fcntl(int fd, Fcntl fcntlConst, int... arg) {
        return unimplementedInt("fcntl");
    }

    public int close(int fd) {
        return unimplementedInt("close");
    }

    public int unlink(CharSequence path) {
        handler.unimplementedError("unlink");

        return -1;
    }

    public int open(CharSequence path, int flags, int perm) {
        handler.unimplementedError("open");

        return -1;
    }

    public int read(int fd, byte[] buf, int n) {
        handler.unimplementedError("read");

        return -1;
    }

    public int write(int fd, byte[] buf, int n) {
        handler.unimplementedError("write");

        return -1;
    }

    public int read(int fd, ByteBuffer buf, int n) {
        handler.unimplementedError("read");

        return -1;
    }

    public int write(int fd, ByteBuffer buf, int n) {
        handler.unimplementedError("write");

        return -1;
    }

    public int pread(int fd, byte[] buf, int n, int offset) {
        handler.unimplementedError("pread");

        return -1;
    }

    public int pwrite(int fd, byte[] buf, int n, int offset) {
        handler.unimplementedError("pwrite");

        return -1;
    }

    public int pread(int fd, ByteBuffer buf, int n, int offset) {
        handler.unimplementedError("pread");

        return -1;
    }

    public int pwrite(int fd, ByteBuffer buf, int n, int offset) {
        handler.unimplementedError("pwrite");

        return -1;
    }

    public int lseek(int fd, int offset, int whence) {
        handler.unimplementedError("lseek");

        return -1;
    }

    public int pipe(int[] fds) {
        handler.unimplementedError("pipe");

        return -1;
    }

    public String gethostname() {
        return helper.gethostname();
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
