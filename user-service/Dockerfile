FROM openjdk:8-jdk-alpine
COPY ./target/app.jar /tmp/app.jar

ENTRYPOINT java -jar /tmp/app.jar -Xmx9093m -Xms1536m -Xmn3500m -XX:+UseG1GC  -XX:MetaspaceSize=96m