
package jnr.posix;

import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import jnr.posix.util.ProcessMaker;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import jnr.constants.platform.Confstr;
import jnr.constants.platform.Pathconf;
import jnr.constants.platform.Signal;

final class LazyPOSIX implements POSIX {

    private final POSIXHandler handler;
    private final boolean useNativePosix;

    // NOTE: because all implementations of POSIX that are loaded via loadPOSIX()
    // are immutable, there is no need for 'posix' to be a volatile field.
    // See http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
    // (but since volatile reads on x86 and x86_64 are cheap, do it anyway)
    private volatile POSIX posix;

    LazyPOSIX(POSIXHandler handler, boolean useNativePosix) {
        this.handler = handler;
        this.useNativePosix = useNativePosix;
    }

    private final POSIX posix() {
        return posix != null ? posix : loadPOSIX();
    }

    private final synchronized POSIX loadPOSIX() {
        return posix != null
                ? posix
                : (posix = POSIXFactory.loadPOSIX(handler, useNativePosix));
    }

    public ProcessMaker newProcessMaker(String... command) {
        return posix().newProcessMaker(command);
    }

    public ProcessMaker newProcessMaker() {
        return posix().newProcessMaker();
    }
    
    public FileStat allocateStat() {
        return posix().allocateStat();
    }

    public MsgHdr allocateMsgHdr() {
        return posix().allocateMsgHdr();
    }

    public int chdir(String path) {
        return posix().chdir(path);
    }

    public int chmod(String filename, int mode) {
        return posix().chmod(filename, mode);
    }

    public int fchmod(int fd, int mode) {
        return posix().fchmod(fd, mode);
    }

    public int chown(String filename, int user, int group) {
        return posix().chown(filename, user, group);
    }

    public CharSequence crypt(CharSequence key, CharSequence salt) {
        return posix().crypt(key, salt);
    }

    public byte[] crypt(byte[] key, byte[] salt) {
        return posix().crypt(key, salt);
    }

    public int fchown(int fd, int user, int group) {
        return posix().fchown(fd, user, group);
    }

    public int endgrent() {
        return posix().endgrent();
    }

    public int endpwent() {
        return posix().endpwent();
    }

    public int errno() {
        return posix().errno();
    }

    public void errno(int value) {
        posix().errno(value);
    }
    
    public int exec(String path, String... args) {
        return posix().exec(path, args);
    }
    
    public int exec(String path, String[] args, String[] envp) {
        return posix().exec(path, args, envp);
    }

    public int execv(String path, String[] argv) {
        return posix().execv(path, argv);
    }

    public int execve(String path, String[] argv, String[] envp) {
        return posix().execve(path, argv, envp);
    }
    
    public int fork() {
        return posix().fork();
    }

    public FileStat fstat(int fd) {
        return posix().fstat(fd);
    }

    public int fstat(int fd, FileStat stat) {
        return posix().fstat(fd, stat);
    }

    public FileStat fstat(FileDescriptor descriptor) {
        return posix().fstat(descriptor);
    }

    public int fstat(FileDescriptor descriptor, FileStat stat) {
        return posix().fstat(descriptor, stat);
    }

    public int getegid() {
        return posix().getegid();
    }

    public int geteuid() {
        return posix().geteuid();
    }

    public int getgid() {
        return posix().getgid();
    }

    public int getdtablesize() {
        return posix().getdtablesize();
    }

    public Group getgrent() {
        return posix().getgrent();
    }

    public Group getgrgid(int which) {
        return posix().getgrgid(which);
    }

    public Group getgrnam(String which) {
        return posix().getgrnam(which);
    }

    public String getlogin() {
        return posix().getlogin();
    }

    public int getpgid() {
        return posix().getpgid();
    }

    public int getpgid(int pid) {
        return posix().getpgid(pid);
    }

    public int getpgrp() {
        return posix().getpgrp();
    }

    public int getpid() {
        return posix().getpid();
    }

    public int getppid() {
        return posix().getppid();
    }

    public int getpriority(int which, int who) {
        return posix().getpriority(which, who);
    }

    public Passwd getpwent() {
        return posix().getpwent();
    }

    public Passwd getpwnam(String which) {
        return posix().getpwnam(which);
    }

    public Passwd getpwuid(int which) {
        return posix().getpwuid(which);
    }

    public int getuid() {
        return posix().getuid();
    }

    public int getrlimit(int resource, RLimit rlim) {
        return posix().getrlimit(resource, rlim);
    }

    public int getrlimit(int resource, Pointer rlim) {
        return posix().getrlimit(resource, rlim);
    }

    public RLimit getrlimit(int resource) {
        return posix().getrlimit(resource);
    }

    public int setrlimit(int resource, RLimit rlim) {
        return posix().setrlimit(resource, rlim);
    }

    public int setrlimit(int resource, Pointer rlim) {
        return posix().setrlimit(resource, rlim);
    }

    public int setrlimit(int resource, long rlimCur, long rlimMax) {
        return posix().setrlimit(resource, rlimCur, rlimMax);
    }

    public boolean isatty(FileDescriptor descriptor) {
        return posix().isatty(descriptor);
    }

