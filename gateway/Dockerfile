FROM openjdk:8-jdk-alpine
COPY ./target/app.jar /tmp/app.jar
ENTRYPOINT java -jar /tmp/app.jar -Xmx3144m -Xms1024m -Xmn1024m -XX:+UseG1GC -XX:MetaspaceSize=96m