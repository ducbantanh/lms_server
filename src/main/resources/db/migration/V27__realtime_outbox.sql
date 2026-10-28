-- V27: máy chấm chạy process riêng (app.judge.mode=worker) không giữ WebSocket. Sự kiện realtime của nó (code.judged)
-- ghi vào đây trong cùng transaction với kết quả chấm; server web (app.judge.mode=external) đọc theo khung thời gian
-- gần nhất rồi gửi qua WebSocket (RealtimeOutboxPoller). Dòng cũ hơn 10 phút bị xóa.

CREATE TABLE realtime_outbox (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    type        VARCHAR(64)     NOT NULL,
    payload     TEXT            NOT NULL,          -- data của sự kiện (JSON)
    created_at  DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    KEY idx_realtime_outbox_created (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
