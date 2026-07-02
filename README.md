# TravelShare 旅游分享平台

> 前后端分离的旅游社交平台 — 大二暑期全栈项目

## 技术栈

| 层级 | 技术 |
|------|------|
| **后端** | Spring Boot 3.5 + Java 21 + MyBatis-Plus + MySQL + JWT + BCrypt |
| **前端** | Vue 3 + Element Plus + Vite + Axios + Vue Router |
| **工具** | Maven, Git |

## 功能模块

- 👤 用户注册/登录（JWT 认证 + BCrypt 密码加密）
- 📝 旅游攻略分享（发布/浏览/评论）
- 📢 旅游公告
- 🗺 路线规划
- 🤝 找搭子（旅伴匹配，房间机制）
- 💰 账单分账（群组账单、AA 结算）
- 🏙 城市景点管理
- 🛠 管理后台（用户管理）

## 项目结构

```
travelshare/              # Spring Boot 后端
  src/main/java/com/travelshare/
    controller/            # REST API 控制器（8个模块）
    service/               # 业务逻辑层
    mapper/                # MyBatis-Plus DAO 层
    entity/                # 数据库实体
    dto/                   # 数据传输对象
    config/                # JWT 拦截器、CORS 配置
    common/                # 统一响应体、全局异常处理

travelshare-web/          # Vue 3 前端
  src/
    views/                 # 8个页面组件
    api/                   # Axios 请求封装
    router/                # 路由 + Token 守卫
```

## 本地运行

**后端：**
```bash
cd travelshare
./mvnw spring-boot:run
```

**前端：**
```bash
cd travelshare-web
npm install
npm run dev
```

> 后端默认端口 8080，前端 5173，通过 Vite 代理转发 API 请求。

## 作者

陈家智 | 广东技术师范大学 软件工程 2024级 | [GitHub](https://github.com/chenjz111)
