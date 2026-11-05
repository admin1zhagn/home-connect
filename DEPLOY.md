# 免费云平台部署指南（零服务器 / 零域名）

本系统已改造为「前后端一体」：前端打包进后端 jar，单个 `java -jar` 即可在 8080 端口同时提供页面与接口。使用免费云平台（Fly.io / Koyeb / Railway）部署后，平台会分配一个固定子域名（如 `xxx.fly.dev`、`xxx.koyeb.app`），**无需购买服务器，也无需自己申请域名**。

---

## 一、已完成的改造（代码已就绪）

- 前端路由改为 hash 模式（`createWebHashHistory`），刷新任意子页都不会 404，且无需后端做 SPA 回退。
- 后端安全配置：根路径 `/` 与静态资源放行，业务接口 `/api/**` 仍需登录；未登录访问 `/api` 返回 403。
- 新增 `Dockerfile`（多阶段：node 构建前端 → maven 打包 → jre 运行，最终镜像只含一个 jar）。
- `application.yml` 端口改为 `${PORT:8080}`，自动读取云平台注入的 `PORT` 环境变量。
- 数据默认 H2 文件库（容器内路径 `/app/data`），挂持久卷即可长期保存。
- 新增 `build-all.bat` / `build-all.sh`：本地一键打「前后端一体」jar。

---

## 二、第一步：准备 GitHub 仓库

1. 在 GitHub 新建仓库（私有/公开均可，如 `home-connect`）。
2. 项目根目录已配好 `.gitignore`（忽略 `node_modules`、`target`、`data`、前端构建产物），直接提交即可：

```bash
git init
git add .
git commit -m "家庭智联服务 v1.0 前后端一体"
git remote add origin <你的仓库地址>
git push -u origin main
```

---

## 三、第二步：选一个平台部署

### 方案 A：Fly.io（推荐，免费层可用，需信用卡验证）

1. 安装 flyctl：https://fly.io/docs/hands-on/installing/
2. 登录：`fly auth login`
3. 初始化（不立即部署）：
```bash
fly launch --no-deploy --name home-connect
```
4. 编辑生成的 `fly.toml`，确认 `internal_port = 8080`；并增加持久卷挂载（H2 数据落盘，否则重启丢数据）：
```toml
[mounts]
  source = "homeconnect_data"
  destination = "/app/data"
```
5. 创建持久卷：
```bash
fly volumes create homeconnect_data --size 1 --app home-connect
```
6. （强烈建议）设置生产与默认不同的凭据：
```bash
fly secrets set APP_SECURITY_ADMIN_PASSWORD=你的强密码
fly secrets set APP_SECURITY_JWT_SECRET=一段至少32位足够长且随机的字符串
fly secrets set SPRING_H2_CONSOLE_ENABLED=false
```
7. 部署：`fly deploy`
8. 获得域名：`https://home-connect.fly.dev`（手机/任意设备直接访问）。

### 方案 B：Koyeb（无需信用卡）

1. 登录 https://www.koyeb.com，连接 GitHub。
2. 新建 App → 选择仓库 → 部署方式选「Dockerfile」。
3. 设置：
   - Port：`8080`
   - Environment variables（可选）：`PORT=8080`、`APP_SECURITY_ADMIN_PASSWORD=...`、`APP_SECURITY_JWT_SECRET=...`
   - 添加 Store（持久卷），挂载路径 `/app/data`
4. 部署后获得 `https://<app>.koyeb.app` 域名。

### 方案 C：Railway（需绑定支付方式，含一定免费额度）

1. 登录 https://railway.app，连接 GitHub。
2. 新建 Project → 选择仓库 → 自动识别 Dockerfile 构建。
3. Variables 设置 `PORT=8080` 及上述可选变量；添加 Volume 挂载 `/app/data`。
4. 部署后获得 `https://<random>.up.railway.app` 域名。

---

## 四、数据持久化（重要）

- 应用把 H2 文件写在运行目录的 `./data`（容器内即 `/app/data`）。
- **务必挂载持久卷到 `/app/data`**，否则容器重启 / 重新部署后数据会被清空（回到初始演示数据）。
- 重置数据：删卷或清空 `/app/data` 后重启，系统会重新播种。

---

## 五、生产加固（上线前必做）

| 项 | 环境变量 | 说明 |
|---|---|---|
| 管理员密码 | `APP_SECURITY_ADMIN_PASSWORD` | 默认 `admin123`，务必改 |
| JWT 密钥 | `APP_SECURITY_JWT_SECRET` | 至少 32 位随机串（HS256 需 ≥256 bits） |
| 关闭 H2 控制台 | `SPRING_H2_CONSOLE_ENABLED=false` | 生产环境不应暴露 |

默认账号：`admin / admin123`。也可直接改 `backend/src/main/resources/application.yml` 里的 `app.security.*`。

---

## 六、本地运行「前后端一体」jar

- 一键打包：`build-all.bat`（Windows）/ `build-all.sh`（Mac/Linux）→ 产物 `backend/target/home-connect-service-1.0.0.jar`
- 运行：`java -jar backend/target/home-connect-service-1.0.0.jar`
- 浏览器打开 `http://localhost:8080`，登录后即为完整系统（含移动端适配）。
- 注意：本地若同时在跑 `start.bat`（分端口 8080+5173）会冲突，二选一。

---

## 七、想换成 MySQL（可选）

默认 H2 已满足演示与中小规模。若需更强并发与备份，参考原 `README.md` 激活 `mysql` profile（在云平台上改用其托管的 MySQL/Postgres，并修改 `application-mysql.yml` 连接配置即可）。
