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
    public int chmod(CharSequence filename, int mode);
    public int chown(CharSequence filename, int user, int group);
    public int fstat(int fd, @Out @Transient FileStat stat);
    public int fstat64(int fd, @Out @Transient FileStat stat);
    @IgnoreError public int getegid();
    public int setegid(int egid);
    @IgnoreError public int geteuid();
    public int seteuid(int euid);
    @IgnoreError public int getgid();
    public String getlogin();
    public int setgid(int gid);
    public int getpgid();
    public int getpgid(int pid);
    public int setpgid(int pid, int pgid);
    public int getpgrp();
    public int setpgrp(int pid, int pgrp);
    @IgnoreError public int getppid();
    @IgnoreError public int getpid();
    public NativePasswd getpwent();
    public NativePasswd getpwuid(int which);
    public NativePasswd getpwnam(CharSequence which);
    public NativeGroup getgrent();
    public NativeGroup getgrgid(int which);
    public NativeGroup getgrnam(CharSequence which);
    public int setpwent();
    public int endpwent();
    public int setgrent();
    public int endgrent();
    @IgnoreError public int getuid();
    public int setsid();
    public int setuid(int uid);
    public int kill(int pid, int signal);
    public int lchmod(CharSequence filename, int mode);
    public int lchown(CharSequence filename, int user, int group);
    public int link(CharSequence oldpath, CharSequence newpath);
    public int lstat(CharSequence path, @Out @Transient FileStat stat);
    public int lstat64(CharSequence path, @Out @Transient FileStat stat);
    public int mkdir(CharSequence path, int mode);
    public int stat(CharSequence path, @Out @Transient FileStat stat);
    public int stat64(CharSequence path, @Out @Transient FileStat stat);
    public int symlink(CharSequence oldpath, CharSequence newpath);
    public int readlink(CharSequence oldpath, @Out ByteBuffer buffer, int len);
    @IgnoreError public int umask(int mask);
    public int utimes(CharSequence path, @In Timeval[] times);
    public int fork();
    public int waitpid(int pid, @Out int[] status, int options);
    public int wait(@Out int[] status);
    public int getpriority(int which, int who);
    public int setpriority(int which, int who, int prio);
    @IgnoreError public int isatty(int fd);
}
