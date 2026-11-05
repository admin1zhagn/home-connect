# syntax=docker/dockerfile:1

# ---------- Stage 1: 构建前端 ----------
FROM node:18-alpine AS frontend
WORKDIR /web
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# ---------- Stage 2: 构建后端（把前端产物打进 jar，前后端一体） ----------
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY backend/ .
# 前端构建产物复制进后端静态资源目录，maven 打包后随 jar 一起发布
COPY --from=frontend /web/dist ./src/main/resources/static
RUN mvn -B clean package -DskipTests

# ---------- Stage 3: 运行镜像 ----------
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar

# 默认 H2 模式；云平台上通过环境变量 PORT 注入端口（application.yml 已配 ${PORT:8080}）
ENV SPRING_PROFILES_ACTIVE=h2
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
