
package org.jruby.ext.posix;

import java.io.FileDescriptor;
import java.io.IOException;

public final class LazyPOSIX implements POSIX {

    private final POSIXHandler handler;
    private final boolean useNativePosix;
    private POSIX posix;

    public LazyPOSIX(POSIXHandler handler, boolean useNativePosix) {
        this.handler = handler;
        this.useNativePosix = useNativePosix;
    }

    private final POSIX p() {
        return posix != null ? posix : loadPOSIX();
    }

    private final synchronized POSIX loadPOSIX() {
        return posix != null
                ? posix
                : (posix = POSIXFactory.loadPOSIX(handler, useNativePosix));
    }


    public int chmod(String filename, int mode) {
        return p().chmod(filename, mode);
    }

    public int chown(String filename, int user, int group) {
        return p().chown(filename, user, group);
    }

    public int endgrent() {
        return p().endgrent();
    }

    public int endpwent() {
        return p().endpwent();
    }

    public int errno() {
        return p().errno();
    }

    public void errno(int value) {
        p().errno(value);
    }

    public int fork() {
        return p().fork();
    }

    public FileStat fstat(FileDescriptor descriptor) {
        return p().fstat(descriptor);
    }

    public int getegid() {
        return p().getegid();
    }

    public int geteuid() {
        return p().geteuid();
    }

    public int getgid() {
        return p().getgid();
    }

    public Group getgrent() {
        return p().getgrent();
    }

    public Group getgrgid(int which) {
        return p().getgrgid(which);
    }

    public Group getgrnam(String which) {
        return p().getgrnam(which);
    }

    public String getlogin() {
        return p().getlogin();
    }

    public int getpgid() {
        return p().getpgid();
    }

    public int getpgid(int pid) {
        return p().getpgid(pid);
    }

    public int getpgrp() {
        return p().getpgrp();
    }

    public int getpid() {
        return p().getpid();
    }

    public int getppid() {
        return p().getppid();
    }

    public int getpriority(int which, int who) {
        return p().getpriority(which, who);
    }

    public Passwd getpwent() {
        return p().getpwent();
    }

    public Passwd getpwnam(String which) {
        return p().getpwnam(which);
    }

    public Passwd getpwuid(int which) {
        return p().getpwuid(which);
    }

    public int getuid() {
        return p().getuid();
    }

    public boolean isatty(FileDescriptor descriptor) {
        return p().isatty(descriptor);
    }

    public int kill(int pid, int signal) {
        return p().kill(pid, signal);
    }

    public int lchmod(String filename, int mode) {
        return p().lchmod(filename, mode);
    }

    public int lchown(String filename, int user, int group) {
        return p().lchown(filename, user, group);
    }

    public int link(String oldpath, String newpath) {
        return p().link(oldpath, newpath);
    }

    public FileStat lstat(String path) {
        return p().lstat(path);
    }

    public int mkdir(String path, int mode) {
        return p().mkdir(path, mode);
    }

    public String readlink(String path) throws IOException {
        return p().readlink(path);
    }

    public int setegid(int egid) {
        return p().setegid(egid);
    }

    public int seteuid(int euid) {
        return p().seteuid(euid);
    }

    public int setgid(int gid) {
        return p().setgid(gid);
    }

    public int setgrent() {
        return p().setgrent();
    }

    public int setpgid(int pid, int pgid) {
        return p().setpgid(pid, pgid);
    }

    public int setpgrp(int pid, int pgrp) {
        return p().setpgrp(pid, pgrp);
    }

    public int setpriority(int which, int who, int prio) {
        return p().setpriority(which, who, prio);
    }

    public int setpwent() {
        return p().setpwent();
    }

    public int setsid() {
        return p().setsid();
    }

    public int setuid(int uid) {
        return p().setuid(uid);
    }

    public FileStat stat(String path) {
        return p().stat(path);
    }

    public int symlink(String oldpath, String newpath) {
        return p().symlink(oldpath, newpath);
    }

    public int umask(int mask) {
        return p().umask(mask);
    }

    public int utimes(String path, long[] atimeval, long[] mtimeval) {
        return p().utimes(path, atimeval, mtimeval);
    }

    public int wait(int[] status) {
        return p().wait(status);
    }

    public int waitpid(int pid, int[] status, int flags) {
        return p().waitpid(pid, status, flags);
    }

}
