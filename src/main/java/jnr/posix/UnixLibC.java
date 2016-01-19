package jnr.posix;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.ByReference;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.NumberByReference;
import jnr.ffi.byref.ShortByReference;
import jnr.ffi.types.pid_t;

public interface UnixLibC extends LibC {
    public int posix_spawn(@Out ByReference pid, @In CharSequence path, @In Pointer fileActions,
                           @In Pointer attr, @In CharSequence[] argv, @In CharSequence[] envp);

    public int posix_spawnp(@Out ByReference pid, @In CharSequence path, @In Pointer fileActions,
                            @In Pointer attr, @In CharSequence[] argv, @In CharSequence[] envp);

    public int posix_spawn_file_actions_init(Pointer fileActions);
    public int posix_spawn_file_actions_destroy(Pointer fileActions);
    public int posix_spawn_file_actions_addclose(Pointer fileActions, int filedes);
    public int posix_spawn_file_actions_addopen(Pointer fileActions, int filedes, CharSequence path,
                                                int oflag, int mode);
    public int posix_spawn_file_actions_adddup2(Pointer fileActions, int filedes, int newfiledes);
    public int posix_spawnattr_init(Pointer attr);
    public int posix_spawnattr_destroy(Pointer attr);
    public int posix_spawnattr_setflags(Pointer attr, short flags);
    public int posix_spawnattr_getflags(Pointer attr, ShortByReference flags);
    public int posix_spawnattr_setpgroup(Pointer attr, @pid_t long pgroup);
    public int posix_spawnattr_getpgroup(Pointer attr, NumberByReference pgroup);
    public int posix_spawnattr_setsigmask(Pointer attr, Pointer sigmask);
    public int posix_spawnattr_getsigmask(Pointer attr, Pointer sigmask);
    public int posix_spawnattr_setsigdefault(Pointer attr, Pointer sigdefault);
    public int posix_spawnattr_getsigdefault(Pointer attr, Pointer sigdefault);
    public int sigprocmask(int how, Pointer set, Pointer get);

    int mkfifo(CharSequence filename, int mode);
}
