# 第一阶段: 构建 Maven 项目
FROM maven:latest AS build
WORKDIR /app

# 将项目的 pom.xml 文件复制到容器中
COPY pom.xml .

# 下载项目的依赖项（这将利用 Maven 缓存）
RUN mvn dependency:go-offline

# 将项目源码复制到容器中
COPY src/ ./src/

# 编译项目
RUN mvn package
FROM openjdk:latest

WORKDIR /app

# Copy the Maven dependencies from the cache
COPY --from=build target/backend-0.0.1-SNAPSHOT.jar /app

# Copy the application JAR
COPY target/backend-0.0.1-SNAPSHOT.jar /app

CMD ["java", "-jar", "/app/backend-0.0.1-SNAPSHOT.jar"]