    public int isatty(int descriptor) {
        return posix().isatty(descriptor);
    }

    public int kill(int pid, int signal) {
        return kill((long) pid, signal);
    }

    public int kill(long pid, int signal) {
        return posix().kill(pid, signal);
    }
    
    public SignalHandler signal(Signal sig, SignalHandler handler) {
        return posix().signal(sig, handler);
    }

    public int raise(int sig) {
        return posix().raise(sig);
    }

    public int lchmod(String filename, int mode) {
        return posix().lchmod(filename, mode);
    }

    public int lchown(String filename, int user, int group) {
        return posix().lchown(filename, user, group);
    }

    public int link(String oldpath, String newpath) {
        return posix().link(oldpath, newpath);
    }

    public FileStat lstat(String path) {
        return posix().lstat(path);
    }

    public int lstat(String path, FileStat stat) {
        return posix().lstat(path, stat);
    }

    public int mkdir(String path, int mode) {
        return posix().mkdir(path, mode);
    }

    public String readlink(String path) throws IOException {
        return posix().readlink(path);
    }

    public int readlink(CharSequence path, byte[] buf, int bufsize) {
        return posix().readlink(path, buf, bufsize);
    }

    public int readlink(CharSequence path, ByteBuffer buf, int bufsize) {
        return posix().readlink(path, buf, bufsize);
    }

    public int readlink(CharSequence path, Pointer bufPtr, int bufsize) {
        return posix().readlink(path, bufPtr, bufsize);
    }

    public int rmdir(String path) {
        return posix().rmdir(path);
    }

    public int setegid(int egid) {
        return posix().setegid(egid);
    }

    public int seteuid(int euid) {
        return posix().seteuid(euid);
    }

    public int setgid(int gid) {
        return posix().setgid(gid);
    }

    public int setgrent() {
        return posix().setgrent();
    }

    public int setpgid(int pid, int pgid) {
        return posix().setpgid(pid, pgid);
    }

    public int setpgrp(int pid, int pgrp) {
        return posix().setpgrp(pid, pgrp);
    }

    public int setpriority(int which, int who, int prio) {
        return posix().setpriority(which, who, prio);
    }

    public int setpwent() {
        return posix().setpwent();
    }

    public int setsid() {
        return posix().setsid();
    }

    public int setuid(int uid) {
        return posix().setuid(uid);
    }

    public FileStat stat(String path) {
        return posix().stat(path);
    }

    public int stat(String path, FileStat stat) {
        return posix().stat(path, stat);
    }

    public int symlink(String oldpath, String newpath) {
        return posix().symlink(oldpath, newpath);
    }

    public int umask(int mask) {
        return posix().umask(mask);
    }

    public int utimes(String path, long[] atimeval, long[] mtimeval) {
        return posix().utimes(path, atimeval, mtimeval);
    }

    public int utimes(String path, Pointer times) {
        return posix().utimes(path, times);
    }

    public int futimes(int fd, long[] atimeval, long[] mtimeval) {
        return posix().futimes(fd, atimeval, mtimeval);
    }

    public int lutimes(String path, long[] atimeval, long[] mtimeval) {
        return posix().lutimes(path, atimeval, mtimeval);
    }

    public int utimensat(int dirfd, String path, long[] atimespec, long[] mtimespec, int flag) {
        return posix().utimensat(dirfd, path, atimespec, mtimespec, flag);
    }

    public int utimensat(int dirfd, String path, Pointer times, int flag) {
        return posix().utimensat(dirfd, path, times, flag);
    }

    public int futimens(int fd, long[] atimespec, long[] mtimespec) {
        return posix().futimens(fd, atimespec, mtimespec);
    }

    public int futimens(int fd, Pointer times) {
        return posix().futimens(fd, times);
    }

    public int wait(int[] status) {
        return posix().wait(status);
    }

    public int waitpid(int pid, int[] status, int flags) {
        return waitpid((long) pid, status, flags);
    }

    public int waitpid(long pid, int[] status, int flags) {
        return posix().waitpid(pid, status, flags);
    }

    public boolean isNative() {
        return posix().isNative();
    }

    public LibC libc() {
        return posix().libc();
    }

    public Pointer environ() {
        return posix().environ();
    }

    public String getenv(String envName) {
        return posix().getenv(envName);
    }

    public int setenv(String envName, String envValue, int overwrite) {
        return posix().setenv(envName, envValue, overwrite);
    }

    public int unsetenv(String envName) {
        return posix().unsetenv(envName);
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions, Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        return posix().posix_spawnp(path, fileActions, argv, envp);
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                             Collection<? extends SpawnAttribute> spawnAttributes,
                             Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        return posix().posix_spawnp(path, fileActions, spawnAttributes, argv, envp);
    }

    public long sysconf(Sysconf name) {
        return posix().sysconf(name);
    }

    public int confstr(Confstr name, ByteBuffer buf, int len) {
        return libc().confstr(name, buf, len);
    }

    public int fpathconf(int fd, Pathconf name) {
        return libc().fpathconf(fd, name);
    }

    public Times times() {
        return posix().times();
    }
    
