package jnr.posix;

import jnr.ffi.Platform;
import jnr.posix.util.ConditionalTestRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import static jnr.posix.LinuxIoPrio.*;

public class LinuxPOSIXTest {

    @ClassRule
    public static RunningOnLinux rule = new RunningOnLinux();

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
}

class RunningOnLinux extends ConditionalTestRule {
    public boolean isSatisfied() {
        return jnr.ffi.Platform.getNativePlatform().getOS().equals(Platform.OS.LINUX);
    }
}
