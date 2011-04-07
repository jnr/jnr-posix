
package org.jruby.ext.posix;

import java.io.FileDescriptor;
import java.io.IOException;

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
    
    public FileStat allocateStat() {
        return posix().allocateStat();
    }

    public int chmod(String filename, int mode) {
        return posix().chmod(filename, mode);
    }

    public int chown(String filename, int user, int group) {
        return posix().chown(filename, user, group);
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
    
    public int exec(String path, String[] args) {
        return posix().exec(path, args);
    }

    public int execv(String path, String... argv) {
        return posix().execv(path, argv);
    }
    
    public int fork() {
        return posix().fork();
    }

    public FileStat fstat(FileDescriptor descriptor) {
        return posix().fstat(descriptor);
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

    public boolean isatty(FileDescriptor descriptor) {
        return posix().isatty(descriptor);
    }

    public int kill(int pid, int signal) {
        return posix().kill(pid, signal);
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

    public int mkdir(String path, int mode) {
        return posix().mkdir(path, mode);
    }

    public String readlink(String path) throws IOException {
        return posix().readlink(path);
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
    
    public int aspawn(boolean overlay, String program, String[] argv, String path) {
        return posix().aspawn(overlay, program, argv, path);
    }
    
    public int spawn(boolean ovelay, String command, String program, String path) {
        return posix().spawn(ovelay, command, program, path);
    }

    public FileStat stat(String path) {
        return posix().stat(path);
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

    public int wait(int[] status) {
        return posix().wait(status);
    }

    public int waitpid(int pid, int[] status, int flags) {
        return posix().waitpid(pid, status, flags);
    }

    public boolean isNative() {
        return posix().isNative();
    }

    public LibC libc() {
        return posix().libc();
    }
}
