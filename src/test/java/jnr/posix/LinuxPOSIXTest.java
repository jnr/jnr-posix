package jnr.posix;

import jnr.constants.platform.AddressFamily;
import jnr.constants.platform.Sock;
import jnr.constants.platform.SocketLevel;
import jnr.constants.platform.SocketOption;
import jnr.ffi.Platform;
import jnr.posix.util.ConditionalTestRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static jnr.posix.LinuxIoPrio.*;

public class LinuxPOSIXTest {

    @ClassRule
    public static ConditionalTestRule rule = new ConditionalTestRule() {
        public boolean isSatisfied() {
            Platform platform = Platform.getNativePlatform();
            Platform.OS os = platform.getOS();
            Platform.CPU cpu = platform.getCPU();

            if (os != Platform.OS.LINUX) return false;

            switch (cpu) {
                case PPC:
                case PPC64:
                case PPC64LE:
                    return false;
            }

            return true;
        }
    };

    private static Linux linuxPOSIX = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    public void ioprioThreadedTest() throws InterruptedException, ExecutionException {
        linuxPOSIX.ioprio_set(IOPRIO_WHO_PROCESS, 0, IOPRIO_PRIO_VALUE(IOPRIO_CLASS_BE, 4));

        Future<Integer> threadPriorityFuture = Executors.newFixedThreadPool(1).submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                linuxPOSIX.ioprio_set(IOPRIO_WHO_PROCESS, 0, IOPRIO_PRIO_VALUE(IOPRIO_CLASS_IDLE, 7));
                return linuxPOSIX.ioprio_get(IOPRIO_WHO_PROCESS, 0);
            }
        });

        int threadPriority = threadPriorityFuture.get().intValue();

        Assert.assertEquals(7, IOPRIO_PRIO_DATA(threadPriority));
        Assert.assertEquals(IOPRIO_CLASS_IDLE, IOPRIO_PRIO_CLASS(threadPriority));
        Assert.assertEquals(4, IOPRIO_PRIO_DATA(linuxPOSIX.ioprio_get(IOPRIO_WHO_PROCESS, 0)));
        Assert.assertEquals(IOPRIO_CLASS_BE, IOPRIO_PRIO_CLASS(linuxPOSIX.ioprio_get(IOPRIO_WHO_PROCESS, 0)));
    }

    @Test
    public void testMessageHdrMultipleControl() {
        if (jnr.posix.util.Platform.IS_WINDOWS) {
            return;
        }

        int[] fds = {0, 0};

        int ret = linuxPOSIX.socketpair(AddressFamily.AF_UNIX.intValue(),
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

        ret = linuxPOSIX.libc().setsockopt(fds[1],
                SocketLevel.SOL_SOCKET.intValue(),
                SocketOption.SO_PASSCRED.intValue(),
                buf,
                buf.remaining());

        Assert.assertTrue(ret >= 0);

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
            Assert.fail(sendmsgError);
            return;
        }

        Assert.assertEquals(dataBytes.length, sendStatus);

        // ----------------

        MsgHdr inMessage = linuxPOSIX.allocateMsgHdr();
        ByteBuffer[] inIov = new ByteBuffer[1];
        inIov[0] = ByteBuffer.allocateDirect(1024);
        inMessage.setIov(inIov);

        inMessage.allocateControls(new int[]{4, 12});
        int recvStatus = linuxPOSIX.recvmsg(fds[1], inMessage, 0);

        Assert.assertEquals(dataBytes.length, recvStatus);

        Assert.assertEquals(2, inMessage.getControls().length);

        CmsgHdr[] controls = inMessage.getControls();
        for (int x = 0; x < controls.length; x++) {
            validateCmsghdr(controls[x]);
        }
    }

    private void validateCmsghdr(CmsgHdr control) {
        if (control.getLevel() == SocketLevel.SOL_SOCKET.intValue()
                && control.getType() == 0x01) {
            // Passing a FD
            ByteBuffer inFdBuf = control.getData();
            inFdBuf.order(ByteOrder.nativeOrder());

            int fd = inFdBuf.getInt();

            Assert.assertTrue(fd != 0);
        } else if (control.getLevel() == SocketLevel.SOL_SOCKET.intValue()
                && control.getType() == 0x02) {
            //Credentials
            ByteBuffer data = control.getData();
            data.order(ByteOrder.nativeOrder());

            int got_pid = data.getInt();
            int got_uid = data.getInt();
            int got_gid = data.getInt();

            Assert.assertEquals(linuxPOSIX.getpid(), got_pid);
            Assert.assertEquals(linuxPOSIX.getuid(), got_uid);
            Assert.assertEquals(linuxPOSIX.getgid(), got_gid);
        } else {
            Assert.fail("Unable to determine cmsghdr type");
        }
    }
}

