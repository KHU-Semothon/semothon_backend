FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir -p /app/uploads
ENTRYPOINT ["java", "-jar", "/app/app.jar"]