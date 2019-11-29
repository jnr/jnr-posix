package jnr.posix;

import jnr.constants.platform.*;
import jnr.posix.util.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by headius on 5/31/14.
 */
public class IOTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    @Test
    public void testOpenReadWrite() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            File tmp = File.createTempFile("IOTest", "testOpen");
            int fd = posix.open(tmp.getPath(), OpenFlags.O_RDWR.intValue(), 0666);
            Assert.assertTrue(fd > 0);

            byte[] hello = "hello".getBytes();
            int written = posix.write(fd, hello, 5);
            Assert.assertEquals(5, written);

            byte[] buf = new byte[5];
            posix.lseekLong(fd, 0, 0); // no jnr-constants for SEEK_SET
            int read = posix.read(fd, buf, 5);
            Assert.assertEquals(5, read);
            Assert.assertArrayEquals(hello, buf);

            byte[] goodbye = "goodbye".getBytes();
            written = posix.pwrite(fd, goodbye, 7, 3);
            Assert.assertEquals(7, written);
            Assert.assertEquals(5, posix.lseekLong(fd, 0, 1)); // SEEK_CUR

            byte[] bye = new byte[3];
            read = posix.pread(fd, bye, 3, 7);
            Assert.assertEquals(3, read);
            Assert.assertEquals(5, posix.lseekLong(fd, 0, 1)); // SEEK_CUR
            Assert.assertArrayEquals("bye".getBytes(), bye);
        }
    }

    @Test
    public void testPipe() throws Throwable {
        int[] fds = {0, 0};
        int ret = posix.pipe(fds);
        Assert.assertTrue(ret >= 0);
        Assert.assertTrue(fds[0] > 0);
        Assert.assertTrue(fds[1] > 0);

        byte[] hello = "hello".getBytes();
        int written = posix.write(fds[1], hello, 5);
        Assert.assertEquals(5, written);

        byte[] buf = new byte[5];
        int read = posix.read(fds[0], buf, 5);
        Assert.assertEquals(5, read);
        Assert.assertArrayEquals(buf, hello);
    }

    @Test
    public void testSocketPair() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            int[] fds = {0, 0};

            int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(), Sock.SOCK_STREAM.intValue(), 0, fds);

            Assert.assertTrue(ret >= 0);
            Assert.assertTrue(fds[0] > 0);
            Assert.assertTrue(fds[1] > 0);

            byte[] hello = "hello".getBytes();
            int written = posix.write(fds[1], hello, 5);
            Assert.assertEquals(5, written);

            byte[] buf = new byte[5];
            int read = posix.read(fds[0], buf, 5);
            Assert.assertEquals(5, read);
            Assert.assertArrayEquals(buf, hello);

            hello = "goodbye".getBytes();
            written = posix.write(fds[0], hello, 7);
            Assert.assertEquals(7, written);

            buf = new byte[7];
            read = posix.read(fds[1], buf, 7);
            Assert.assertEquals(7, read);
            Assert.assertArrayEquals(buf, hello);
        }
    }

    @Test
    public void testSendRecvMsg_NoControl() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            int[] fds = {0, 0};

            int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(), Sock.SOCK_STREAM.intValue(), 0, fds);

            Assert.assertTrue(ret >= 0);
            Assert.assertTrue(fds[0] > 0);
            Assert.assertTrue(fds[1] > 0);

            MsgHdr outMessage = posix.allocateMsgHdr();

            String data = "does this work?";
            byte[] dataBytes = data.getBytes();

            ByteBuffer[] outIov = new ByteBuffer[1];
            outIov[0] = ByteBuffer.allocateDirect(dataBytes.length);
            outIov[0].put(dataBytes);
            outIov[0].flip();

            outMessage.setIov(outIov);

            int sendStatus = posix.sendmsg(fds[0], outMessage, 0);

            Assert.assertTrue(sendStatus == dataBytes.length);

            // ----------------

            MsgHdr inMessage = posix.allocateMsgHdr();
            ByteBuffer[] inIov = new ByteBuffer[1];
            inIov[0] = ByteBuffer.allocateDirect(1024);
            inMessage.setIov(inIov);

            int recvStatus = posix.recvmsg(fds[1], inMessage, 0);

            Assert.assertTrue(recvStatus == dataBytes.length);

            for (int i = 0; i < recvStatus; ++i) {
                Assert.assertEquals(dataBytes[i], outIov[0].get(i));
            }
        }
    }

    @Test
    public void testSendRecvMsg_WithControl() throws Throwable {
        if (!Platform.IS_WINDOWS) {
            int[] fds = {0, 0};

            int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(), Sock.SOCK_STREAM.intValue(), 0, fds);

            String data = "does this work?";
            byte[] dataBytes = data.getBytes();


            Assert.assertTrue(ret >= 0);
            Assert.assertTrue(fds[0] > 0);
            Assert.assertTrue(fds[1] > 0);

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

            Assert.assertTrue(sendStatus == dataBytes.length);

            // ----------------

            MsgHdr inMessage = posix.allocateMsgHdr();
            ByteBuffer[] inIov = new ByteBuffer[1];
            inIov[0] = ByteBuffer.allocateDirect(1024);
            inMessage.setIov(inIov);

            inMessage.allocateControl(4);
            int recvStatus = posix.recvmsg(fds[1], inMessage, 0);

            Assert.assertTrue(recvStatus == dataBytes.length);

            ByteBuffer inFdBuf = inMessage.getControls()[0].getData();
            inFdBuf.order(ByteOrder.nativeOrder());

            int fd = inFdBuf.getInt();

            Assert.assertTrue(fd != 0);

            for (int i = 0; i < recvStatus; ++i) {
                Assert.assertEquals(dataBytes[i], outIov[0].get(i));
            }

        }
    }

     @Test
    public void testMessageHdrMultipleControl(){
        if (Platform.IS_WINDOWS) {
            return;
        }

        int[] fds = {0, 0};

        int ret = posix.socketpair(AddressFamily.AF_UNIX.intValue(),
                Sock.SOCK_STREAM.intValue(),
                0,
                fds);


        String data = "twoControlMessages";
        byte[] dataBytes = data.getBytes();

        Assert.assertTrue(ret >= 0);
        Assert.assertTrue(fds[0] > 0);
        Assert.assertTrue(fds[1] > 0);

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(1).flip();

        ret = posix.libc().setsockopt(fds[1],
                SocketLevel.SOL_SOCKET.intValue(),
                jnr.constants.platform.SocketOption.SO_PASSCRED.intValue(),
                buf,
                buf.remaining());

        Assert.assertTrue(ret >= 0);

        MsgHdr outMessage = posix.allocateMsgHdr();

        ByteBuffer[] outIov = new ByteBuffer[1];
        outIov[0] = ByteBuffer.allocateDirect(dataBytes.length);
        outIov[0].put(dataBytes);
        outIov[0].flip();

        outMessage.setIov(outIov);

        CmsgHdr[] outControl = outMessage.allocateControls(new int[]{ 4 });
        outControl[0].setLevel(SocketLevel.SOL_SOCKET.intValue());
        outControl[0].setType(0x01);

        ByteBuffer fdBuf = ByteBuffer.allocateDirect(4);
        fdBuf.order(ByteOrder.nativeOrder());
        fdBuf.putInt(0, fds[0]);
        outControl[0].setData(fdBuf);

        int sendStatus = posix.sendmsg(fds[0], outMessage, 0);
        if( sendStatus == -1 ){
            String sendmsgError = "Error with sendmsg: " + posix.strerror(posix.errno());
            Assert.fail(sendmsgError);
            return;
        }

        Assert.assertEquals(dataBytes.length, sendStatus);

        // ----------------

        MsgHdr inMessage = posix.allocateMsgHdr();
        ByteBuffer[] inIov = new ByteBuffer[1];
        inIov[0] = ByteBuffer.allocateDirect(1024);
        inMessage.setIov(inIov);

        inMessage.allocateControls(new int[]{ 4, 12 });
        int recvStatus = posix.recvmsg(fds[1], inMessage, 0);

        Assert.assertTrue(recvStatus == dataBytes.length);

        Assert.assertTrue(inMessage.getControls().length == 2);

        CmsgHdr[] controls = inMessage.getControls();
        for( int x = 0; x < controls.length; x++ ){
            validateCmsghdr( controls[x] );
        }
    }

    private void validateCmsghdr(CmsgHdr control){
        if( control.getLevel() == SocketLevel.SOL_SOCKET.intValue()
                && control.getType() == 0x01 ){
            // Passing a FD
            ByteBuffer inFdBuf = control.getData();
            inFdBuf.order(ByteOrder.nativeOrder());

            int fd = inFdBuf.getInt();

            Assert.assertTrue(fd != 0);
        }else if( control.getLevel() == SocketLevel.SOL_SOCKET.intValue()
                && control.getType() == 0x02 ){
            //Credentials
            ByteBuffer data = control.getData();
            data.order(ByteOrder.nativeOrder());

            int got_pid = data.getInt();
            int got_uid = data.getInt();
            int got_gid = data.getInt();

            Assert.assertEquals( posix.getpid(), got_pid );
            Assert.assertEquals( posix.getuid(), got_uid );
            Assert.assertEquals( posix.getgid(), got_gid );
        }else{
            Assert.fail( "Unable to determine cmsghdr type" );
        }
    }
}
