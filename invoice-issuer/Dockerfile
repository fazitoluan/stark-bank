FROM openjdk:17-jdk-alpine
LABEL maintainer="invoice.issuer"
VOLUME /tmp
EXPOSE 8081
ARG JAR_FILE=target/invoice-issuer-0.0.1-SNAPSHOT.jar
ADD target/invoice-issuer-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]