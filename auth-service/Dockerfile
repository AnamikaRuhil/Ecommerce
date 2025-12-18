# ----- BUILD STAGE -----
FROM gradle:8.4-jdk17 AS build

# Copy root project (multi-module)
WORKDIR /home/gradle/project
COPY . .

# Build only this microservice
ARG MODULE_NAME
RUN gradle :${MODULE_NAME}:clean :${MODULE_NAME}:bootJar --no-daemon

# ----- RUNTIME STAGE -----
FROM eclipse-temurin:17-jre
WORKDIR /app

ARG MODULE_NAME
COPY --from=build /home/gradle/project/${MODULE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
