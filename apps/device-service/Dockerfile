# Stage 1: Build the application
FROM gradle:jdk21-alpine AS build
WORKDIR /app

COPY settings.gradle.kts gradle.properties gradlew /app/
COPY app/build.gradle.kts /app/app/
COPY --from=commons . /common

# Cache Gradle dependencies
RUN gradle dependencies

COPY . /app
RUN gradle build -x test

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app/build/libs/app.jar app.jar
EXPOSE 8083
# If your application uses a port
ENTRYPOINT ["java", "-jar", "app.jar"]
