# Build stage
FROM gradle:8.13-jdk21 AS build
WORKDIR /app
# 빌드 파일만 먼저 복사 — 소스 변경 시에도 이 레이어는 캐시됨
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle dependencies --no-daemon
# 소스 복사 후 빌드
COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
