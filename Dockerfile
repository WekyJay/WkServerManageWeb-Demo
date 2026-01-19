# ========================================================
# 第一阶段：前端构建 (Node.js 环境)
# 目的：将 Vue3 源码编译为浏览器可识别的静态文件 (dist)
# ========================================================
FROM docker.m.daocloud.io/library/node:20-slim AS frontend-builder

# 设置容器内的工作目录
WORKDIR /app/frontend

# 利用 Docker 缓存层优化：先只复制 package.json
# 只要 package.json 不动，下次构建时会直接跳过 npm install，大幅提速
COPY frontend/package*.json ./
RUN npm install

# 复制前端所有源码并执行打包
COPY frontend/ .
RUN npm run build


# ========================================================
# 第二阶段：后端构建 (Maven + JDK 环境)
# 目的：将 Java 源码和第一阶段生成的静态资源打包成一个可执行的 JAR
# ========================================================
FROM docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-17 AS backend-builder

# 设置容器内的总根目录（多模块项目必须在根目录构建）
WORKDIR /app

# 1. 复制父项目的 POM 文件，建立多模块依赖关系
COPY pom.xml .

# 2. 复制后端模块的 POM 文件和源码
COPY backend/pom.xml ./backend/
COPY backend/src ./backend/src

# 3. 【核心步骤】动静结合：将前端打包产物移动到 Java 的静态资源目录
# 这样打包出来的 Jar 包就自带前端界面了，类似于 Halo 的单文件分发
COPY --from=frontend-builder /app/frontend/dist /app/backend/src/main/resources/static

# 4. 执行 Maven 打包命令
# -pl backend: 仅仅构建 backend 模块
# -am: 同时构建 backend 依赖的其他内部模块 (Also Make)
# -DskipTests: 跳过单元测试，加快构建速度
RUN mvn clean package -pl backend -am -DskipTests


# ========================================================
# 第三阶段：最终运行镜像 (JRE 环境)
# 目的：只保留运行所需的最小环境，确保镜像轻量且安全
# ========================================================
FROM docker.m.daocloud.io/library/eclipse-temurin:17-jre

WORKDIR /app

# 从第二阶段的编译产物中，将生成的 JAR 包拷贝过来
# 提示：如果你的 pom 中设置了 <finalName>app</finalName>，这里的路径会更稳固
COPY --from=backend-builder /app/backend/target/*.jar app.jar

# 暴露后端服务端口
EXPOSE 8080

# 启动命令
# 使用 -XX:+UseContainerSupport 确保 Java 能够感知 Docker 的内存限制
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]