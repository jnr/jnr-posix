package jnr.posix;

import jnr.constants.platform.Fcntl;
import jnr.constants.platform.Sysconf;
import jnr.posix.util.ProcessMaker;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Collection;
import jnr.constants.platform.Signal;

public interface POSIX {
    FileStat allocateStat();
    int chmod(String filename, int mode);
    int chown(String filename, int user, int group);
    /**
     * Shell expanding and escaping version of exec which handles all the
     * preparation of a command line or command list.
     */
    int exec(String path, String... argv);
    
    /**
     * Shell expanding and escaping version of exec which handles all the
     * preparation of a command line or command list.
     */    
    int exec(String path, String[] argv, String[] envp);
    
    int execv(String path, String[] argv);  
    int execve(String path, String[] argv, String[] envp);    
    int fork();
    FileStat fstat(FileDescriptor descriptor);
    FileStat fstat(int descriptor);
    int fstat(FileDescriptor descriptor, FileStat stat);
    int fstat(int fd, FileStat stat);
    String getenv(String envName);
    int getegid();
    int geteuid();
    int seteuid(int euid);
    int getgid();
    String getlogin();
    int getpgid();
    int getpgid(int pid);
    int getpgrp();
    int getpid();
    int getppid();
    int getpriority(int which, int who);
    Passwd getpwent();
    Passwd getpwuid(int which);
    Passwd getpwnam(String which);
    Group getgrgid(int which);
    Group getgrnam(String which);
    Group getgrent();
    int endgrent();
    int setgrent();
    int endpwent();
    int setpwent();
    int getuid();
    boolean isatty(FileDescriptor descriptor);
    int kill(int pid, int signal);
    SignalHandler signal(Signal sig, SignalHandler handler);
    int lchmod(String filename, int mode);
    int lchown(String filename, int user, int group);
    int link(String oldpath,String newpath);
    FileStat lstat(String path);
    int lstat(String path, FileStat stat);
    int mkdir(String path, int mode);
    String readlink(String path) throws IOException;
    int rmdir(String path);
    int setenv(String envName, String envValue, int overwrite); // 0 no !0 yes
    int setsid();
    int setgid(int gid);
    int setegid(int egid);
    int setpgid(int pid, int pgid);
    int setpgrp(int pid, int pgrp);
    int setpriority(int which, int who, int prio);
    int setuid(int uid);
    FileStat stat(String path);
    int stat(String path, FileStat stat);
    int symlink(String oldpath,String newpath);
    int umask(int mask);
    int unsetenv(String envName);
    int utimes(String path, long[] atimeval, long[] mtimeval);
    int waitpid(int pid, int[] status, int flags);
    int wait(int[] status);
    int errno();
    void errno(int value);
    int chdir(String path);
    boolean isNative();
    /** Returns null if isNative returns false. */
    LibC libc();
    ProcessMaker newProcessMaker(String... command);
    ProcessMaker newProcessMaker();

    public long sysconf(Sysconf name);
    public Times times();

    public long posix_spawnp(String path, Collection<? extends SpawnFileAction> fileActions,
                            Collection<? extends CharSequence> argv, Collection<? extends CharSequence> envp);
    
    public int flock(int fd, int operation);

    int dup(int fd);

    int dup2(int oldFd, int newFd);

    int fcntl(int fd, Fcntl fcntlConst, int... arg);
}
