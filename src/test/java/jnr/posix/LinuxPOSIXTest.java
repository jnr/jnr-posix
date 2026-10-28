package jnr.posix;

import jnr.constants.platform.AddressFamily;
import jnr.constants.platform.PosixFadvise;
import jnr.constants.platform.OpenFlags;
import jnr.constants.platform.Sock;
import jnr.constants.platform.SocketLevel;
import jnr.constants.platform.SocketOption;
import jnr.ffi.Platform;
import jnr.posix.junit.EnabledOnJnrOs;
import jnr.posix.junit.Modes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static jnr.posix.LinuxIoPrio.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@EnabledOnJnrOs(Platform.OS.LINUX)
class LinuxPOSIXTest {

    private static Linux linuxPOSIX = null;

    @BeforeAll
    static void setUpClass() {
        POSIX posix = POSIXFactory.getNativePOSIX();

        if (posix instanceof Linux) {
            linuxPOSIX = (Linux) posix;
        }
    }

    /**
     * Tests that IO priority can be set to a thread and that it doesn't change priority set on
     * another thread
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    @DisplayName("ioprio_set on one thread doesn't affect another thread's priority")
    void ioprioThreadedTest() throws InterruptedException, ExecutionException {
        linuxPOSIX.ioprio_set(IOPRIO_WHO_PROCESS, 0, IOPRIO_PRIO_VALUE(IOPRIO_CLASS_BE, 4));

        Future<Integer> threadPriorityFuture = Executors.newFixedThreadPool(1).submit(() -> {
            linuxPOSIX.ioprio_set(IOPRIO_WHO_PROCESS, 0, IOPRIO_PRIO_VALUE(IOPRIO_CLASS_IDLE, 7));
            return linuxPOSIX.ioprio_get(IOPRIO_WHO_PROCESS, 0);
        });

        int threadPriority = threadPriorityFuture.get();

        assertEquals(7, IOPRIO_PRIO_DATA(threadPriority));
        assertEquals(IOPRIO_CLASS_IDLE, IOPRIO_PRIO_CLASS(threadPriority));
        assertEquals(4, IOPRIO_PRIO_DATA(linuxPOSIX.ioprio_get(IOPRIO_WHO_PROCESS, 0)));
        assertEquals(IOPRIO_CLASS_BE, IOPRIO_PRIO_CLASS(linuxPOSIX.ioprio_get(IOPRIO_WHO_PROCESS, 0)));
    }

    @Test
    void testMessageHdrMultipleControl() {
        int[] fds = {0, 0};

        int ret = linuxPOSIX.socketpair(AddressFamily.AF_UNIX.intValue(),
                Sock.SOCK_STREAM.intValue(),
                0,
                fds);


        String data = "twoControlMessages";
        byte[] dataBytes = data.getBytes();

        assertTrue(ret >= 0);
        assertTrue(fds[0] > 0);
        assertTrue(fds[1] > 0);

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(1).flip();

        ret = linuxPOSIX.libc().setsockopt(fds[1],
                SocketLevel.SOL_SOCKET.intValue(),
                SocketOption.SO_PASSCRED.intValue(),
                buf,
                buf.remaining());

        assertTrue(ret >= 0);

        MsgHdr outMessage = linuxPOSIX.allocateMsgHdr();

        ByteBuffer[] outIov = new ByteBuffer[1];
        outIov[0] = ByteBuffer.allocateDirect(dataBytes.length);
        outIov[0].put(dataBytes);
        outIov[0].flip();

        outMessage.setIov(outIov);

        CmsgHdr[] outControl = outMessage.allocateControls(new int[]{4});
        outControl[0].setLevel(SocketLevel.SOL_SOCKET.intValue());
        outControl[0].setType(0x01);

        ByteBuffer fdBuf = ByteBuffer.allocateDirect(4);
        fdBuf.order(ByteOrder.nativeOrder());
        fdBuf.putInt(0, fds[0]);
        outControl[0].setData(fdBuf);

        int sendStatus = linuxPOSIX.sendmsg(fds[0], outMessage, 0);
        if (sendStatus == -1) {
            String sendmsgError = "Error with sendmsg: " + linuxPOSIX.strerror(linuxPOSIX.errno());
            fail(sendmsgError);
            return;
        }

        assertEquals(dataBytes.length, sendStatus);

        // ----------------

        MsgHdr inMessage = linuxPOSIX.allocateMsgHdr();
        ByteBuffer[] inIov = new ByteBuffer[1];
        inIov[0] = ByteBuffer.allocateDirect(1024);
        inMessage.setIov(inIov);

        inMessage.allocateControls(new int[]{4, 12});
        int recvStatus = linuxPOSIX.recvmsg(fds[1], inMessage, 0);

        assertEquals(dataBytes.length, recvStatus);

        assertEquals(2, inMessage.getControls().length);

        CmsgHdr[] controls = inMessage.getControls();
        for (CmsgHdr control : controls) {
            validateCmsghdr(control);
        }
    }

    private void validateCmsghdr(CmsgHdr control) {
        if (control.getLevel() == SocketLevel.SOL_SOCKET.intValue()
                && control.getType() == 0x01) {
            // Passing a FD
            ByteBuffer inFdBuf = control.getData();
            inFdBuf.order(ByteOrder.nativeOrder());

            int fd = inFdBuf.getInt();

            assertTrue(fd != 0);
        } else if (control.getLevel() == SocketLevel.SOL_SOCKET.intValue()
                && control.getType() == 0x02) {
            //Credentials
            ByteBuffer data = control.getData();
            data.order(ByteOrder.nativeOrder());

            int got_pid = data.getInt();
            int got_uid = data.getInt();
            int got_gid = data.getInt();

            assertEquals(linuxPOSIX.getpid(), got_pid);
            assertEquals(linuxPOSIX.getuid(), got_uid);
            assertEquals(linuxPOSIX.getgid(), got_gid);
        } else {
            fail("Unable to determine cmsghdr type");
        }
    }

    @ParameterizedTest
    @MethodSource("posixFadviseAdvicesInOrder")
    void testPosixFadvise(PosixFadvise advice, @TempDir File tempDir) throws Throwable {
        File file = File.createTempFile("posix_fadvise", null, tempDir);
        int fd = linuxPOSIX.open(file.getAbsolutePath(), OpenFlags.O_RDWR.intValue(), Modes.RA);

        try {
            assertEquals(0, linuxPOSIX.posix_fadvise(fd, 0, 0, advice));
        } finally {
            linuxPOSIX.close(fd);
        }
    }

    // normal last to reset it
    private static Stream<PosixFadvise> posixFadviseAdvicesInOrder() {
        return Stream.of(
                PosixFadvise.POSIX_FADV_SEQUENTIAL,
                PosixFadvise.POSIX_FADV_RANDOM,
                PosixFadvise.POSIX_FADV_NOREUSE,
                PosixFadvise.POSIX_FADV_WILLNEED,
                PosixFadvise.POSIX_FADV_DONTNEED,
                PosixFadvise.POSIX_FADV_NORMAL);
    }
}
