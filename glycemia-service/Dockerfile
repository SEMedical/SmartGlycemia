FROM openjdk:8-jdk-alpine
COPY ./target/app.jar /tmp/app.jar
ENTRYPOINT java -jar /tmp/app.jar -Xmx9093m -Xms1536m -Xmn4096m -XX:+UseG1GC -XX:MaxGCPauseMillis=1 -XX:MetaspaceSize=96m