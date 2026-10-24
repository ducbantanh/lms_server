package lms.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lms.code.CodeJudge.TestCase;

/**
 * Chấm thật bằng LocalSandbox với trình biên dịch trên máy chạy test; thiếu trình biên dịch nào thì bỏ qua
 * test của ngôn ngữ đó.
 */
class CodeJudgeTest {

    private static final String PYTHON = LocalSandbox.WINDOWS ? "python" : "python3";

    @TempDir
    Path work;

    private static final List<TestCase> SUM = List.of(
            TestCase.of("1 2\n", "3\n", true),
            TestCase.of("10 -4\n", "6\n", false),
            TestCase.of("2000000000 2000000000\n", "4000000000\n", false));

    private CodeJudge judge() {
        return new CodeJudge(new LocalSandbox(new Sandbox.Toolchain("gcc", "g++", "javac", "java", PYTHON)), work);
    }

    private static boolean available(String... command) {
        try {
            Process p = new ProcessBuilder(command).redirectErrorStream(true).start();
            p.getInputStream().readAllBytes();
            return p.waitFor(30, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private CodeJudge.Result run(CodeLanguage lang, String source, List<TestCase> tests) throws Exception {
        return judge().judge(new CodeJudge.Task(lang, source, tests, 2000, 256));
    }

    @Test
    void pythonAcceptedAndWrongAnswerWithPartialScore() throws Exception {
        assumeTrue(available(PYTHON, "--version"));
        CodeJudge.Result ok = run(CodeLanguage.PYTHON, "a, b = map(int, input().split())\nprint(a + b)\n", SUM);
        assertEquals(CodeJudge.AC, ok.verdict());
        assertEquals(3, ok.passed());
        assertEquals(3, ok.total());

        // Sai test cuối
        CodeJudge.Result wrong = run(CodeLanguage.PYTHON,
                "a, b = map(int, input().split())\nprint(-1 if a + b > 1000 else a + b)\n", SUM);
        assertEquals(CodeJudge.WA, wrong.verdict());
        assertEquals(2, wrong.passed());
        assertEquals(3, wrong.total());
        assertEquals("-1", wrong.tests().get(2).output());
        // Test ẩn làm đúng thì không giữ output; test mẫu thì luôn giữ
        assertEquals(null, wrong.tests().get(1).output());
        assertEquals("3", wrong.tests().get(0).output());
    }

    @Test
    void pythonSyntaxErrorIsCompileErrorAndCrashIsRuntimeError() throws Exception {
        assumeTrue(available(PYTHON, "--version"));
        CodeJudge.Result ce = run(CodeLanguage.PYTHON, "print(1\n", SUM);
        assertEquals(CodeJudge.CE, ce.verdict());
        assertNotNull(ce.compileOutput());
        assertEquals(0, ce.tests().size());

        CodeJudge.Result re = run(CodeLanguage.PYTHON, "raise SystemExit(3)\n", SUM.subList(0, 1));
        assertEquals(CodeJudge.RE, re.verdict());
    }

    @Test
    void infiniteLoopIsTimeLimitExceeded() throws Exception {
        assumeTrue(available(PYTHON, "--version"));
        CodeJudge.Result r = judge().judge(new CodeJudge.Task(CodeLanguage.PYTHON, "while True:\n    pass\n",
                SUM.subList(0, 1), 200, 256));
        assertEquals(CodeJudge.TLE, r.verdict());
    }

    @Test
    void cAndCppCompileAndRun() throws Exception {
        assumeTrue(available("gcc", "--version"));
        CodeJudge.Result c = run(CodeLanguage.C,
                "#include <stdio.h>\nint main(){long long a,b;scanf(\"%lld %lld\",&a,&b);printf(\"%lld\\n\",a+b);}\n",
                SUM);
        assertEquals(CodeJudge.AC, c.verdict(), c.compileOutput());

        // g++ có trên máy nhưng không liên kết được (cài đặt MSYS2 lỗi) thì bỏ qua phần C++
        CodeJudge.Result hello = run(CodeLanguage.CPP, "#include <iostream>\nint main(){std::cout<<3;}\n",
                SUM.subList(0, 1));
        assumeTrue(CodeJudge.AC.equals(hello.verdict()), "g++ không dùng được: " + hello.compileOutput());
        CodeJudge.Result ce = run(CodeLanguage.CPP, "int main( { }\n", SUM);
        assertEquals(CodeJudge.CE, ce.verdict());
        CodeJudge.Result cpp = run(CodeLanguage.CPP,
                "#include <iostream>\nint main(){int a,b;std::cin>>a>>b;std::cout<<a+b<<'\\n';}\n", SUM);
        // int 32 bit tràn ở test cuối
        assertEquals(CodeJudge.WA, cpp.verdict(), cpp.compileOutput());
        assertEquals(2, cpp.passed());
    }

    @Test
    void javaNeedsClassMain() throws Exception {
        assumeTrue(available("javac", "-version"));
        CodeJudge.Result r = run(CodeLanguage.JAVA, """
                import java.util.Scanner;
                public class Main {
                    public static void main(String[] args) {
                        Scanner in = new Scanner(System.in);
                        long a = in.nextLong(), b = in.nextLong();
                        System.out.println(a + b);
                    }
                }
                """, SUM);
        assertEquals(CodeJudge.AC, r.verdict(), r.compileOutput());
    }

    @Test
    void missingCompilerIsSystemError() throws Exception {
        CodeJudge broken = new CodeJudge(new LocalSandbox(
                new Sandbox.Toolchain("no-such-gcc-xyz", "g++", "javac", "java", PYTHON)), work);
        CodeJudge.Result r = broken.judge(new CodeJudge.Task(CodeLanguage.C, "int main(){}", SUM, 1000, 64));
        assertEquals(CodeJudge.SE, r.verdict());
    }
}
