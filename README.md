# 家庭智联服务系统 V1.0

个人装维 / 通信服务人员的**客户管理 + 家庭套餐分析**工具。
技术栈：Spring Boot 3（Java 17）+ MyBatis-Plus + MySQL 8 / H2，Vue 3 + TypeScript + Element Plus + Pinia + ECharts。

> 默认以**嵌入式 H2 数据库**运行，零外部依赖，`java -jar` 即可启动。生产可用 MySQL 8（见下文切换）。

---

## 一、环境要求

- JDK 17 及以上（已用 JDK 21 验证）
- Node.js 18 及以上（前端）
- Maven 3.9+（可选；仓库已内置 Maven Wrapper，首次运行会自动下载）
- MySQL 8（**仅**在切换到 MySQL 模式时需要，默认 H2 不需要）

---

## 二、快速开始（默认 H2，零配置）

### 1) 后端

方式 A — 直接用打包好的 jar（推荐）：

```bash
cd backend
# 已构建好 target/home-connect-service-1.0.0.jar，直接运行：
java -jar target/home-connect-service-1.0.0.jar
```

方式 B — 用 Maven 构建并运行：

```bash
cd backend
./mvnw clean package -DskipTests        # 首次会自动下载 Maven 发行包（已从华为云镜像加速）
java -jar target/home-connect-service-1.0.0.jar
```

- 默认端口 **8080**。若该端口被占用，用 `java -jar target/...jar --server.port=9090` 指定其他端口。
- 启动后访问：
  - 接口基址：`http://localhost:8080/api`
  - H2 控制台：`http://localhost:8080/h2-console`（JDBC URL：`jdbc:h2:file:./data/homeconnect`）
- 默认账号：**admin / admin123**（首次启动自动建表并播种，生产环境务必修改 `backend/src/main/resources/application.yml` 中的 `app.security.*`）。

### 2) 前端（开发模式）

```bash
cd frontend
npm install
npm run dev          # 默认 http://localhost:5173，已配置 /api 代理到后端 8080
```

前端生产构建：`npm run build`（输出到 `frontend/dist`，可由 Nginx 等托管）。

---

## 三、切换到 MySQL 8（生产）

1. 启动 MySQL 8，并执行 `backend/docker-compose.yml`（已含 MySQL 服务）或自建实例。
2. 修改后端启动参数，激活 mysql profile：

```bash
java -jar target/home-connect-service-1.0.0.jar --spring.profiles.active=mysql
```

或在 `application.yml` 把 `spring.profiles.active` 改为 `mysql`。
相关连接配置见 `backend/src/main/resources/application-mysql.yml`（默认账号 `homeconnect` / `homeconnect123`，库名 `home_connect`）。

---

## 四、核心功能与验收

| 模块 | 说明 |
|---|---|
| 客户管理 | 增删改查、搜索、状态/来源筛选、逻辑删除 |
| 家庭成员 | 一个客户多个成员，记录运营商/套餐/流量需求 |
| 当前套餐 | 多套餐录入，区分精确/估算，计算家庭当前月支出 |
| 运营商套餐库 | 维护可推荐套餐（标准/优惠/结束后资费、一次性费用） |
| **家庭套餐分析** | 组合套餐 → 分阶段测算（优惠期/结束后/一次性/合约期对比），结论裁决 SAVING/NO_SAVING/VERIFY |
| 推荐历史 | 每次分析**价格快照**落库，套餐库改价不影响历史（验收第 12 条） |
| 推荐反馈 | 记录客户反馈与成交状态 |
| 服务记录 | 各类上门服务过程记录 |
| 待跟进 | 今日 / 逾期 / 全部跟进列表 |
| 首页看板 | 客户数、推荐次数、成功率、转化率等 + ECharts 图表 |

---

## 五、目录结构

```
家庭智联服务/
├── backend/                 Spring Boot 3 工程
│   ├── src/main/java/com/homeconnect/   实体/Mapper/Service/Controller/安全/配置
│   ├── src/main/resources/              schema-h2.sql / schema-mysql.sql / application*.yml
│   ├── docker-compose.yml               MySQL 8 一键起
│   └── target/*.jar                     已打包可执行 jar
└── frontend/                Vue3 + TS 工程（src/views 下 13 个页面）
```

---

## 六、安全说明

- 登录鉴权：Spring Security 无状态 + JWT（Bearer Token）+ BCrypt 密码哈希。
- 个人客户数据仅本人授权账号访问；联系方式最小化收集。
- 不保存身份证、银行卡、账户密码；不导入未经授权的公司客户资料。
- 关键操作建议保留日志，数据库定期备份（MySQL 模式下）。
