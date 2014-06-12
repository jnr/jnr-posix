/*
 **** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2007 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package jnr.posix;

import jnr.constants.platform.Sysconf;
import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;
import jnr.ffi.types.clock_t;

import java.nio.ByteBuffer;
import jnr.ffi.annotations.Delegate;
import jnr.ffi.types.intptr_t;

public interface LibC {
    int chmod(CharSequence filename, int mode);
    int chown(CharSequence filename, int user, int group);
    int fstat(int fd, @Out @Transient FileStat stat);
    int fstat64(int fd, @Out @Transient FileStat stat);
    String getenv(CharSequence envName);
    @IgnoreError int getegid();
    int setegid(int egid);
    @IgnoreError int geteuid();
    int seteuid(int euid);
    @IgnoreError int getgid();
    String getlogin();
    int setgid(int gid);
    int getpgid();
    int getpgid(int pid);
    int setpgid(int pid, int pgid);
    int getpgrp();
    int setpgrp(int pid, int pgrp);
    @IgnoreError int getppid();
    @IgnoreError int getpid();
    NativePasswd getpwent();
    NativePasswd getpwuid(int which);
    NativePasswd getpwnam(CharSequence which);
    NativeGroup getgrent();
    NativeGroup getgrgid(int which);
    NativeGroup getgrnam(CharSequence which);
    int setpwent();
    int endpwent();
    int setgrent();
    int endgrent();
    @IgnoreError int getuid();
    int setsid();
    int setuid(int uid);
    int kill(int pid, int signal);

    int dup(int fd);
    int dup2(int oldFd, int newFd);

    int fcntl(int fd, int fnctl);
    int getdtablesize();

    public interface LibCSignalHandler {
        @Delegate void signal(int sig);
    }
    @intptr_t long signal(int sig, LibCSignalHandler handler);
    int lchmod(CharSequence filename, int mode);
    int lchown(CharSequence filename, int user, int group);
    int link(CharSequence oldpath, CharSequence newpath);
    int lstat(CharSequence path, @Out @Transient FileStat stat);
    int lstat64(CharSequence path, @Out @Transient FileStat stat);
    int mkdir(CharSequence path, int mode);
    int rmdir(CharSequence path);
    int stat(CharSequence path, @Out @Transient FileStat stat);
    int stat64(CharSequence path, @Out @Transient FileStat stat);
    int symlink(CharSequence oldpath, CharSequence newpath);
    int readlink(CharSequence oldpath, @Out ByteBuffer buffer, int len);
    int setenv(CharSequence envName, CharSequence envValue, int overwrite);
    @IgnoreError int umask(int mask);
    int unsetenv(CharSequence envName);
    int utimes(CharSequence path, @In Timeval[] times);
    int fork();
    int waitpid(long pid, @Out int[] status, int options);
    int wait(@Out int[] status);
    int getpriority(int which, int who);
    int setpriority(int which, int who, int prio);
    @IgnoreError int isatty(int fd);
    int read(int fd, @Out ByteBuffer dst, int len);
    int write(int fd, @In ByteBuffer src, int len);
    int read(int fd, @Out byte[] dst, int len);
    int write(int fd, @In byte[] src, int len);
    int pread(int fd, @Out ByteBuffer src, int len, int offset);
    int pread(int fd, @Out byte[] src, int len, int offset);
    int pwrite(int fd, @In ByteBuffer src, int len, int offset);
    int pwrite(int fd, @In byte[] src, int len, int offset);
    int lseek(int fd, int offset, int whence);
    int close(int fd);
    int execv(CharSequence path, @In CharSequence... argv);
    int execve(CharSequence path, @In CharSequence[] argv, @In CharSequence[] envp);
    int chdir(CharSequence path);

    public long sysconf(Sysconf name);
    public @clock_t long times(@Out @Transient NativeTimes tms);
    
    int flock(int fd, int mode);
    int unlink(CharSequence path);
    int open(CharSequence path, int flags, int perm);
    int pipe(@Out int[] fds);

    String gethostname();
}
