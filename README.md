# 外卖点餐系统

一个完整的外卖点餐系统，支持用户、商家、骑手三种角色。

## How to Run

### Docker 一键启动（推荐）

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

启动后访问：
- 前端: http://localhost:8081
- 后端 API: http://localhost:8080
- 静态资源: http://localhost:9000

### 手动启动

**1. 数据库初始化**

```bash
mysql -u root -p < sql/init.sql
```

**2. 后端启动**

```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库配置
mvn spring-boot:run
```

后端运行在 http://localhost:8080

**3. 静态资源服务启动**

```bash
cd static-server
npm install
npm start
```

静态资源服务运行在 http://localhost:9000

**4. 前端启动**

```bash
cd frontend-user
npm install
npm run dev
```

前端运行在 http://localhost:5173

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| mysql | 3307 | MySQL 数据库 |
| backend | 8080 | SpringBoot 后端 API |
| frontend-user | 8081 (Docker) / 5173 (Dev) | Vue 前端 |
| static-server | 9000 | 静态资源服务（数据库模拟数据图片） |

> **说明**：static-server 用于提供数据库初始化脚本中店铺和菜品的模拟图片，用户上传的图片（头像、菜品图等）仍由后端 `/uploads` 接口处理。

## 测试账号

系统初始化后会创建以下测试账号（密码均为 `123456`）：

| 角色 | 用户名 | 昵称 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 管理员 | 系统管理 |
| 普通用户 | user1 | 张三 | 可浏览店铺、下单 |
| 普通用户 | user2 | 李四 | 可浏览店铺、下单 |
| 商家 | merchant1 | 王老板 | 老王黄焖鸡店主 |
| 商家 | merchant2 | 李老板 | 川味小厨店主 |
| 商家 | merchant3 | 赵老板 | 日式料理屋店主 |
| 骑手 | rider1 | 骑手小王 | 可接单配送 |
| 骑手 | rider2 | 骑手小李 | 可接单配送 |

## 题目内容

帮我从零生成一个SpringBoot+Vue的外卖点餐系统，组件库可以使用antd：1.拥有登录注册功能；2.要拥有外卖平台的基础功能；3.用户界面可以修改头像密码之类的个人信息，还可以管理自己的送餐地址；4.用户界面还可以申请成为商家，管理自己的店铺，将自己的店铺进行上架以及菜品的增删改查，以及店铺地址的编写，商家还可以看到用户是否购买了自己的菜品；5.用户界面还可以申请成为骑手，当其他用户下订单时，骑手应该有另一个界面查看所有用户购买的订单，并进行是否接取功能，以及是否送达功能；6.所有订单的状态应该有，待支付，待接单，备餐中，待取餐，配送中，已送达，已取消；

## 技术栈

**后端**
- SpringBoot 3.2
- MyBatis-Plus
- MySQL 8.0
- JWT 认证
- Spring Security

**前端**
- Vue 3 + Composition API
- Vite
- Ant Design Vue 4.x
- Pinia
- Vue Router 4
- Axios

## 功能特性

### 用户端
- 注册登录
- 浏览店铺和菜品
- 购物车管理
- 下单支付
- 订单管理
- 地址管理
- 个人信息管理

### 商家端
- 申请开店
- 店铺信息管理
- 菜品分类管理
- 菜品增删改查
- 订单接单和备餐

### 骑手端
- 申请成为骑手
- 查看待配送订单
- 接单配送
- 确认送达

### 订单状态流转
待支付 → 待接单 → 备餐中 → 待取餐 → 配送中 → 已送达/已取消

## 项目结构

```
├── backend/                    # 后端项目
│   ├── src/main/java/com/fooddelivery/
│   │   ├── annotation/         # 自定义注解
│   │   ├── aspect/             # AOP切面
│   │   ├── common/             # 公共类
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── exception/          # 异常处理
│   │   ├── mapper/             # MyBatis Mapper
│   │   ├── security/           # 安全相关
│   │   ├── service/            # 服务层
│   │   └── util/               # 工具类
│   └── src/main/resources/
│       └── application.yml     # 配置文件
├── frontend-user/              # 前端项目
│   ├── src/
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia状态管理
│   │   ├── styles/             # 全局样式
│   │   ├── utils/              # 工具函数
│   │   └── views/              # 页面组件
│   │       ├── merchant/       # 商家页面
│   │       └── rider/          # 骑手页面
│   └── vite.config.js          # Vite配置
├── static-server/              # 静态资源服务（模拟数据图片）
│   ├── server.js               # Express 服务
│   ├── package.json            # 依赖配置
│   ├── Dockerfile              # Docker 构建
│   └── images/                 # 图片资源
│       ├── shops/              # 店铺图片
│       └── dishes/             # 菜品图片
├── sql/
│   └── init.sql                # 数据库初始化脚本
├── docker-compose.yml          # Docker 编排配置
├── .gitignore
└── README.md
```

## API 接口

### 认证
- POST `/api/auth/login` - 登录
- POST `/api/auth/register` - 注册

### 用户
- GET `/api/user/info` - 获取当前用户信息
- PUT `/api/user/profile` - 更新个人信息
- PUT `/api/user/password` - 修改密码
- POST `/api/user/avatar` - 上传头像

### 地址
- GET `/api/addresses` - 地址列表
- POST `/api/addresses` - 添加地址
- PUT `/api/addresses/{id}` - 更新地址
- DELETE `/api/addresses/{id}` - 删除地址

### 店铺
- GET `/api/shops` - 店铺列表
- GET `/api/shops/{id}` - 店铺详情
- GET `/api/shops/{id}/dishes` - 店铺菜品
- POST `/api/shops/apply` - 申请开店

### 订单
- POST `/api/orders` - 创建订单
- GET `/api/orders` - 我的订单
- POST `/api/orders/{id}/pay` - 支付订单
- POST `/api/orders/{id}/cancel` - 取消订单

## Docker 部署

### 跨平台构建

Dockerfile 使用多阶段构建，支持 ARM64 和 AMD64 架构：

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t food-delivery-backend ./backend
docker buildx build --platform linux/amd64,linux/arm64 -t food-delivery-frontend ./frontend-user
```

### 环境变量

后端支持以下环境变量覆盖配置：
- `SPRING_DATASOURCE_URL` - 数据库连接地址
- `SPRING_DATASOURCE_USERNAME` - 数据库用户名
- `SPRING_DATASOURCE_PASSWORD` - 数据库密码

## License

MIT
