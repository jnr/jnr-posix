package jnr.posix;

import jnr.constants.Constant;
import jnr.constants.platform.Errno;
import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Sysconf;
import jnr.ffi.*;
import jnr.ffi.byref.AbstractNumberReference;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.LongLongByReference;
import jnr.ffi.byref.NumberByReference;
import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.mapper.FromNativeConverter;
import jnr.ffi.mapper.ToNativeContext;
import jnr.ffi.mapper.ToNativeConverter;
import jnr.posix.util.Java5ProcessMaker;
import jnr.posix.util.MethodName;
import jnr.posix.util.ProcessMaker;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jnr.constants.platform.Signal;

abstract class BaseNativePOSIX extends NativePOSIX implements POSIX {
    private final LibC libc;
    
    protected final POSIXHandler handler;
    protected final JavaLibCHelper helper;
    
    protected final Map<Signal, SignalHandler> signalHandlers = new HashMap();
    
    BaseNativePOSIX(LibCProvider libcProvider, POSIXHandler handler) {
        this.handler = handler;
        this.libc = libcProvider.getLibC();
        this.helper = new JavaLibCHelper(handler);
    }

    public ProcessMaker newProcessMaker(String... command) {
        return new Java5ProcessMaker(handler, command);
    }

    public ProcessMaker newProcessMaker() {
        return new Java5ProcessMaker(handler);
    }

    public final LibC libc() {
        return libc;
    }

    POSIXHandler handler() {
        return handler;
    }

    protected <T> T unimplementedNull() {
        handler().unimplementedError(MethodName.getCallerMethodName());
        return null;
    }

    protected int unimplementedInt() {
        handler().unimplementedError(MethodName.getCallerMethodName());
        return -1;
    }

    public int chmod(String filename, int mode) {
        return libc().chmod(filename, mode);
    }

    public int chown(String filename, int user, int group) {
        return libc().chown(filename, user, group);
    }
    
    public int exec(String path, String... args) {
        handler.unimplementedError("exec unimplemented");
        return -1;
    }
    
    public int exec(String path, String[] args, String[] envp) {
        handler.unimplementedError("exec unimplemented");
        return -1;
    }
    
    public int execv(String path, String[] args) {
        return libc().execv(path, args);
    }
    
    public int execve(String path, String[] args, String[] env) {
        return libc().execve(path, args, env);
    }

    public FileStat fstat(FileDescriptor fileDescriptor) {
        FileStat stat = allocateStat();

        if (fstat(fileDescriptor, stat) < 0) handler.error(Errno.valueOf(errno()), "fstat", "" + helper.getfd(fileDescriptor));
        
        return stat;
    }

    public FileStat fstat(int fd) {
        FileStat stat = allocateStat();
        if (fstat(fd, stat) < 0) handler.error(Errno.valueOf(errno()), "fstat", "" + fd);  
        return stat;
    }

    
    public int fstat(FileDescriptor fileDescriptor, FileStat stat) {
        int fd = helper.getfd(fileDescriptor);
        return libc().fstat(fd, stat);
    }

    public int fstat(int fd, FileStat stat) {
        return libc().fstat(fd, stat);
    }
    
    public String getenv(String envName) {
        return libc().getenv(envName);
    }

    public int getegid() {
        return libc().getegid();
    }

    public int geteuid() {
        return libc().geteuid();
    }

    public int getgid() {
        return libc().getgid();
    }

    public int getdtablesize() {
        return libc().getdtablesize();
    }

    public String getlogin() {
        return libc().getlogin();
    }

    public int getpgid() {
        return libc().getpgid();
    }

    public int getpgrp() {
        return libc().getpgrp();
    }

    public int getpid() {
        return libc().getpid();
    }

    public int getppid() {
        return libc().getppid();
    }
    
    public Passwd getpwent() {
        return libc().getpwent();
    }

    public Passwd getpwuid(int which) {
        return libc().getpwuid(which);
    }

