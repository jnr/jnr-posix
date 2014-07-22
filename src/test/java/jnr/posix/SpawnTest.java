package jnr.posix;

import jnr.constants.platform.windows.OpenFlags;
import jnr.ffi.Library;
import jnr.ffi.Platform;
import jnr.ffi.annotations.Out;
import org.junit.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static jnr.posix.SpawnFileAction.*;

public class SpawnTest {
    private static POSIX posix;
    private static LibC libc;
    private static final List<String> emptyEnv = Arrays.asList(new String[0]);
    private static final List<SpawnFileAction> emptyActions = Arrays.asList(new SpawnFileAction[0]);

    public static interface LibC {
        int pipe(@Out int[] fds);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (Platform.getNativePlatform().isUnix()) {
            posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
            libc = Library.loadLibrary(LibC.class, "c");
        }
    }

    @Test public void validPid() {
        if (Platform.getNativePlatform().isUnix()) {
            long pid = -1;
            try {
                pid = posix.posix_spawnp("true", emptyActions, Arrays.asList("true"), emptyEnv);
                assertTrue(pid != -1);
            } finally {
                if (pid != -1) posix.libc().waitpid((int) pid, null, 0);
            }
        }
    }

    @Test public void emptyCommand() {
        if (Platform.getNativePlatform().isUnix()) {
            long pid = -1;
            try {
                pid = posix.posix_spawnp("", emptyActions, Collections.EMPTY_LIST, emptyEnv);
                assertEquals(-1, pid);
            } finally {
                if (pid != -1) posix.libc().waitpid((int) pid, null, 0);
            }
        }
    }

    private static void closePipe(int[] fds) {
        posix.libc().close(fds[0]);
        posix.libc().close(fds[1]);
    }

    private static void killChild(long pid) {
        if (pid > 0) {
            posix.libc().kill((int) pid, 9); posix.libc().waitpid((int) pid, null, 0);
        }
    }

    @Test public void outputPipe() {
        if (Platform.getNativePlatform().isUnix()) {
            int[] outputPipe = { -1, -1 };
            long pid = -1;
            try {
                assertFalse(libc.pipe(outputPipe) < 0);
                assertNotSame(-1, outputPipe[0]);
                assertNotSame(-1, outputPipe[1]);

                List<SpawnFileAction> actions = Arrays.asList(dup(outputPipe[1], 1));
                pid = posix.posix_spawnp("echo", actions, Arrays.asList("echo", "bar"), emptyEnv);
                assertTrue(pid != -1);

                // close the write side of the output pipe, so read() will return immediately once the process has exited
                posix.libc().close(outputPipe[1]);

                ByteBuffer output = ByteBuffer.allocate(100);
                long nbytes = posix.libc().read(outputPipe[0], output, output.remaining());
                assertEquals(4L, nbytes);
                output.position((int) nbytes).flip();
                byte[] bytes = new byte[output.remaining()];
                output.get(bytes);
                assertEquals("bar", new String(bytes).trim());
            } finally {
                closePipe(outputPipe);
                killChild(pid);
            }
        }
    }

    @Test public void inputPipe() {
        if (Platform.getNativePlatform().isUnix()) {
            int[] outputPipe = { -1, -1 };
            int[] inputPipe = { -1, -1 };
            long pid = -1;
            try {
            assertFalse(libc.pipe(outputPipe) < 0);
                assertFalse(libc.pipe(inputPipe) < 0);
                assertNotSame(-1, outputPipe[0]);
                assertNotSame(-1, outputPipe[1]);
                assertNotSame(-1, inputPipe[0]);
                assertNotSame(-1, inputPipe[1]);

                List<SpawnFileAction> actions = Arrays.asList(dup(inputPipe[0], 0), dup(outputPipe[1], 1));
                pid = posix.posix_spawnp("cat", actions, Arrays.asList("cat", "-"), emptyEnv);
                assertTrue(pid != -1);
                posix.libc().close(inputPipe[0]);
                assertEquals(3, posix.libc().write(inputPipe[1], ByteBuffer.wrap("foo".getBytes(Charset.forName("US-ASCII"))), 3));
                posix.libc().close(inputPipe[1]); // send EOF to process

                // close the write side of the output pipe, so read() will return immediately once the process has exited
                posix.libc().close(outputPipe[1]);

                ByteBuffer output = ByteBuffer.allocate(100);
                long nbytes = posix.libc().read(outputPipe[0], output, output.remaining());
                assertEquals(3L, nbytes);
                output.position((int) nbytes).flip();
                byte[] bytes = new byte[output.remaining()];
                output.get(bytes);
                assertEquals("foo", new String(bytes).trim());
            } finally {
                closePipe(outputPipe);
                closePipe(inputPipe);
                killChild(pid);
            }
        }
    }

