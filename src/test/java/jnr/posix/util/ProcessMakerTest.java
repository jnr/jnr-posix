package jnr.posix.util;

import jnr.posix.DummyPOSIXHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProcessMakerTest {
    @TempDir
    File tempDir;

    // Run as the child process: copy one line of stdin to stdout, then write "err" to stderr.
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = in.readLine();
        System.out.println(line == null ? "out" : line);
        System.out.flush();
        System.err.println("err");
    }

    private static ProcessMaker child() {
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        ProcessMaker maker = new JavaLangProcessMaker(java, "-cp", classpath, ProcessMakerTest.class.getName());

        // The JVM writes a "Picked up ..." note to stderr when one of these is set
        maker.environment().remove("JDK_JAVA_OPTIONS");
        maker.environment().remove("JAVA_TOOL_OPTIONS");
        maker.environment().remove("_JAVA_OPTIONS");

        return maker;
    }

    private static List<String> lines(File file) throws IOException {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    @Test
    void redirectsAreAPipeByDefault() {
        ProcessMaker maker = child();

        assertSame(ProcessMaker.Redirect.PIPE, maker.redirectInput());
        assertSame(ProcessMaker.Redirect.PIPE, maker.redirectOutput());
        assertSame(ProcessMaker.Redirect.PIPE, maker.redirectError());
        assertEquals(false, maker.redirectErrorStream());
    }

    @Test
    void redirectsOutputAndErrorToFiles() throws Exception {
        File out = new File(tempDir, "out");
        File err = new File(tempDir, "err");
        ProcessMaker maker = child()
                .redirectInput(ProcessMaker.Redirect.from(writeInput("in")))
                .redirectOutput(ProcessMaker.Redirect.to(out))
                .redirectError(err);

        assertEquals(0, maker.start().waitFor());
        assertEquals(Arrays.asList("in"), lines(out));
        assertEquals(Arrays.asList("err"), lines(err));
        assertEquals(out, maker.redirectOutput().file());
        assertEquals(err, maker.redirectError().file());
    }

    @Test
    void appendsOutputToAFile() throws Exception {
        File out = new File(tempDir, "out");
        Files.write(out.toPath(), Arrays.asList("before"), StandardCharsets.UTF_8);
        ProcessMaker maker = child()
                .redirectInput(writeInput("in"))
                .redirectOutput(ProcessMaker.Redirect.appendTo(out));

        assertEquals(0, maker.start().waitFor());
        assertEquals(Arrays.asList("before", "in"), lines(out));
    }

    @Test
    void mergesErrorIntoOutput() throws Exception {
        File out = new File(tempDir, "out");
        ProcessMaker maker = child()
                .redirectInput(writeInput("in"))
                .redirectOutput(out)
                .redirectErrorStream(true);

        assertEquals(0, maker.start().waitFor());
        assertEquals(true, maker.redirectErrorStream());
        assertEquals(Arrays.asList("in", "err"), lines(out));
    }

    private File writeInput(String line) throws IOException {
        File in = new File(tempDir, "in");
        Files.write(in.toPath(), Arrays.asList(line), StandardCharsets.UTF_8);
        return in;
    }
}