    public Passwd getpwnam(String which) {
        return libc().getpwnam(which);
    }

    public Group getgrent() {
        return libc().getgrent();
    }
    public Group getgrgid(int which) {
        return libc().getgrgid(which);
    }
    public Group getgrnam(String which) {
        return libc().getgrnam(which);
    }

    public int setpwent() {
        return libc().setpwent();
    }

    public int endpwent() {
        return libc().endpwent();
    }

    public int setgrent() {
        return libc().setgrent();
    }

    public int endgrent() {
        return libc().endgrent();
    }

    public int getuid() {
        return libc().getuid();
    }

    public int setegid(int egid) {
        return libc().setegid(egid);
    }

    public int seteuid(int euid) {
        return libc().seteuid(euid);
    }

    public int setgid(int gid) {
        return libc().setgid(gid);
    }
    
    public int getfd(FileDescriptor descriptor) {
        return helper.getfd(descriptor);
    }

    public int getpgid(int pid) {
        return libc().getpgid(pid);
    }

    public int setpgid(int pid, int pgid) {
        return libc().setpgid(pid, pgid);
    }

    public int setpgrp(int pid, int pgrp) {
        return libc().setpgrp(pid, pgrp);
    }

    public int setsid() {
        return libc().setsid();
    }

    public int setuid(int uid) {
        return libc().setuid(uid);
    }

    public int kill(int pid, int signal) {
        return libc().kill(pid, signal);
    }
    
    public SignalHandler signal(Signal sig, final SignalHandler handler) {
        synchronized (signalHandlers) {
            SignalHandler old = signalHandlers.get(sig);

            long result = libc().signal(sig.intValue(), new LibC.LibCSignalHandler() {
                public void signal(int sig) {
                    handler.handle(sig);
                }
            });

            if (result != -1) {
                signalHandlers.put(sig, handler);
            }

            return old;
        }
    }

    public int lchmod(String filename, int mode) {
        try {
            return libc().lchmod(filename, mode);
        } catch (UnsatisfiedLinkError ule) {
            return unimplementedInt();
        }
    }

    public int lchown(String filename, int user, int group) {
        try {
            return libc().lchown(filename, user, group);
        } catch (UnsatisfiedLinkError ule) {
            return unimplementedInt();
        }
    }

    public int link(String oldpath, String newpath) {
        return libc().link(oldpath, newpath);
    }
    
    public FileStat lstat(String path) {
        FileStat stat = allocateStat();
        
        if (lstat(path, stat) < 0) handler.error(Errno.valueOf(errno()), "lstat", path);
        
        return stat;
    }
    
    public int lstat(String path, FileStat stat) {
        return libc().lstat(path, stat);
    }

    public int mkdir(String path, int mode) {
        int res = libc().mkdir(path, mode);
        if (res < 0) {
            int errno = errno();
            handler.error(Errno.valueOf(errno), "mkdir", path);
        }
        return res;
    }

    public int rmdir(String path) {
        int res = libc().rmdir(path);

        if (res < 0) handler.error(Errno.valueOf(errno()), "rmdir", path);

        return res;
    }
    
    public int setenv(String envName, String envValue, int overwrite) {
        return libc().setenv(envName, envValue, overwrite);
    }

    public FileStat stat(String path) {
        FileStat stat = allocateStat();

        if (stat(path, stat) < 0) handler.error(Errno.valueOf(errno()), "stat", path);
        
        return stat;
    }

    public int stat(String path, FileStat stat) {
        return libc().stat(path, stat);
    }

    public int symlink(String oldpath, String newpath) {
        return libc().symlink(oldpath, newpath);
    }
    
    public String readlink(String oldpath) throws IOException {
        // TODO: this should not be hardcoded to 256 bytes
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int result = libc().readlink(oldpath, buffer, buffer.capacity());
        
        if (result == -1) return null;
        
        buffer.position(0);
        buffer.limit(result);
        return Charset.forName("ASCII").decode(buffer).toString();
    }
    
