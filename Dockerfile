# syntax=docker/dockerfile:1

# ==========================================
# Giai đoạn 1: Build Frontend (Next.js Client)
# ==========================================
FROM node:22-alpine AS frontend-builder
WORKDIR /app/client

ARG CLIENT_REPO=https://github.com/ducbantanh/lms_client.git
ARG CLIENT_BRANCH=main

ARG CACHEBUST=1

RUN apk add --no-cache git
RUN git clone --depth 1 -b ${CLIENT_BRANCH} ${CLIENT_REPO} .
RUN npm ci || npm install
RUN npm run build

# ==========================================
# Giai đoạn 2: Build Backend (Spring Boot Server)
# ==========================================
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-builder
WORKDIR /app/server

# Tận dụng cache của Docker cho dependencies Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Chép toàn bộ bản xuất tĩnh của Next.js vào classpath tĩnh của Spring Boot
COPY --from=frontend-builder /app/client/out/ ./src/main/resources/static/

RUN mvn clean package -DskipTests

# ==========================================
# Giai đoạn 3: Runtime Image
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Cài đặt các trình biên dịch/thông dịch để máy chấm bài (Judge) hoạt động (sandbox=local)
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    python3 \
    coreutils \
    && rm -rf /var/lib/apt/lists/*

# Tạo các thư mục dữ liệu lưu trữ
RUN mkdir -p data/files data/judge

# Chép file jar đã build
COPY --from=backend-builder /app/server/target/lms-server-*.jar app.jar

# Cấu hình biến môi trường mặc định (Render tự động ghi đè PORT)
ENV PORT=8080
ENV APP_PORT=8080
ENV APP_PROFILE=prod
ENV APP_JUDGE_SANDBOX=local
ENV APP_STORAGE_DIR=data/files
ENV APP_JUDGE_WORK_DIR=data/judge

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
