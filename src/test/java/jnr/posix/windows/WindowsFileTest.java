package jnr.posix.windows;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import jnr.posix.DummyPOSIXHandler;
import jnr.posix.FileStat;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.WindowsPOSIX;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WindowsFileTest {
    private static POSIX posix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    private class Pair {
        public File base;
        public File leaf;
        public Pair(File base, File leaf) {
            this.base = base;
            this.leaf = leaf;
        }

        public void cleanup() {
            cleanup(base);
        }

        public void cleanup(File node) {
            if (node.isDirectory()) {
                File[] files = node.listFiles();
                if (files != null) {
                    for(File file: files) {
                        cleanup(file);
                    }
                }
            }
            node.delete();
        }
    }
    // FIXME: This is a broken method since it does not delete any of the generated dirs.
    private static final String DIR_NAME = "0123456789";
    private Pair makeLongPath() throws IOException {
        File tmp = File.createTempFile("temp", Long.toHexString(System.nanoTime()));

        if (!(tmp.delete() && tmp.mkdir())) throw new IOException("Could not make a long path");

        StringBuilder buf = new StringBuilder(DIR_NAME);
        for (int i = 0; i < 30; i++) {
            buf.append(DIR_NAME).append('/');
        }
        File tmp2 = new File(tmp, buf.toString());
        tmp2.mkdirs();

        return new Pair(tmp, tmp2);
    }

    @Test
    public void testLowercaseDriveLetter() throws Throwable {
        // FIXME: Not all systems have a C drive but nearly all do
        FileStat st = posix.stat("c:/");
        assertTrue(st.dev() == 2);
        assertTrue(st.rdev() == 2);
    }

    @Test
    public void testFakeUIDGUID() throws Throwable {
        File f = File.createTempFile("stat", null);

        try {
            FileStat st = posix.stat(f.getAbsolutePath());
            assertTrue(st.uid() == 0);
            assertTrue(st.gid() == 0);
        } finally {
            f.delete();
        }
    }

    @Test
    public void testExecutableSuffixesAreExecutable() throws Throwable {
        File f = File.createTempFile("stat", ".exe");

        try {
            FileStat st = posix.stat(f.getAbsolutePath());
            assertEquals("100755", Integer.toOctalString(st.mode()));
            assertTrue(st.isExecutable());
        } finally {
            f.delete();
        }

        f = File.createTempFile("STAT", ".EXE");

        try {
            FileStat st = posix.stat(f.getAbsolutePath());
            assertEquals("100755", Integer.toOctalString(st.mode()));
            assertTrue(st.isExecutable());
        } finally {
            f.delete();
        }

    }

    @Test
    public void testBlocksAndBlockSizeReturn() throws Throwable {
        File f = File.createTempFile("stat", null);

        try {
            FileStat st = posix.stat(f.getAbsolutePath());
            assertTrue(st.blocks() == -1);
            assertTrue(st.blockSize() == -1);
        } finally {
            f.delete();
        }
    }

    @Test
        public void testLongFileRegular() throws Throwable {
        Pair pair = makeLongPath();
        String path = pair.leaf.getAbsolutePath();
        try {
            FileStat st = posix.stat(path);
            assertNotNull("posix.stat failed", st);

            FileStat stat = posix.allocateStat();
            int result = posix.stat(path, stat);
            assertNotNull("posix.stat failed", stat);
            assertEquals(0, result);
        } finally {
            pair.cleanup();
        }
    }

    @Test
    public void testLongFileUNC() throws Throwable {
        Pair pair = makeLongPath();
        String absolutePath = pair.leaf.getAbsolutePath();
        char letter = absolutePath.charAt(0);
        String path = absolutePath.replace(absolutePath.substring(0,2), "\\\\localhost\\" + letter + "$");
        try {
            FileStat st = posix.stat(path);
            assertNotNull("posix.stat failed", st);

            FileStat stat = posix.allocateStat();
            int result = posix.stat(path, stat);
            assertNotNull("posix.stat failed", stat);
            assertEquals(0, result);
        } finally {
            pair.cleanup();
        }
    }

    @Test
    public void statUNCFile() throws Throwable {
        File f = File.createTempFile("stat", null);
        String absolutePath = f.getAbsolutePath();
        char letter = absolutePath.charAt(0);
        String path = absolutePath.replace(absolutePath.substring(0,2), "\\\\localhost\\" + letter + "$");
        try {
            FileStat st = posix.stat(path);
            assertNotNull("posix.stat failed", st);

            FileStat stat = posix.allocateStat();
            int result = posix.stat(path, stat);
            assertNotNull("posix.stat failed", stat);
            assertEquals(0, result);
        } finally {
            f.delete();
        }
    }

    @Test
    public void unlinkTestWindows() throws Throwable {
        File tmp = File.createTempFile("unlinkTest", "tmp");
        RandomAccessFile raf = new RandomAccessFile(tmp, "rw");

        raf.write("hello".getBytes());

        // Windows won't allow you to delete open files, so we must
        // close the handle before trying to delete it.  Unfortunately,
        // this also means we're unable to write to the handle afterwards
        // as we do with the non-Windows test.
        raf.close();

        int res = posix.unlink(tmp.getCanonicalPath());

        assertEquals(0, res);
        assertFalse(tmp.exists());
    }

    @Test
    public void testFindFirstFile() throws Throwable {
        File f = File.createTempFile("stat", null);
        try {

            POSIX posix = POSIXFactory.getNativePOSIX();
            FileStat stat = posix.allocateStat();
            int result = ((WindowsPOSIX) posix).findFirstFile(f.getAbsolutePath(), stat);

            assertEquals(0, result);
            assertTrue(stat.isFile());
        } finally {
            f.delete();
        }
    }

    @Test
    public void testFindFirstFileBogusFile() throws Throwable {
        POSIX posix = POSIXFactory.getNativePOSIX();
        FileStat stat = posix.allocateStat();
        int result = ((WindowsPOSIX) posix).findFirstFile("sdjfhjfsdfhdsdfhsdj", stat);
        assertTrue(result < 0);
    }
}
