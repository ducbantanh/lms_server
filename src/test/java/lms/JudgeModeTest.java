package lms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import lms.code.JudgeModeGuard;
import lms.config.AppProperties;

class JudgeModeTest {

    @Test
    void argumentWinsOverSystemPropertyAndEnvironment() {
        assertEquals("worker", LmsServerApplication.judgeMode(new String[] { "--app.judge.mode=worker" }, "external",
                "embedded"));
        assertEquals("external", LmsServerApplication.judgeMode(new String[] { "--server.port=9000" }, "external",
                "worker"));
        assertEquals("worker", LmsServerApplication.judgeMode(new String[0], null, "worker"));
        assertNull(LmsServerApplication.judgeMode(new String[0], null, null));
    }

    private static AppProperties withMode(String mode) {
        AppProperties.Judge judge = new AppProperties.Judge(mode, "local", 2, "data/judge", "docker", "lms-judge:1",
                "gcc", "g++", "javac", "java", null);
        return new AppProperties(null, null, null, null, null, null, judge, null, null, null);
    }

    @Test
    void webServerRejectsWorkerModeFromConfigFiles() {
        new JudgeModeGuard(withMode("embedded"));
        new JudgeModeGuard(withMode("external"));
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new JudgeModeGuard(withMode("worker")));
        assertEquals(true, e.getMessage().contains("APP_JUDGE_MODE=worker"));
    }
}
