# 多阶段构建 - 后端
# 构建阶段
FROM --platform=$BUILDPLATFORM docker.1ms.run/library/maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制 pom.xml 先下载依赖（利用缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用跨平台基础镜像
FROM docker.1ms.run/library/eclipse-temurin:17-jre

WORKDIR /app

# 创建非 root 用户
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# 创建上传目录
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

# 从构建阶段复制 jar
COPY --from=builder /app/target/*.jar app.jar

# 切换到非 root 用户
USER appuser

# 暴露端口
EXPOSE 8080

# JVM 优化参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
