package jnr.posix;

import jnr.constants.platform.*;
import jnr.posix.junit.Modes;
import jnr.posix.util.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by headius on 5/31/14.
 */
class IOTest {
    private static POSIX posix;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    void testOpenReadWrite() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            File tmp = File.createTempFile("IOTest", "testOpen");
            int fd = posix.open(tmp.getPath(), OpenFlags.O_RDWR.intValue(), Modes.RWA);
            assertTrue(fd > 0);

            byte[] hello = "hello".getBytes();
            int written = posix.write(fd, hello, 5);
            assertEquals(5, written);

            byte[] buf = new byte[5];
            posix.lseekLong(fd, 0, 0); // no jnr-constants for SEEK_SET
            int read = posix.read(fd, buf, 5);
            assertEquals(5, read);
            assertArrayEquals(hello, buf);

            byte[] goodbye = "goodbye".getBytes();
            written = posix.pwrite(fd, goodbye, 7, 3);
            assertEquals(7, written);
            assertEquals(5, posix.lseekLong(fd, 0, 1)); // SEEK_CUR

            byte[] bye = new byte[3];
            read = posix.pread(fd, bye, 3, 7);
            assertEquals(3, read);
            assertEquals(5, posix.lseekLong(fd, 0, 1)); // SEEK_CUR
            assertArrayEquals("bye".getBytes(), bye);
        }
    }

    @Test
    void testPipe() {
        int[] fds = {0, 0};
        int ret = posix.pipe(fds);
        assertTrue(ret >= 0);
        assertTrue(fds[0] > 0);
        assertTrue(fds[1] > 0);

        byte[] hello = "hello".getBytes();
        int written = posix.write(fds[1], hello, 5);
        assertEquals(5, written);

        byte[] buf = new byte[5];
        int read = posix.read(fds[0], buf, 5);
        assertEquals(5, read);
        assertArrayEquals(buf, hello);
    }

    @Test
    void testPathconf() {
        if (Platform.IS_WINDOWS) {
            int res = posix.fpathconf(-1, Pathconf._PC_PIPE_BUF);
            assertEquals(-1, res);
            assertEquals(Errno.EOPNOTSUPP.intValue(), posix.errno());
        } else {
            int res = posix.fpathconf(-1, Pathconf._PC_PIPE_BUF);
            assertEquals(-1, res);
            
            int[] fds = {0, 0};
            posix.pipe(fds);
            res = posix.fpathconf(fds[1], Pathconf._PC_PIPE_BUF);
            if (Platform.IS_LINUX) {
                assertEquals(4096, res);
            } else {
                assertTrue(res > 0);
            }
        }
    }

    @Test
    void testSocketPair() {
        if (!Platform.IS_WINDOWS) {
            int[] fds = {0, 0};

            int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(), Sock.SOCK_STREAM.intValue(), 0, fds);

            assertTrue(ret >= 0);
            assertTrue(fds[0] > 0);
            assertTrue(fds[1] > 0);

            byte[] hello = "hello".getBytes();
            int written = posix.write(fds[1], hello, 5);
            assertEquals(5, written);

            byte[] buf = new byte[5];
            int read = posix.read(fds[0], buf, 5);
            assertEquals(5, read);
            assertArrayEquals(buf, hello);

            hello = "goodbye".getBytes();
            written = posix.write(fds[0], hello, 7);
            assertEquals(7, written);

            buf = new byte[7];
            read = posix.read(fds[1], buf, 7);
            assertEquals(7, read);
            assertArrayEquals(buf, hello);
        }
    }

    @Test
    void testSendRecvMsg_NoControl() {
        if (!Platform.IS_WINDOWS) {
            int[] fds = {0, 0};

            int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(), Sock.SOCK_STREAM.intValue(), 0, fds);

            assertTrue(ret >= 0);
            assertTrue(fds[0] > 0);
            assertTrue(fds[1] > 0);

            MsgHdr outMessage = posix.allocateMsgHdr();

            String data = "does this work?";
            byte[] dataBytes = data.getBytes();

            ByteBuffer[] outIov = new ByteBuffer[1];
            outIov[0] = ByteBuffer.allocateDirect(dataBytes.length);
            outIov[0].put(dataBytes);
            outIov[0].flip();

            outMessage.setIov(outIov);

            int sendStatus = posix.sendmsg(fds[0], outMessage, 0);

            assertEquals(sendStatus, dataBytes.length);

            // ----------------

            MsgHdr inMessage = posix.allocateMsgHdr();
            ByteBuffer[] inIov = new ByteBuffer[1];
            inIov[0] = ByteBuffer.allocateDirect(1024);
            inMessage.setIov(inIov);

            int recvStatus = posix.recvmsg(fds[1], inMessage, 0);

            assertEquals(recvStatus, dataBytes.length);

            for (int i = 0; i < recvStatus; ++i) {
                assertEquals(dataBytes[i], outIov[0].get(i));
            }
        }
    }

    @Test
    void testSendRecvMsg_WithControl() {
        if (!Platform.IS_WINDOWS) {
            int[] fds = {0, 0};

            int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(), Sock.SOCK_STREAM.intValue(), 0, fds);

            String data = "does this work?";
            byte[] dataBytes = data.getBytes();


            assertTrue(ret >= 0);
            assertTrue(fds[0] > 0);
            assertTrue(fds[1] > 0);

            MsgHdr outMessage = posix.allocateMsgHdr();

            ByteBuffer[] outIov = new ByteBuffer[1];
            outIov[0] = ByteBuffer.allocateDirect(dataBytes.length);
            outIov[0].put(dataBytes);
            outIov[0].flip();

            outMessage.setIov(outIov);

            CmsgHdr outControl = outMessage.allocateControl(4);
            outControl.setLevel(SocketLevel.SOL_SOCKET.intValue());
            outControl.setType(0x01);

            ByteBuffer fdBuf = ByteBuffer.allocateDirect(4);
            fdBuf.order(ByteOrder.nativeOrder());
            fdBuf.putInt(0, fds[0]);
            outControl.setData(fdBuf);

            int sendStatus = posix.sendmsg(fds[0], outMessage, 0);

            assertEquals(sendStatus, dataBytes.length);

            // ----------------

            MsgHdr inMessage = posix.allocateMsgHdr();
            ByteBuffer[] inIov = new ByteBuffer[1];
            inIov[0] = ByteBuffer.allocateDirect(1024);
            inMessage.setIov(inIov);

            inMessage.allocateControl(4);
            int recvStatus = posix.recvmsg(fds[1], inMessage, 0);

            assertEquals(recvStatus, dataBytes.length);

            ByteBuffer inFdBuf = inMessage.getControls()[0].getData();
            inFdBuf.order(ByteOrder.nativeOrder());

            int fd = inFdBuf.getInt();

            assertTrue(fd != 0);

            for (int i = 0; i < recvStatus; ++i) {
                assertEquals(dataBytes[i], outIov[0].get(i));
            }

        }
    }
}
