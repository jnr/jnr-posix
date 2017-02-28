package jnr.posix;

import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Signal;
import jnr.constants.platform.Sysconf;
import jnr.ffi.Pointer;
import jnr.posix.util.MethodName;
import jnr.posix.util.ProcessMaker;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;

final class CheckedPOSIX implements POSIX {
    private final POSIX posix;
    private final POSIXHandler handler;
    
    CheckedPOSIX(POSIX posix, POSIXHandler handler) {
        this.posix = posix;
        this.handler = handler;
    }

    private <T> T unimplementedNull() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return null;
    }

    private int unimplementedInt() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return -1;
    }

    private boolean unimplementedBool() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return false;
    }

    private String unimplementedString() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return null;
    }

    public ProcessMaker newProcessMaker(String... command) {
        try { return posix.newProcessMaker(command); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public ProcessMaker newProcessMaker() {
        try { return posix.newProcessMaker(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public FileStat allocateStat() {
        try { return posix.allocateStat(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public MsgHdr allocateMsgHdr() {
        try { return posix.allocateMsgHdr(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int chdir(String path) {
        try { return posix.chdir(path); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int chmod(String filename, int mode) {
        try { return posix.chmod(filename, mode); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int fchmod(int fd, int mode) {
        try { return posix.fchmod(fd, mode); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int chown(String filename, int user, int group) {
        try { return posix.chown(filename, user, group); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public CharSequence crypt(CharSequence key, CharSequence salt) {
        try { return posix.crypt(key, salt); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public byte[] crypt(byte[] key, byte[] salt) {
        try { return posix.crypt(key, salt); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int fchown(int fd, int user, int group) {
        try { return posix.fchown(fd, user, group); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int endgrent() {
        try { return posix.endgrent(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int endpwent() {
        try { return posix.endpwent(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int errno() {
        return posix.errno();
    }

    public void errno(int value) {
        posix.errno(value);
    }

    public int exec(String path, String... args) {
        try { return posix.exec(path, args); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int exec(String path, String[] args, String[] envp) {
        try { return posix.exec(path, args, envp); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int execv(String path, String[] argv) {
        try { return posix.execv(path, argv); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int execve(String path, String[] argv, String[] envp) {
        try { return posix.execve(path, argv, envp); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int fork() {
        try { return posix.fork(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public FileStat fstat(int fd) {
        try { return posix.fstat(fd); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int fstat(int fd, FileStat stat) {
        try { return posix.fstat(fd, stat); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public FileStat fstat(FileDescriptor descriptor) {
        try { return posix.fstat(descriptor); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int fstat(FileDescriptor descriptor, FileStat stat) {
        try { return posix.fstat(descriptor, stat); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getegid() {
        try { return posix.getegid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int geteuid() {
        try { return posix.geteuid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getgid() {
        try { return posix.getgid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getdtablesize() {
        try { return posix.getdtablesize(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public Group getgrent() {
        try { return posix.getgrent(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public Group getgrgid(int which) {
        try { return posix.getgrgid(which); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public Group getgrnam(String which) {
        try { return posix.getgrnam(which); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public String getlogin() {
        try { return posix.getlogin(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int getpgid() {
        try { return posix.getpgid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getpgid(int pid) {
        try { return posix.getpgid(pid); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getpgrp() {
        try { return posix.getpgrp(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getpid() {
        try { return posix.getpid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getppid() {
        try { return posix.getppid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getpriority(int which, int who) {
        try { return posix.getpriority(which, who); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public Passwd getpwent() {
        try { return posix.getpwent(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public Passwd getpwnam(String which) {
        try { return posix.getpwnam(which); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public Passwd getpwuid(int which) {
        try { return posix.getpwuid(which); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int getuid() {
        try { return posix.getuid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getrlimit(int resource, RLimit rlim) {
        try { return posix.getrlimit(resource, rlim); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int getrlimit(int resource, Pointer rlim) {
        try { return posix.getrlimit(resource, rlim); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public RLimit getrlimit(int resource) {
        try { return posix.getrlimit(resource); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int setrlimit(int resource, RLimit rlim) {
        try { return posix.setrlimit(resource, rlim); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setrlimit(int resource, Pointer rlim) {
        try { return posix.setrlimit(resource, rlim); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setrlimit(int resource, long rlimCur, long rlimMax) {
        try { return posix.setrlimit(resource, rlimCur, rlimMax); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public boolean isatty(FileDescriptor descriptor) {
        try { return posix.isatty(descriptor); } catch (UnsatisfiedLinkError ule) { return unimplementedBool(); }
    }

    public int isatty(int descriptor) {
        try { return posix.isatty(descriptor); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int kill(int pid, int signal) {
        return kill((long) pid, signal);
    }

    public int kill(long pid, int signal) {
        try { return posix.kill(pid, signal); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public SignalHandler signal(Signal sig, SignalHandler handler) {
        try { return posix.signal(sig, handler); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int lchmod(String filename, int mode) {
        try { return posix.lchmod(filename, mode); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int lchown(String filename, int user, int group) {
        try { return posix.lchown(filename, user, group); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int link(String oldpath, String newpath) {
        try { return posix.link(oldpath, newpath); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public FileStat lstat(String path) {
        try { return posix.lstat(path); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int lstat(String path, FileStat stat) {
        try { return posix.lstat(path, stat); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int mkdir(String path, int mode) {
        try { return posix.mkdir(path, mode); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public String readlink(String path) throws IOException {
        try { return posix.readlink(path); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int readlink(CharSequence path, byte[] buf, int bufsize) {
        try { return posix.readlink(path, buf, bufsize); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int readlink(CharSequence path, ByteBuffer buf, int bufsize) {
        try { return posix.readlink(path, buf, bufsize); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int readlink(CharSequence path, Pointer bufPtr, int bufsize) {
        try { return posix.readlink(path, bufPtr, bufsize); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int rmdir(String path) {
        try { return posix.rmdir(path); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setegid(int egid) {
        try { return posix.setegid(egid); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int seteuid(int euid) {
        try { return posix.seteuid(euid); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setgid(int gid) {
        try { return posix.setgid(gid); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setgrent() {
        try { return posix.setgrent(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setpgid(int pid, int pgid) {
        try { return posix.setpgid(pid, pgid); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setpgrp(int pid, int pgrp) {
        try { return posix.setpgrp(pid, pgrp); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setpriority(int which, int who, int prio) {
        try { return posix.setpriority(which, who, prio); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setpwent() {
        try { return posix.setpwent(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setsid() {
        try { return posix.setsid(); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int setuid(int uid) {
        try { return posix.setuid(uid); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public FileStat stat(String path) {
        try { return posix.stat(path); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int stat(String path, FileStat stat) {
        try { return posix.stat(path, stat); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int symlink(String oldpath, String newpath) {
        try { return posix.symlink(oldpath, newpath); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int umask(int mask) {
        try { return posix.umask(mask); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int utimes(String path, long[] atimeval, long[] mtimeval) {
        try { return posix.utimes(path, atimeval, mtimeval); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int utimes(String path, Pointer times) {
        try { return posix.utimes(path, times); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int futimes(int fd, long[] atimeval, long[] mtimeval) {
        try { return posix.futimes(fd, atimeval, mtimeval); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int lutimes(String path, long[] atimeval, long[] mtimeval) {
        try { return posix.lutimes(path, atimeval, mtimeval); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int wait(int[] status) {
        try { return posix.wait(status); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int waitpid(int pid, int[] status, int flags) {
        return waitpid((long)pid, status, flags);
    }

    public int waitpid(long pid, int[] status, int flags) {
        try { return posix.waitpid(pid, status, flags); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public boolean isNative() {
        return posix.isNative();
    }

    public LibC libc() {
        return posix.libc();
    }

    public Pointer environ() {
        try { return posix.environ(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public String getenv(String envName) {
        try { return posix.getenv(envName); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    public int setenv(String envName, String envValue, int overwrite) {
        try { return posix.setenv(envName, envValue, overwrite); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int unsetenv(String envName) {
        try { return posix.unsetenv(envName); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions, Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        try { return posix.posix_spawnp(path, fileActions, argv, envp); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                             Collection<? extends SpawnAttribute> spawnAttributes,
                             Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp) {
        try { return posix.posix_spawnp(path, fileActions, spawnAttributes, argv, envp); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }


    public long sysconf(Sysconf name) {
        try { return posix.sysconf(name); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public Times times() {
        try { return posix.times(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }
    
    public int flock(int fd, int mode) {
        return posix.flock(fd, mode);
    }

    public int dup(int fd) {
        try { return posix.dup(fd); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int dup2(int oldFd, int newFd) {
        try { return posix.dup2(oldFd, newFd); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int fcntlInt(int fd, Fcntl fcntlConst, int arg) {
        try { return posix.fcntlInt(fd, fcntlConst, arg); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int fcntl(int fd, Fcntl fcntlConst) {
        try { return posix.fcntl(fd, fcntlConst); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int fcntl(int fd, Fcntl fcntlConst, int... arg) {
        try { return posix.fcntl(fd, fcntlConst); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int access(CharSequence path, int amode) {
        try { return posix.access(path, amode); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int close(int fd) {
        try { return posix.close(fd); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int unlink(CharSequence path) {
        try { return posix.unlink(path); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int open(CharSequence path, int flags, int perm) {
        try { return posix.open(path, flags, perm); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public long read(int fd, byte[] buf, long n) {
        try { return posix.read(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long write(int fd, byte[] buf, long n) {
        try { return posix.write(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long read(int fd, ByteBuffer buf, long n) {
        try { return posix.read(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long write(int fd, ByteBuffer buf, long n) {
        try { return posix.write(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long pread(int fd, byte[] buf, long n, long offset) {
        try { return posix.pread(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long pwrite(int fd, byte[] buf, long n, long offset) {
        try { return posix.pwrite(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long pread(int fd, ByteBuffer buf, long n, long offset) {
        try { return posix.pread(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public long pwrite(int fd, ByteBuffer buf, long n, long offset) {
        try { return posix.pwrite(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int read(int fd, byte[] buf, int n) {
        try { return posix.read(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int write(int fd, byte[] buf, int n) {
        try { return posix.write(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int read(int fd, ByteBuffer buf, int n) {
        try { return posix.read(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int write(int fd, ByteBuffer buf, int n) {
        try { return posix.write(fd, buf, n); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int pread(int fd, byte[] buf, int n, int offset) {
        try { return posix.pread(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int pwrite(int fd, byte[] buf, int n, int offset) {
        try { return posix.pwrite(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int pread(int fd, ByteBuffer buf, int n, int offset) {
        try { return posix.pread(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
    public int pwrite(int fd, ByteBuffer buf, int n, int offset) {
        try { return posix.pwrite(fd, buf, n, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int lseek(int fd, long offset, int whence) {
        try { return posix.lseek(fd, offset, whence); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public long lseekLong(int fd, long offset, int whence) {
        try { return posix.lseekLong(fd, offset, whence); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int pipe(int[] fds) {
        try {return posix.pipe(fds); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int socketpair(int domain, int type, int protocol, int[] fds) {
        try {return posix.socketpair(domain, type, protocol, fds); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int sendmsg(int socket, MsgHdr message, int flags) {
        try {return posix.sendmsg(socket, message, flags); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int recvmsg(int socket, MsgHdr message, int flags) {
        try {return posix.recvmsg(socket, message, flags); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int truncate(CharSequence path, long length) {
        try { return posix.truncate(path, length); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int ftruncate(int fd, long offset) {
        try {return posix.ftruncate(fd, offset); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int rename(CharSequence oldName, CharSequence newName) {
        try {return posix.rename(oldName, newName); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public String getcwd() {
        try {return posix.getcwd(); } catch (UnsatisfiedLinkError ule) { return unimplementedString(); }
    }

    public int fsync(int fd) {
        try {return posix.fsync(fd); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int fdatasync(int fd) {
        try {return posix.fsync(fd); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int mkfifo(String path, int mode) {
        try {return posix.mkfifo(path, mode); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public int daemon(int nochdir, int noclose) {
        try {return posix.daemon(nochdir, noclose); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public long[] getgroups() {
        try {return posix.getgroups(); } catch (UnsatisfiedLinkError ule) { return null; }
    }

    public int getgroups(int size, int[] groups) {
        try {return posix.getgroups(size, groups); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }

    public String nl_langinfo(int item) {
        try {return posix.nl_langinfo(item); } catch (UnsatisfiedLinkError ule) { return unimplementedString(); }
    }

    public String setlocale(int category, String locale) {
        try {return posix.setlocale(category, locale); } catch (UnsatisfiedLinkError ule) { return unimplementedString(); }
    }

    @Override
    public String strerror(int code) {
        try {return posix.strerror(code); } catch (UnsatisfiedLinkError ule) { return unimplementedString(); }
    }

    @Override
    public Timeval allocateTimeval() {
        try {return posix.allocateTimeval(); } catch (UnsatisfiedLinkError ule) { return unimplementedNull(); }
    }

    @Override
    public int gettimeofday(Timeval tv) {
        try {return posix.gettimeofday(tv); } catch (UnsatisfiedLinkError ule) { return unimplementedInt(); }
    }
}
