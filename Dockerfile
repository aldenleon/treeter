FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*.jar treeter-app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "treeter-app.jar"]
