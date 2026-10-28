# Image sandbox cho máy chấm bài lập trình (app.judge.sandbox=docker).
# Dựng: docker build -t lms-judge:1 judge
# Server chạy mỗi lần chấm một container từ image này (không mạng, giới hạn RAM/CPU/tiến trình),
# nên image chỉ cần trình biên dịch và coreutils (lệnh timeout).
FROM debian:bookworm-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends gcc g++ libc6-dev openjdk-17-jdk-headless python3 coreutils \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /box
