FROM openjdk:latest

WORKDIR /app

# Copy the Maven dependencies from the cache
COPY target/backend-0.0.1-SNAPSHOT.jar /app

# Copy the application JAR
COPY target/backend-0.0.1-SNAPSHOT.jar /app

CMD ["java", "-jar", "/app/backend-0.0.1-SNAPSHOT.jar"]
