### Overview

[English](./README_en.md) | [简体中文](./README.md)

[![Docker Image](https://img.shields.io/badge/Docker-wk--server--manage-blue?logo=docker)](https://hub.docker.com/r/wekyjay/wk-server-manage) [![Docker Pulls](https://img.shields.io/docker/pulls/wekyjay/wk-server-manage)](https://hub.docker.com/r/wekyjay/wk-server-manage) [![Docker Version](https://img.shields.io/docker/v/wekyjay/wk-server-manage)](https://hub.docker.com/r/wekyjay/wk-server-manage/tags)


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
