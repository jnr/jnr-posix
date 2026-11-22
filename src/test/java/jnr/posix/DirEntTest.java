package jnr.posix;

import jnr.constants.platform.Errno;
import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import jnr.posix.junit.EnabledOnJnrOs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DirEntTest {
    private static POSIX posix;

    @TempDir
    Path dir;

    @BeforeAll
    static void setUpClass() {
        posix = POSIXFactory.getPOSIX(new DummyPOSIXHandler(), true);
    }

    private static Map<String, DirEnt> entries(Path path) {
        Pointer stream = posix.opendir(path.toString());
        assertNotNull(stream, "opendir failed with errno " + posix.errno());

        Map<String, DirEnt> entries = new HashMap<>();
        try {
            for (DirEnt entry; (entry = posix.readdir(stream)) != null; ) {
                assertNull(entries.put(entry.d_name(), entry), "read twice: " + entry);
            }
            assertEquals(0, posix.errno(), "readdir failed");
        } finally {
            assertEquals(0, posix.closedir(stream));
        }

        return entries;
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void readsEveryEntryWithItsType() throws IOException {
        Files.createFile(dir.resolve("file"));
        Files.createFile(dir.resolve(".hidden"));
        Files.createDirectory(dir.resolve("directory"));
        Files.createSymbolicLink(dir.resolve("link"), dir.resolve("file"));
        Files.createSymbolicLink(dir.resolve("broken"), dir.resolve("nowhere"));
        Files.createSymbolicLink(dir.resolve("directory-link"), dir.resolve("directory"));
        assertEquals(0, posix.mkfifo(dir.resolve("fifo").toString(), 0600));

        Map<String, DirEnt> entries = entries(dir);

        assertEquals(9, entries.size(), entries.toString());
        assertEquals(DirEnt.DT_DIR, entries.get(".").d_type());
        assertEquals(DirEnt.DT_DIR, entries.get("..").d_type());
        assertEquals(DirEnt.DT_REG, entries.get("file").d_type());
        assertEquals(DirEnt.DT_REG, entries.get(".hidden").d_type());
        assertEquals(DirEnt.DT_DIR, entries.get("directory").d_type());
        assertEquals(DirEnt.DT_LNK, entries.get("link").d_type());
        assertEquals(DirEnt.DT_LNK, entries.get("broken").d_type());
        assertEquals(DirEnt.DT_LNK, entries.get("directory-link").d_type());
        assertEquals(DirEnt.DT_FIFO, entries.get("fifo").d_type());
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void readsTheInodeNumber() throws IOException {
        Path file = Files.createFile(dir.resolve("file"));

        assertEquals(posix.lstat(file.toString()).ino(), entries(dir).get("file").d_ino());
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void readsCharacterDevices() {
        DirEnt devNull = entries(dir.getFileSystem().getPath("/dev")).get("null");

        assertEquals(DirEnt.DT_CHR, devNull.d_type());
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void readsLongAndNonAsciiNames() throws IOException {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 255; i++) longName.append('a');
        Files.createFile(dir.resolve(longName.toString()));
        Files.createFile(dir.resolve("こんにちは.txt"));

        Map<String, DirEnt> entries = entries(dir);

        assertEquals(DirEnt.DT_REG, entries.get(longName.toString()).d_type());
        assertEquals(DirEnt.DT_REG, entries.get("こんにちは.txt").d_type());
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void keepsAnEntryAfterTheNextRead() throws IOException {
        Files.createFile(dir.resolve("first"));
        Files.createFile(dir.resolve("second"));

        Pointer stream = posix.opendir(dir.toString());
        try {
            DirEnt first = posix.readdir(stream);
            String name = first.d_name();
            while (posix.readdir(stream) != null) { }

            assertEquals(name, first.d_name());
        } finally {
            posix.closedir(stream);
        }
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void opendirReportsAMissingDirectory() {
        assertNull(posix.opendir(dir.resolve("missing").toString()));
        assertEquals(Errno.ENOENT.intValue(), posix.errno());
    }

    @Test
    @EnabledOnJnrOs({Platform.OS.LINUX, Platform.OS.DARWIN})
    void opendirReportsAFile() throws IOException {
        Path file = Files.createFile(dir.resolve("file"));

        assertNull(posix.opendir(file.toString()));
        assertEquals(Errno.ENOTDIR.intValue(), posix.errno());
    }

    @Test
    @EnabledOnJnrOs(Platform.OS.WINDOWS)
    void opendirWindows() {
        assertNull(posix.opendir(dir.toString()));
        assertEquals(Errno.ENOSYS.intValue(), posix.errno());
    }
}
