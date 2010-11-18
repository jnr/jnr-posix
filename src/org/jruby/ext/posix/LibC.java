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
package org.jruby.ext.posix;

import com.kenai.jaffl.annotations.IgnoreError;
import com.kenai.jaffl.annotations.In;
import com.kenai.jaffl.annotations.Out;
import com.kenai.jaffl.annotations.Transient;
import java.nio.ByteBuffer;

public interface LibC {
    int chmod(CharSequence filename, int mode);
    int chown(CharSequence filename, int user, int group);
    int fstat(int fd, @Out @Transient FileStat stat);
    int fstat64(int fd, @Out @Transient FileStat stat);
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
    int lchmod(CharSequence filename, int mode);
    int lchown(CharSequence filename, int user, int group);
    int link(CharSequence oldpath, CharSequence newpath);
    int lstat(CharSequence path, @Out @Transient FileStat stat);
    int lstat64(CharSequence path, @Out @Transient FileStat stat);
    int mkdir(CharSequence path, int mode);
    int stat(CharSequence path, @Out @Transient FileStat stat);
    int stat64(CharSequence path, @Out @Transient FileStat stat);
    int symlink(CharSequence oldpath, CharSequence newpath);
    int readlink(CharSequence oldpath, @Out ByteBuffer buffer, int len);
    @IgnoreError int umask(int mask);
    int utimes(CharSequence path, @In Timeval[] times);
    int fork();
    int waitpid(int pid, @Out int[] status, int options);
    int wait(@Out int[] status);
    int getpriority(int which, int who);
    int setpriority(int which, int who, int prio);
    @IgnoreError int isatty(int fd);
    int read(int fd, @Out ByteBuffer dst, int len);
    int write(int fd, @In ByteBuffer src, int len);
    int close(int fd);
}
