package lms.code;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lms.config.AppProperties;

/**
 * Máy chấm chạy nền: mỗi nửa giây lấy các lần chạy/nộp đang chờ trong DB (cũ trước) và chấm song song tối đa
 * app.judge.workers bài. Hàng đợi nằm trong DB nên server khởi động lại không mất bài: bài đang chấm dở được đưa
 * về hàng đợi khi khởi động.
 */
@Component
public class CodeJudgeWorker {

    private static final Logger log = LoggerFactory.getLogger(CodeJudgeWorker.class);

    private final CodeService service;
    private final CodeJudge judge;
    private final ExecutorService pool;
    private final Semaphore slots;
    private volatile boolean ready;

    public CodeJudgeWorker(CodeService service, AppProperties props) {
        AppProperties.Judge cfg = props.judge();
        this.service = service;
        this.judge = new CodeJudge(sandbox(cfg), Path.of(cfg.workDir()));
        int workers = Math.max(1, cfg.workers());
        AtomicInteger n = new AtomicInteger();
        this.pool = Executors.newFixedThreadPool(workers, r -> {
            Thread t = new Thread(r, "code-judge-" + n.incrementAndGet());
            t.setDaemon(true);
            return t;
        });
        this.slots = new Semaphore(workers);
    }

    static Sandbox sandbox(AppProperties.Judge cfg) {
        if ("docker".equalsIgnoreCase(cfg.sandbox())) {
            log.info("Code judge: docker sandbox, image {}", cfg.dockerImage());
            return new DockerSandbox(cfg.docker(), cfg.dockerImage());
        }
        log.warn("Code judge: LOCAL sandbox - student programs run unisolated on this machine."
                + " Set app.judge.sandbox=docker for real deployments.");
        String python = cfg.python() == null || cfg.python().isBlank()
                ? (LocalSandbox.WINDOWS ? "python" : "python3") : cfg.python();
        return new LocalSandbox(new Sandbox.Toolchain(cfg.gcc(), cfg.gpp(), cfg.javac(), cfg.java(), python));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        int n = service.requeueInterrupted();
        if (n > 0) {
            log.info("Requeued {} code attempts interrupted by a restart", n);
        }
        ready = true;
    }

    @Scheduled(fixedDelay = 500, initialDelay = 2_000)
    public void poll() {
        if (!ready || slots.availablePermits() == 0) {
            return;
        }
        try {
            for (Long id : service.queued(slots.availablePermits())) {
                if (!slots.tryAcquire()) {
                    return;
                }
                CodeService.Claimed claimed;
                try {
                    claimed = service.claim(id).orElse(null);
                } catch (RuntimeException e) {
                    slots.release();
                    throw e;
                }
                if (claimed == null) {
                    slots.release();
                    continue;
                }
                pool.execute(() -> {
                    try {
                        run(claimed);
                    } finally {
                        slots.release();
                    }
                });
            }
        } catch (RuntimeException e) {
            log.warn("Code judge poll failed: {}", e.getMessage());
        }
    }

    private void run(CodeService.Claimed claimed) {
        try {
            CodeJudge.Result r = judge.judge(claimed.task());
            service.complete(claimed.attemptId(), claimed.token(), r);
        } catch (InterruptedException e) {
            // Server đang dừng: bài ở trạng thái đang chấm, lần khởi động sau đưa về hàng đợi
            Thread.currentThread().interrupt();
        } catch (RuntimeException e) {
            log.error("Judging attempt {} failed", claimed.attemptId(), e);
            // Không để bài kẹt ở "đang chấm": báo lỗi máy chấm, giảng viên chấm lại được
            try {
                service.complete(claimed.attemptId(), claimed.token(), new CodeJudge.Result(CodeJudge.SE,
                        "Máy chấm gặp lỗi.", List.of(), 0, claimed.task().tests().size()));
            } catch (RuntimeException ignored) {
                // DB lỗi: lần khởi động sau đưa bài về hàng đợi
            }
        }
    }

    @PreDestroy
    void stop() throws InterruptedException {
        ready = false;
        pool.shutdownNow();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
