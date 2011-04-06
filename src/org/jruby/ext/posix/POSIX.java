package org.jruby.ext.posix;

import java.io.FileDescriptor;
import java.io.IOException;


public interface POSIX {
    FileStat allocateStat();
    int chmod(String filename, int mode);
    int chown(String filename, int user, int group);
    int fork();
    FileStat fstat(FileDescriptor descriptor);
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
    int lchmod(String filename, int mode);
    int lchown(String filename, int user, int group);
    int link(String oldpath,String newpath);
    FileStat lstat(String path);
    int mkdir(String path, int mode);
    String readlink(String path) throws IOException;
    int setsid();
    int setgid(int gid);
    int setegid(int egid);
    int setpgid(int pid, int pgid);
    int setpgrp(int pid, int pgrp);
    int setpriority(int which, int who, int prio);
    int setuid(int uid);
    FileStat stat(String path);
    int symlink(String oldpath,String newpath);
    int umask(int mask);
    int utimes(String path, long[] atimeval, long[] mtimeval);
    int waitpid(int pid, int[] status, int flags);
    int wait(int[] status);
    int errno();
    void errno(int value);
    int execv(String path, String... argv);
    boolean isNative();
    int aspawn(boolean overlay, String program, String[] argv, String path);
    int spawn(boolean ovelay, String command, String program, String path);
    /** Returns null if isNative returns false. */
    LibC libc();
}
