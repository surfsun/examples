# syntax=docker/dockerfile:1
# -------------------------------------------------------------------
# 第一阶段：构建阶段 (Builder)
# -------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# 利用 Docker Cache Mount 缓存 Maven 本地仓库
# 这样即使 Docker 镜像层缓存失效，已经下载过的 jar 包也不会被清理
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# 再复制源码进行编译，同样使用缓存挂载
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# -------------------------------------------------------------------
# 第二阶段：运行阶段 (Runner)
# -------------------------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
# 从 builder 阶段复制构建好的 jar
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 9000
ENV APP_ID=""
ENV DASHSCOPE_API_KEY=""

ENTRYPOINT ["java", "-jar", "app.jar"]
