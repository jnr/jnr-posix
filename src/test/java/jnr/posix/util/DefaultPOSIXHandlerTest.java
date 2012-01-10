package jnr.posix.util;

import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DefaultPOSIXHandlerTest
{
    @Test
    public void testGetPid() throws Exception
    {
        int pid_from_jmx = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);

        POSIX posix = POSIXFactory.getPOSIX(new DefaultPOSIXHandler(), true);
        int pid = posix.getpid();
        assertThat(pid, equalTo(pid_from_jmx));
    }
}
