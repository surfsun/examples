FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

COPY target/rabbitmq-tutorial-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
