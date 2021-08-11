package jnr.posix;

import jnr.ffi.Pointer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

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
        ByteBuffer nativePath;

        public Open(String path, int fd, int flags, int mode) {
            this.path = path;
            this.fd = fd;
            this.flags = flags;
            this.mode = mode;
        }

        final boolean act(POSIX posix, Pointer nativeFileActions) {
            /*
            This logic allocates a direct ByteBuffer to use for the path in order to work around systems that have not
            patched CVE-2014-4043, in which older glibc versions do not make a defensive copy of the file path passed to
            posix_spawn_file_actions_addopen. The buffer may be freed by the caller before it can be used in an
            eventual posix_spawn call.

            See https://bugzilla.redhat.com/show_bug.cgi?id=1983750 for a RHEL version of this issue.
             */

            // determine encoded byte array length
            CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
            int bpc = (int) encoder.maxBytesPerChar();
            int size = (path.length() + 1) * bpc;

            // transcode to native buffer
            nativePath = ByteBuffer.allocateDirect(size);
            encoder.encode(CharBuffer.wrap(path), nativePath, true);
            nativePath.flip();

            // null terminate
            nativePath.limit(nativePath.limit() + bpc);

            return ((UnixLibC) posix.libc()).posix_spawn_file_actions_addopen(nativeFileActions, fd, nativePath, flags, mode) == 0;
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
