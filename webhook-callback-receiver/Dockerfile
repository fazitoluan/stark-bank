FROM openjdk:17-jdk-alpine
LABEL maintainer="webhook.callback.receiver"
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/webhook-callback-receiver-0.0.1-SNAPSHOT.jar
ADD target/webhook-callback-receiver-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]