    @Test public void inputFile() throws IOException {
        if (Platform.getNativePlatform().isUnix()) {
            File inputFile = File.createTempFile("foo", null);
            FileOutputStream inputStream = new FileOutputStream(inputFile);
            inputStream.write("foo".getBytes("US-ASCII"));
            inputStream.close();
            int[] outputPipe = { -1, -1 };
            long pid = -1;
            try {
                assertFalse(libc.pipe(outputPipe) < 0);
                assertNotSame(-1, outputPipe[0]);
                assertNotSame(-1, outputPipe[1]);

                List<SpawnFileAction> actions = Arrays.asList(open(inputFile.getAbsolutePath(), 0, OpenFlags.O_RDONLY.intValue(), 0444),
                        dup(outputPipe[1], 1));
                pid = posix.posix_spawnp("cat", actions, Arrays.asList("cat", "-"), emptyEnv);
                assertTrue(pid != -1);

                // close the write side of the output pipe, so read() will return immediately once the process has exited
                posix.libc().close(outputPipe[1]);

                ByteBuffer output = ByteBuffer.allocate(100);
                long nbytes = posix.libc().read(outputPipe[0], output, output.remaining());
                assertEquals(3L, nbytes);
                output.position((int) nbytes).flip();
                byte[] bytes = new byte[output.remaining()];
                output.get(bytes);
                assertEquals("foo", new String(bytes).trim());
            } finally {
                closePipe(outputPipe);
                killChild(pid);
            }
        }
    }

    @Test public void closeInput() throws IOException {
        if (Platform.getNativePlatform().isUnix()) {
            int[] outputPipe = { -1, -1 };
            int[] inputPipe = { -1, -1 };
            long pid = -1;
            try {
                assertFalse(libc.pipe(outputPipe) < 0);
                assertFalse(libc.pipe(inputPipe) < 0);
                assertNotSame(-1, outputPipe[0]);
                assertNotSame(-1, outputPipe[1]);
                assertNotSame(-1, inputPipe[0]);
                assertNotSame(-1, inputPipe[1]);

                List<SpawnFileAction> actions = Arrays.asList(dup(outputPipe[1], 1),
                        open("/dev/null", 2, OpenFlags.O_WRONLY.intValue(), 0444),
                        close(inputPipe[0]), close(inputPipe[1]));
                pid = posix.posix_spawnp("cat", actions, Arrays.asList("cat", "/dev/fd/" + inputPipe[0]), emptyEnv);
                assertTrue(pid != -1);
                assertEquals(3, posix.libc().write(inputPipe[1], ByteBuffer.wrap("foo".getBytes(Charset.forName("US-ASCII"))), 3));
                posix.libc().close(inputPipe[1]); // send EOF to process

                // close the write side of the output pipe, so read() will return immediately once the process has exited
                posix.libc().close(outputPipe[1]);

                // Output from the process on stdout should be empty
                ByteBuffer output = ByteBuffer.allocate(100);
                long nbytes = posix.libc().read(outputPipe[0], output, output.remaining());
                assertEquals(0L, nbytes);
            } finally {
                closePipe(inputPipe);
                closePipe(outputPipe);
                killChild(pid);
            }
        }
    }
}
