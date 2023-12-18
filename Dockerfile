# 第一阶段: 构建 Maven 项目
FROM maven:latest AS build
WORKDIR /app

# 将项目的 pom.xml 文件复制到容器中
COPY pom.xml .

# 下载项目的依赖项（这将利用 Maven 缓存）
RUN mvn dependency:go-offline

# 将项目源码复制到容器中
COPY src/ ./src/
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DB_URL
# 编译项目
RUN mvn package
#RUN ls -la /app
#FROM openjdk:latest
#RUN pwd
#RUN ls -la .
# Copy the Maven dependencies from the cache

CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
