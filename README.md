# Wk Server Manage Web Demo 🚀

[![Docker Image](https://img.shields.io/badge/Docker-wk--server--manage-blue?logo=docker)](https://hub.docker.com/r/wekyjay/wk-server-manage) [![Docker Pulls](https://img.shields.io/docker/pulls/wekyjay/wk-server-manage)](https://hub.docker.com/r/wekyjay/wk-server-manage) [![Docker Version](https://img.shields.io/docker/v/wekyjay/wk-server-manage)](https://hub.docker.com/r/wekyjay/wk-server-manage/tags)

这是一个基于 **Vue3 SPA 前端 + Spring Boot 后端** 的单体应用示例项目，同时集成了 **多阶段 Docker 镜像构建** 和 **Docker Compose 一键启动**。  

👉 这是我用来学习 **构建单页应用（SPA）并进行 Docker 打包与部署** 的练习项目。

## 项目结构 🧩

```text
.
├── backend/          Spring Boot 后端服务
├── frontend/         Vue3 + Vite 单页前端应用
├── Dockerfile        多阶段构建镜像（前端 + 后端打成一个 JAR）
├── docker-compose.yml  应用 + MySQL 一键启动编排
└── docker/           容器运行时挂载的数据和日志目录
```

## 技术栈 ⚙️

- 前端：Vue 3、Vite、SPA 路由
- 后端：Java 17、Spring Boot、Spring Web
- 构建：Maven 多模块、多阶段 Docker 构建
- 部署：Docker、Docker Compose、MySQL 8

后端中 `SpaController` 通过将所有非静态资源路径转发到 `index.html`，实现了典型的 **前端路由型单页应用**。

## 本地开发 💻

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

默认在 `http://localhost:5173` 启动前端开发服务器。

### 后端开发

```bash
cd backend
mvn spring-boot:run
```

默认在 `http://localhost:8080` 启动后端服务。

## Docker 构建与运行 🐳

### 快速测试运行

如果只是想快速体验项目效果，可以直接拉取已构建的镜像：

```bash
docker pull wekyjay/wk-server-manage:v1.0.1
```

### 构建镜像

在项目根目录执行：

```bash
docker build -t wk-server-manage:local .
```

构建完成后会得到一个同时包含前端静态资源和后端服务的轻量级运行镜像。

### 使用 Docker Compose 启动应用和数据库

```bash
docker compose up -d
```

- `wk-app`：运行打包好的后端 + 前端单体应用，暴露端口 `8080`
- `wk-db`：MySQL 8 数据库，数据目录挂载到 `./docker/mysql_data`

启动完成后，可以通过浏览器访问：

- 应用地址：http://localhost:8080

## 学习要点 📚

- 如何使用 Vue3 + Vite 构建 SPA 前端并通过 Spring Boot 提供静态资源
- 如何在 Dockerfile 中通过多阶段构建，将前后端打包为单个可运行 JAR
- 如何使用 Docker Compose 同时编排应用容器与数据库容器
- 如何设计一个方便本地开发和容器化部署的目录结构

如果你也在学习前后端一体化部署和 Docker 打包，希望这个项目对你有所帮助 😊

---

## English Version 🌎

### Overview

This is a demo project of a **single-page application (SPA)** built with a **Vue 3 SPA frontend and a Spring Boot backend**.  
It also demonstrates **multi-stage Docker image builds** and **Docker Compose based one-command startup**.  

👉 I use this project to learn how to **build a SPA and package it into a Docker image for deployment**.

### Project Structure 🧩

```text
.
├── backend/             Spring Boot backend service
├── frontend/            Vue 3 + Vite SPA frontend
├── Dockerfile           Multi-stage build (frontend + backend into a single JAR)
├── docker-compose.yml   App + MySQL orchestration with one command
└── docker/              Mounted data and log directories for containers
```

### Tech Stack ⚙️

- Frontend: Vue 3, Vite, SPA routing
- Backend: Java 17, Spring Boot, Spring Web
- Build: Maven multi-module, multi-stage Docker build
- Deploy: Docker, Docker Compose, MySQL 8

In the backend, `SpaController` forwards all non-static paths to `index.html`, enabling a typical **client-side router based SPA**.

### Local Development 💻

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend dev server runs at `http://localhost:5173` by default.

#### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend service runs at `http://localhost:8080` by default.

### Docker Build & Run 🐳

#### Quick Test Run

If you just want to quickly try the project, you can pull the pre-built image:

```bash
docker pull wekyjay/wk-server-manage:v1.0.1
```

#### Build Image from Source

From the project root:

```bash
docker build -t wk-server-manage:local .
```

This produces a lightweight image that contains both the frontend static assets and the backend service.

#### Run App and Database with Docker Compose

```bash
docker compose up -d
```

- `wk-app`: bundled backend + frontend app, exposes port `8080`
- `wk-db`: MySQL 8 database, data persisted to `./docker/mysql_data`

After startup, you can visit the application at:

- App URL: http://localhost:8080

### Learning Notes 📚

- How to build a Vue 3 + Vite SPA and serve it via Spring Boot
- How to package frontend and backend into a single runnable JAR using a multi-stage Dockerfile
- How to orchestrate application and database containers with Docker Compose
- How to design a directory structure that works well for both local development and containerized deployment

If you are also learning full-stack deployment and Docker packaging, I hope this project can be helpful to you 😊
