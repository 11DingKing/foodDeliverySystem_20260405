# 多阶段构建 - 前端
# 构建阶段
FROM --platform=$BUILDPLATFORM docker.1ms.run/library/node:20-alpine AS builder

WORKDIR /app

# 复制 package.json 先安装依赖（利用缓存）
COPY package.json package-lock.json* pnpm-lock.yaml* ./

# 安装依赖
RUN if [ -f pnpm-lock.yaml ]; then \
      npm install -g pnpm && pnpm install --frozen-lockfile; \
    elif [ -f package-lock.json ]; then \
      npm ci; \
    else \
      npm install; \
    fi

# 复制源码并构建
COPY . .
RUN npm run build

# 运行阶段 - 使用跨平台 nginx 镜像
FROM docker.1ms.run/library/nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]
