FROM openjdk:18-jdk-alpine
COPY ./target/app.jar /tmp/app.jar
COPY ./src/main/resources/region.json /tmp/region.json
ENTRYPOINT java -jar /tmp/app.jar -Xmx9093m -Xms1536m -Xmn3500m -XX:+UseG1GC  -XX:MetaspaceSize=96m