    public int flock(int fd, int mode) {
        return posix().flock(fd, mode);
    }

    public int dup(int fd) {
        return posix().dup(fd);
    }

    public int dup2(int oldFd, int newFd) {
        return posix().dup2(oldFd, newFd);
    }

    public int fcntlInt(int fd, Fcntl fcntlConst, int arg) {
        return posix().fcntlInt(fd, fcntlConst, arg);
    }

    public int fcntl(int fd, Fcntl fcntlConst) {
        return posix().fcntl(fd, fcntlConst);
    }

    public int fcntl(int fd, Fcntl fcntlConst, int arg) {
        return posix().fcntl(fd, fcntlConst, arg);
    }

    @Deprecated
    public int fcntl(int fd, Fcntl fcntlConst, int... arg) {
        return posix().fcntl(fd, fcntlConst);
    }

    public int access(CharSequence path, int amode) {
        return posix().access(path, amode);
    }

    public int close(int fd) {
        return posix().close(fd);
    }

    public int unlink(CharSequence path) {
        return posix().unlink(path);
    }

    public int open(CharSequence path, int flags, int perm) {
        return posix().open(path, flags, perm);
    }

    public long read(int fd, byte[] buf, long n) {
        return posix().read(fd, buf, n);
    }
    public long write(int fd, byte[] buf, long n) {
        return posix().write(fd, buf, n);
    }
    public long read(int fd, ByteBuffer buf, long n) {
        return posix().read(fd, buf, n);
    }
    public long write(int fd, ByteBuffer buf, long n) {
        return posix().write(fd, buf, n);
    }
    public long pread(int fd, byte[] buf, long n, long offset) {
        return posix().pread(fd, buf, n, offset);
    }
    public long pwrite(int fd, byte[] buf, long n, long offset) {
        return posix().pwrite(fd, buf, n, offset);
    }
    public long pread(int fd, ByteBuffer buf, long n, long offset) {
        return posix().pread(fd, buf, n, offset);
    }
    public long pwrite(int fd, ByteBuffer buf, long n, long offset) {
        return posix().pwrite(fd, buf, n, offset);
    }

    public int read(int fd, byte[] buf, int n)
    {
        return posix().read(fd, buf, n);
    }
    public int write(int fd, byte[] buf, int n) {
        return posix().write(fd, buf, n);
    }
    public int read(int fd, ByteBuffer buf, int n) {
        return posix().read(fd, buf, n);
    }
    public int write(int fd, ByteBuffer buf, int n) {
        return posix().write(fd, buf, n);
    }
    public int pread(int fd, byte[] buf, int n, int offset) {
        return posix().pread(fd, buf, n, offset);
    }
    public int pwrite(int fd, byte[] buf, int n, int offset) {
        return posix().pwrite(fd, buf, n, offset);
    }
    public int pread(int fd, ByteBuffer buf, int n, int offset) {
        return posix().pread(fd, buf, n, offset);
    }
    public int pwrite(int fd, ByteBuffer buf, int n, int offset) {
        return posix().pwrite(fd, buf, n, offset);
    }

    public int lseek(int fd, long offset, int whence) {
        return posix().lseek(fd, offset, whence);
    }

    public long lseekLong(int fd, long offset, int whence) {
        return posix().lseekLong(fd, offset, whence);
    }

    public int pipe(int[] fds) {
        return posix().pipe(fds);
    }

    public int socketpair(int domain, int type, int protocol, int[] fds) {
        return posix().socketpair(domain, type, protocol, fds);
    }

    public int sendmsg(int socket, MsgHdr message, int flags) {
        return posix().sendmsg(socket, message, flags);
    }

    public int recvmsg(int socket, MsgHdr message, int flags) {
        return posix().recvmsg(socket, message, flags);
    }

    public int truncate(CharSequence path, long length) {
        return posix().truncate(path, length);
    }

    public int ftruncate(int fd, long offset) {
        return posix().ftruncate(fd, offset);
    }

    public int rename(CharSequence oldName, CharSequence newName) {
        return posix().rename(oldName, newName);
    }

    public String getcwd() {
        return posix().getcwd();
    }

    public int fsync(int fd) {
        return posix().fsync(fd);
    }

    public int fdatasync(int fd) {
        return posix().fdatasync(fd);
    }

    public int mkfifo(String path, int mode) {
        return posix().mkfifo(path, mode);
    }

    public String gethostname() {
        return posix().gethostname();
    }

    public int daemon(int nochdir, int noclose) {
        return posix().daemon(nochdir, noclose);
    }

    public long[] getgroups() {
        return posix().getgroups();
    }

    public int getgroups(int size, int[] groups) {
        return posix().getgroups(size, groups);
    }

    public String nl_langinfo(int item) {
        return posix().nl_langinfo(item);
    }

    public String setlocale(int category, String locale) {
        return posix().setlocale(category, locale);
    }

    @Override
    public String strerror(int code) {
        return posix().strerror(code);
    }

    @Override
    public Timeval allocateTimeval() { return posix().allocateTimeval(); }

    @Override
    public int gettimeofday(Timeval tv) { return posix().gettimeofday(tv); }
}
