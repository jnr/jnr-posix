package jnr.posix;

import jnr.ffi.Pointer;

public abstract class SpawnFileAction {
    abstract boolean act(POSIX posix, Pointer nativeFileActions);

    public static SpawnFileAction dup(int fd, int newfd) {
        return new Dup(fd, newfd);
    }

    public static SpawnFileAction open(String path, int fd, int flags, int mode) {
        return new Open(path, fd, flags, mode);
    }

    public static SpawnFileAction close(int fd) {
        return new Close(fd);
    }

    private static final class Dup extends SpawnFileAction {
        final int fd, newfd;

        public Dup(int fd, int newfd) {
            this.fd = fd;
            this.newfd = newfd;
        }

        final boolean act(POSIX posix, Pointer nativeFileActions) {
            return ((UnixLibC) posix.libc()).posix_spawn_file_actions_adddup2(nativeFileActions, fd, newfd) == 0;
        }

        public String toString() {
            return "SpawnFileAction::Dup(old = " + fd + ", new = " + newfd + ")";
        }
    }

    private static final class Open extends SpawnFileAction {
        final String path;
        final int fd;
        final int flags, mode;

        public Open(String path, int fd, int flags, int mode) {
            this.path = path;
            this.fd = fd;
            this.flags = flags;
            this.mode = mode;
        }

        final boolean act(POSIX posix, Pointer nativeFileActions) {
            return ((UnixLibC) posix.libc()).posix_spawn_file_actions_addopen(nativeFileActions, fd, path, flags, mode) == 0;
        }

        public String toString() {
            return "SpawnFileAction::Open(path = '" + path + "', fd = " + fd + ", flags = " + Integer.toHexString(flags) + ", mode = " + Integer.toHexString(mode) + ")";
        }
    }

    private static final class Close extends SpawnFileAction {
        final int fd;

        public Close(int fd) {
            this.fd = fd;
        }

        final boolean act(POSIX posix, Pointer nativeFileActions) {
            return ((UnixLibC) posix.libc()).posix_spawn_file_actions_addclose(nativeFileActions, fd) == 0;
        }

        public String toString() {
            return "SpawnFileAction::Close(fd = " + fd + ")";
        }
    }
}
