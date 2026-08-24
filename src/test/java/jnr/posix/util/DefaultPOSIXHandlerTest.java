package jnr.posix.util;

import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultPOSIXHandlerTest
{
    @Test
    public void testGetPid() {
        int pid_from_jmx = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);

        POSIX posix = POSIXFactory.getPOSIX(new DefaultPOSIXHandler(), true);
        int pid = posix.getpid();
        assertEquals(pid_from_jmx, pid);
    }
}
