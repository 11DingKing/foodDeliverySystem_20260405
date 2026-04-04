# MySQL with init script
FROM docker.1ms.run/library/mysql:8.0

# 设置环境变量确保 UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# 复制初始化脚本并确保编码正确
COPY --chmod=644 init.sql /docker-entrypoint-initdb.d/init.sql

# 确保脚本以 UTF-8 执行
RUN sed -i '1i\\set names utf8mb4;' /docker-entrypoint-initdb.d/init.sql || true
