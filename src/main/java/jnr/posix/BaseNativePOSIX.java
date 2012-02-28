package jnr.posix;

import static jnr.constants.platform.Errno.*;

import jnr.constants.platform.Errno;
import jnr.ffi.LastError;
import jnr.ffi.Memory;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.mapper.FromNativeConverter;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.ToNativeContext;
import jnr.ffi.mapper.ToNativeConverter;
import jnr.ffi.Struct;
import jnr.posix.util.Java5ProcessMaker;
import jnr.posix.util.ProcessMaker;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

abstract class BaseNativePOSIX extends NativePOSIX implements POSIX {
    private final LibC libc;

    protected final String libraryName;
    protected final POSIXHandler handler;
    protected final JavaLibCHelper helper;

    BaseNativePOSIX(String libraryName, LibCProvider libcProvider, POSIXHandler handler) {
        this.handler = handler;
        this.libraryName = libraryName;
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

        if (fstat(fileDescriptor, stat) < 0) handler.error(Errno.valueOf(errno()), "" + helper.getfd(fileDescriptor));

        return stat;
    }

    public FileStat fstat(int fd) {
        FileStat stat = allocateStat();
        if (fstat(fd, stat) < 0) handler.error(Errno.valueOf(errno()), "" + fd);
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

    public int lchmod(String filename, int mode) {
        return libc().lchmod(filename, mode);
    }

    public int lchown(String filename, int user, int group) {
        return libc().lchown(filename, user, group);
    }

    public int link(String oldpath, String newpath) {
        return libc().link(oldpath, newpath);
    }

    public FileStat lstat(String path) {
        FileStat stat = allocateStat();

        if (lstat(path, stat) < 0) handler.error(Errno.valueOf(errno()), path);

        return stat;
    }

    public int lstat(String path, FileStat stat) {
        return libc().lstat(path, stat);
    }

    public int mkdir(String path, int mode) {
        int res = libc().mkdir(path, mode);
        if (res < 0) {
            int errno = errno();
            handler.error(Errno.valueOf(errno), path);
        }
        return res;
    }

    public int setenv(String envName, String envValue, int overwrite) {
        return libc().setenv(envName, envValue, overwrite);
    }

    public FileStat stat(String path) {
        FileStat stat = allocateStat();

        if (stat(path, stat) < 0) handler.error(Errno.valueOf(errno()), path);

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

    public boolean isNative() {
        return true;
    }

    public int posix_spawnp(String path, List<? extends SpawnFileAction> fileActions,
            List<? extends CharSequence> argv, List<? extends CharSequence> envp) {

        CharSequence[] nativeArgv = new CharSequence[argv.size()];
        argv.toArray(nativeArgv);

        CharSequence[] nativeEnv = new CharSequence[envp.size()];
        envp.toArray(nativeEnv);

        return posix_spawnp(path, fileActions, nativeArgv, nativeEnv);
    }

    public int posix_spawnp(String path, List<? extends SpawnFileAction> fileActions,
            CharSequence[] argv, CharSequence[] envp) {
        IntByReference pid = new IntByReference(-1);
        Pointer nativeFileActions = nativeFileActions(fileActions);

        try {
            if (libc().posix_spawnp(pid, path, nativeFileActions, null, argv, envp) < 0) {
                Errno e = Errno.valueOf(errno());
                handler.error(e, e.description());
            }
        } finally {
            libc.posix_spawn_file_actions_destroy(nativeFileActions);
        }

        return pid.getValue();
    }

    private final Pointer nativeFileActions(List<? extends SpawnFileAction> fileActions) {
        Pointer nativeFileActions = Memory.allocateDirect(getRuntime(), 128);
        libc().posix_spawn_file_actions_init(nativeFileActions);
        for (SpawnFileAction action : fileActions) {
            action.act(this, nativeFileActions);
        }

        return nativeFileActions;
    }

    public abstract BaseHeapFileStat allocateStat();

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

    public static final ToNativeConverter<FileStat, Struct> FileStatConverter = new ToNativeConverter<FileStat, Struct>() {

        public Struct toNative(FileStat value, ToNativeContext context) {
            if (!(value instanceof Struct)) {
                throw new IllegalArgumentException("FileStat instance is not a struct");
            }
            return (Struct) value;
        }

        public Class<Struct> nativeType() {
            return Struct.class;
        }

    };
}
