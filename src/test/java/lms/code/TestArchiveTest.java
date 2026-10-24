package lms.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

class TestArchiveTest {

    private static final TestArchive.Limits LIMITS = new TestArchive.Limits(200, 1000, 5000);

    private static byte[] zip(Map<String, String> files) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream z = new ZipOutputStream(out)) {
            for (Map.Entry<String, String> f : files.entrySet()) {
                z.putNextEntry(new ZipEntry(f.getKey()));
                if (!f.getKey().endsWith("/")) {
                    z.write(f.getValue().getBytes(StandardCharsets.UTF_8));
                }
                z.closeEntry();
            }
        }
        return out.toByteArray();
    }

    private static Map<String, String> files(String... pairs) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            m.put(pairs[i], pairs[i + 1]);
        }
        return m;
    }

    private static List<String> names(TestArchive.Result r) {
        return r.tests().stream().map(TestArchive.Test::name).toList();
    }

    @Test
    void pairsInOutByNameInNaturalOrderAndStripsCommonFolder() throws IOException {
        TestArchive.Result r = TestArchive.read(zip(files(
                "bo-test/10.in", "10 10", "bo-test/10.out", "20",
                "bo-test/2.in", "1 1", "bo-test/2.out", "2",
                "bo-test/1.inp", "﻿0 0", "bo-test/1.ans", "0",
                "bo-test/sol.cpp", "int main(){}",
                "__MACOSX/bo-test/._1.in", "x")), LIMITS);
        assertTrue(r.ok(), r.problems().toString());
        assertEquals(List.of("1", "2", "10"), names(r));
        // BOM bị bỏ
        assertEquals("0 0", r.tests().get(0).input());
        assertEquals("20", r.tests().get(2).expected());
        assertEquals(1, r.ignored());
    }

    @Test
    void txtPrefixAndThemisFolders() throws IOException {
        TestArchive.Result txt = TestArchive.read(zip(files(
                "input01.txt", "a", "output01.txt", "A", "input02.txt", "b", "output02.txt", "B")), LIMITS);
        assertEquals(List.of("01", "02"), names(txt));

        TestArchive.Result themis = TestArchive.read(zip(files(
                "Test01/BAI.INP", "1", "Test01/BAI.OUT", "1",
                "Test02/BAI.INP", "2", "Test02/BAI.OUT", "4")), LIMITS);
        assertTrue(themis.ok());
        assertEquals(List.of("test01/bai", "test02/bai"), names(themis));
        assertEquals("4", themis.tests().get(1).expected());
    }

    @Test
    void reportsMissingFilesAndLimits() throws IOException {
        TestArchive.Result r = TestArchive.read(zip(files("1.in", "1", "1.out", "1", "2.in", "2", "3.out", "3")),
                LIMITS);
        assertFalse(r.ok());
        assertEquals(1, r.tests().size());
        assertEquals(List.of(new TestArchive.Problem("2", TestArchive.MISSING_OUTPUT),
                new TestArchive.Problem("3", TestArchive.MISSING_INPUT)), r.problems());

        TestArchive.Result big = TestArchive.read(zip(files("1.in", "x".repeat(1001), "1.out", "1")), LIMITS);
        assertEquals(TestArchive.FILE_TOO_LARGE, big.problems().get(0).reason());

        Map<String, String> many = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            many.put(i + ".in", "x".repeat(900));
            many.put(i + ".out", "1");
        }
        assertEquals(TestArchive.TOO_LARGE, TestArchive.read(zip(many), LIMITS).problems().get(0).reason());
        assertEquals(TestArchive.TOO_MANY,
                TestArchive.read(zip(many), new TestArchive.Limits(3, 1000, 100_000)).problems().get(0).reason());
    }

    @Test
    void notAZipOrNoTests() throws IOException {
        assertEquals(TestArchive.NOT_ZIP,
                TestArchive.read("hello".getBytes(StandardCharsets.UTF_8), LIMITS).problems().get(0).reason());
        assertEquals(TestArchive.EMPTY,
                TestArchive.read(zip(files("readme.md", "x")), LIMITS).problems().get(0).reason());
    }
}