    public int unsetenv(String envName) {
        return libc().unsetenv(envName);
    }
    
    public int umask(int mask) {
        return libc().umask(mask);
    }
    
    public int utimes(String path, long[] atimeval, long[] mtimeval) {
        Timeval[] times = null;
        if (atimeval != null && mtimeval != null) {
            times = Struct.arrayOf(getRuntime(), DefaultNativeTimeval.class, 2);
            times[0].setTime(atimeval);
            times[1].setTime(mtimeval);
        }
        return libc().utimes(path, times);
    }

    public int fork() {
        return libc().fork();
    }
    
    public int waitpid(int pid, int[] status, int flags) {
        return waitpid((long)pid, status, flags);
    }

    public int waitpid(long pid, int[] status, int flags) {
        return libc().waitpid(pid, status, flags);
    }
    
    public int wait(int[] status) {
        return libc().wait(status);
    }
    
    public int getpriority(int which, int who) {
        return libc().getpriority(which, who);
    }
    
    public int setpriority(int which, int who, int prio) {
        return libc().setpriority(which, who, prio);
    }

    public boolean isatty(FileDescriptor fd) {
       return libc().isatty(helper.getfd(fd)) != 0;
    }

    public int errno() {
        return LastError.getLastError(getRuntime());
    }

    public void errno(int value) {
        LastError.setLastError(getRuntime(), value);
    }
    
    public int chdir(String path) {
        return libc().chdir(path);
    }

    public boolean isNative() {
        return true;
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                             CharSequence[] argv, CharSequence[] envp) {
        return posix_spawnp(path, fileActions, null, argv, envp);
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                            Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        return posix_spawnp(path, fileActions, null, argv, envp);
    }
    
    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                             Collection<? extends SpawnAttribute> spawnAttributes,
                             Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        
        CharSequence[] nativeArgv = new CharSequence[argv.size()];
        argv.toArray(nativeArgv);

        CharSequence[] nativeEnv = new CharSequence[envp.size()];
        envp.toArray(nativeEnv);

