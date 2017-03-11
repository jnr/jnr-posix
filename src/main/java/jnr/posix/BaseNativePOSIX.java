package jnr.posix;

import jnr.constants.Constant;
import jnr.constants.platform.Errno;
import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Sysconf;
import jnr.ffi.*;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jnr.constants.platform.Signal;

public abstract class BaseNativePOSIX extends NativePOSIX implements POSIX {
    private final LibC libc;
    
    protected final POSIXHandler handler;
    protected final JavaLibCHelper helper;
    
    protected final Map<Signal, SignalHandler> signalHandlers = new HashMap();
    
    protected BaseNativePOSIX(LibCProvider libcProvider, POSIXHandler handler) {
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

    public int fchmod(int fd, int mode) {
        return libc().fchmod(fd, mode);
    }

    public int chown(String filename, int user, int group) {
        return libc().chown(filename, user, group);
    }

    public int fchown(int fd, int user, int group) {
        return libc().fchown(fd, user, group);
    }

    public CharSequence crypt(CharSequence key, CharSequence salt) {
        return libc().crypt(key, salt);
    }

    public byte[] crypt(byte[] key, byte[] salt) {
        Pointer ptr = libc().crypt(key, salt);
        if (ptr == null) return null;
        int end = ptr.indexOf(0, (byte)0);
        byte[] bytes = new byte[end + 1];
        ptr.get(0, bytes, 0, end);

        return bytes;
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

    public Pointer environ() {
        return getRuntime().getMemoryManager().newPointer(libc().environ().get());
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

    public int getrlimit(int resource, RLimit rlim) {
        return libc().getrlimit(resource, rlim);
    }

    public int getrlimit(int resource, Pointer rlim) {
        return libc().getrlimit(resource, rlim);
    }

    public RLimit getrlimit(int resource) {
        RLimit rlim = new DefaultNativeRLimit(getRuntime());

        if (getrlimit(resource, rlim) < 0) handler.error(Errno.valueOf(errno()), "rlim");

        return rlim;
    }

    public int setrlimit(int resource, RLimit rlim) {
        return libc().setrlimit(resource, rlim);
    }

    public int setrlimit(int resource, Pointer rlim) {
        return libc().setrlimit(resource, rlim);
    }

    public int setrlimit(int resource, long rlimCur, long rlimMax) {
        RLimit rlim = new DefaultNativeRLimit(getRuntime());
        rlim.init(rlimCur, rlimMax);

        return libc().setrlimit(resource, rlim);
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
        return kill((long) pid, signal);
    }

    public int kill(long pid, int signal) {
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

    public int readlink(CharSequence path, byte[] buf, int bufsize) {
        return libc().readlink(path, buf, bufsize);
    }

    public int readlink(CharSequence path, ByteBuffer buf, int bufsize) {
        return libc().readlink(path, buf, bufsize);
    }

    public int readlink(CharSequence path, Pointer bufPtr, int bufsize) {
        return libc().readlink(path, bufPtr, bufsize);
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

    public int utimes(String path, Pointer times) {
        return libc().utimes(path, times);
    }

    public int futimes(int fd, long[] atimeval, long[] mtimeval) {
        Timeval[] times = null;
        if (atimeval != null && mtimeval != null) {
            times = Struct.arrayOf(getRuntime(), DefaultNativeTimeval.class, 2);
            times[0].setTime(atimeval);
            times[1].setTime(mtimeval);
        }
        return libc().futimes(fd, times);
    }

    public int lutimes(String path, long[] atimeval, long[] mtimeval) {
        Timeval[] times = null;
        if (atimeval != null && mtimeval != null) {
            times = Struct.arrayOf(getRuntime(), DefaultNativeTimeval.class, 2);
            times[0].setTime(atimeval);
            times[1].setTime(mtimeval);
        }
        return libc().lutimes(path, times);
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
       return isatty(helper.getfd(fd)) != 0;
    }

    public int isatty(int fd) {
        return libc().isatty(fd);
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
        long result;

        try {
            result = ((UnixLibC) libc()).posix_spawnp(pid, path, nativeFileActions, nativeSpawnAttributes, argv, envp);
        } finally {
            if (nativeFileActions != null) ((UnixLibC) libc()).posix_spawn_file_actions_destroy(nativeFileActions);
            if (nativeSpawnAttributes != null) ((UnixLibC) libc()).posix_spawnattr_destroy(nativeSpawnAttributes);
        }

        if (result != 0) return -1; // result will be errno, but we can't indicate error because we return pid
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

    public int fcntlInt(int fd, Fcntl fcntl, int arg) {
        return libc().fcntl(fd, fcntl.intValue(), arg);
    }

    public int fcntl(int fd, Fcntl fcntl) {
        return libc().fcntl(fd, fcntl.intValue());
    }

    public int fcntl(int fd, Fcntl fcntl, int... arg) {
        return libc().fcntl(fd, fcntl.intValue());
    }

    public int access(CharSequence path, int amode) {
        return libc().access(path, amode);
    }

    public int close(int fd) {
        return libc().close(fd);
    }

    private Pointer nativeFileActions(Collection<? extends SpawnFileAction> fileActions) {
        Pointer nativeFileActions = allocatePosixSpawnFileActions();
        ((UnixLibC) libc()).posix_spawn_file_actions_init(nativeFileActions);
        for (SpawnFileAction action : fileActions) {
            action.act(this, nativeFileActions);
        }

        return nativeFileActions;
    }

    private Pointer nativeSpawnAttributes(Collection<? extends SpawnAttribute> spawnAttributes) {
        Pointer nativeSpawnAttributes = allocatePosixSpawnattr();
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

    public long read(int fd, byte[] buf, long n) {
        return libc().read(fd, buf, n);
    }
    public long write(int fd, byte[] buf, long n) {
        return libc().write(fd, buf, n);
    }
    public long read(int fd, ByteBuffer buf, long n) {
        return libc().read(fd, buf, n);
    }
    public long write(int fd, ByteBuffer buf, long n) {
        return libc().write(fd, buf, n);
    }
    public long pread(int fd, byte[] buf, long n, long offset) {
        return libc().pread(fd, buf, n, offset);
    }
    public long pwrite(int fd, byte[] buf, long n, long offset) {
        return libc().pwrite(fd, buf, n, offset);
    }
    public long pread(int fd, ByteBuffer buf, long n, long offset) {
        return libc().pread(fd, buf, n, offset);
    }
    public long pwrite(int fd, ByteBuffer buf, long n, long offset) {
        return libc().pwrite(fd, buf, n, offset);
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

    public int lseek(int fd, long offset, int whence) {
        return (int) libc().lseek(fd, offset, whence);
    }

    public long lseekLong(int fd, long offset, int whence) {
        return libc().lseek(fd, offset, whence);
    }

    public int pipe(int[] fds) {
        return libc().pipe(fds);
    }

    public int socketpair(int domain, int type, int protocol, int[] fds) {
        return libc().socketpair(domain, type, protocol, fds);
    }

    public int sendmsg(int socket, MsgHdr message, int flags) {
        return libc().sendmsg( socket, message, flags );
    }

    public int recvmsg(int socket, MsgHdr message, int flags) {
        return libc().recvmsg(socket, message, flags);
    }

    public int truncate(CharSequence path, long length) {
        return libc().truncate(path, length);
    }

    public int ftruncate(int fd, long offset) {
        return libc().ftruncate(fd, offset);
    }

    public int rename(CharSequence oldName, CharSequence newName) {
        return libc().rename(oldName, newName);
    }

    public String getcwd() {
        byte[] cwd = new byte[1024];
        long result = libc().getcwd(cwd, 1024);
        if (result == -1) return null;
        int len = 0;
        for (; len < 1024; len++) if (cwd[len] == 0) break;
        return new String(cwd, 0, len);
    }

    public int fsync(int fd) {
        return libc().fsync(fd);
    }

    public int fdatasync(int fd) {
        return libc().fdatasync(fd);
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

    public static final ToNativeConverter<MsgHdr, Pointer> MsgHdrConverter = new ToNativeConverter<MsgHdr, Pointer>() {
        public Pointer toNative(MsgHdr value, ToNativeContext context) {
            if ( value instanceof BaseMsgHdr ) {
                return ((BaseMsgHdr) value).memory;
            } else if ( value instanceof Struct ) {
                return Struct.getMemory((Struct) value);
            } else if ( value == null ) {
                return null;
            }

            throw new IllegalArgumentException("instance of " + value.getClass() + " is not a struct");
        }

        public Class<Pointer> nativeType() {
            return Pointer.class;
        }
    };

    public int mkfifo(String filename, int mode) {
        return ((UnixLibC) libc()).mkfifo(filename, mode);
    }

    public int daemon(int nochdir, int noclose) {
        return libc().daemon(nochdir, noclose);
    }

    @Override
    public long[] getgroups() {
        final int size = getgroups(0, null);
        final int[] groups = new int[size];
        final long[] castGroups = new long[size];

        final int actualSize = getgroups(size, groups);

        if (actualSize == -1) {
            return null;
        }

        for (int i = 0; i < actualSize; i++) {
            castGroups[i] = groups[i] & 0xFFFFFFFFL;
        }

        if (actualSize < size) {
            return Arrays.copyOfRange(castGroups, 0, actualSize);
        }

        return castGroups;
    }

    @Override
    public int getgroups(int size, int[] groups) {
        return libc().getgroups(size, groups);
    }

    @Override
    public String nl_langinfo(int item) {
        return libc().nl_langinfo(item);
    }

    @Override
    public String setlocale(int category, String locale) {
        return libc().setlocale(category, locale);
    }

    @Override
    public String strerror(int code) {
        return libc().strerror(code);
    }

    @Override
    public Timeval allocateTimeval() { return new DefaultNativeTimeval(getRuntime()); }

    @Override
    public int gettimeofday(Timeval tv) { return libc().gettimeofday(tv, 0); }
}