        return posix_spawnp(path, fileActions, spawnAttributes, nativeArgv, nativeEnv);
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                             Collection<? extends SpawnAttribute> spawnAttributes,
                             CharSequence[] argv, CharSequence[] envp) {
//        AbstractNumberReference<? extends Number> pid = getRuntime().findType(TypeAlias.pid_t).size() == 4
//            ? new IntByReference(-1) : new LongLongByReference(-1);
        NumberByReference pid = new NumberByReference(TypeAlias.pid_t);
        Pointer nativeFileActions = fileActions != null && !fileActions.isEmpty() ? nativeFileActions(fileActions) : null;
        Pointer nativeSpawnAttributes = spawnAttributes != null && !spawnAttributes.isEmpty() ? nativeSpawnAttributes(spawnAttributes) : null;

        try {
            if (((UnixLibC) libc()).posix_spawnp(pid, path, nativeFileActions, nativeSpawnAttributes, argv, envp) == -1) {
                Errno e = Errno.valueOf(errno());
                handler.error(e, "posix_spawnp", e.description());
            }
        } finally {
            if (nativeFileActions != null) ((UnixLibC) libc()).posix_spawn_file_actions_destroy(nativeFileActions);
            if (nativeSpawnAttributes != null) ((UnixLibC) libc()).posix_spawnattr_destroy(nativeSpawnAttributes);
        }

        return pid.longValue();
    }
    
    public int flock(int fd, int mode) {
        return libc().flock(fd, mode);
    }

    public int dup(int fd) {
        return libc().dup(fd);
    }

    public int dup2(int oldFd, int newFd) {
        return libc().dup2(oldFd, newFd);
    }

    public int fcntl(int fd, Fcntl fcntl, int... arg) {
        return libc().fcntl(fd, fcntl.intValue());
    }

    public int close(int fd) {
        return libc().close(fd);
    }

    private Pointer nativeFileActions(Collection<? extends SpawnFileAction> fileActions) {
        Pointer nativeFileActions = Memory.allocateDirect(getRuntime(), 128);
        ((UnixLibC) libc()).posix_spawn_file_actions_init(nativeFileActions);
        for (SpawnFileAction action : fileActions) {
            action.act(this, nativeFileActions);
        }

        return nativeFileActions;
    }

    private Pointer nativeSpawnAttributes(Collection<? extends SpawnAttribute> spawnAttributes) {
        Pointer nativeSpawnAttributes = Memory.allocateDirect(getRuntime(), 128);
        ((UnixLibC) libc()).posix_spawnattr_init(nativeSpawnAttributes);
        for (SpawnAttribute action : spawnAttributes) {
            action.set(this, nativeSpawnAttributes);
        }

        return nativeSpawnAttributes;
    }

    public abstract FileStat allocateStat();

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

    public int unlink(CharSequence path) {
        return libc().unlink(path);
    }

    public int open(CharSequence path, int flags, int perm) {
        return libc().open(path, flags, perm);
    }

    public int read(int fd, byte[] buf, int n) {
        return libc().read(fd, buf, n);
    }

    public int write(int fd, byte[] buf, int n) {
        return libc().write(fd, buf, n);
    }

    public int read(int fd, ByteBuffer buf, int n) {
        return libc().read(fd, buf, n);
    }

    public int write(int fd, ByteBuffer buf, int n) {
        return libc().write(fd, buf, n);
    }

    public int pread(int fd, byte[] buf, int n, int offset) {
        return libc().pread(fd, buf, n, offset);
    }

    public int pwrite(int fd, byte[] buf, int n, int offset) {
        return libc().pwrite(fd, buf, n, offset);
    }

    public int pread(int fd, ByteBuffer buf, int n, int offset) {
        return libc().pread(fd, buf, n, offset);
    }

    public int pwrite(int fd, ByteBuffer buf, int n, int offset) {
        return libc().pwrite(fd, buf, n, offset);
    }

    public int lseek(int fd, int offset, int whence) {
        return libc().lseek(fd, offset, whence);
    }

    public int pipe(int[] fds) {
        return libc().pipe(fds);
    }

    public String gethostname() {
        return libc().gethostname();
    }

    public static abstract class PointerConverter implements FromNativeConverter {
        public Class nativeType() {
            return Pointer.class;
        }
    }
    
    public static final PointerConverter GROUP = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new DefaultNativeGroup((Pointer) arg) : null;
        }
    };

    public static final ToNativeConverter<FileStat, Pointer> FileStatConverter = new ToNativeConverter<FileStat, Pointer>() {

        public Pointer toNative(FileStat value, ToNativeContext context) {
            if (value instanceof BaseFileStat) {
                return ((BaseFileStat) value).memory;

            } else if (value instanceof Struct) {
                return Struct.getMemory((Struct) value);

            } else if (value == null) {
                return null;
            }

            throw new IllegalArgumentException("instance of " + value.getClass() + " is not a struct");
        }

        public Class<Pointer> nativeType() {
            return Pointer.class;
        }

    };

    public static final ToNativeConverter<NativeTimes, Pointer> TimesConverter = new ToNativeConverter<NativeTimes, Pointer>() {

        public Pointer toNative(NativeTimes value, ToNativeContext context) {
            return value.memory;
        }

        public Class<Pointer> nativeType() {
            return Pointer.class;
        }
    };

    public static final ToNativeConverter<Constant, Integer> ConstantConverter = new ToNativeConverter<Constant, Integer>() {

        public Integer toNative(Constant value, ToNativeContext context) {
            return value.intValue();
        }

        public Class<Integer> nativeType() {
            return Integer.class;
        }
    };
}
