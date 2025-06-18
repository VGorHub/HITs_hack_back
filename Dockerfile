# Dockerfile — многоступенчатая сборка и запуск
# Этап сборки
FROM gradle:8.7-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN gradle bootJar -x test                                      # собираем fat-jar  [oai_citation:0‡repo-to-text_2025-06-17-10-58-13-UTC.txt](file-service://file-LLWE4NGxgw6FUcSh4iaPJs)

# Этап запуска
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Только jar: keystore монтируем через docker-compose
COPY --from=builder /workspace/build/libs/*.jar app.jar

# JVM-опции: 8080 порт и prod–профиль
ENV JAVA_TOOL_OPTIONS="\
 -Dserver.port=8080 \
 -Dspring.profiles.active=prod"

EXPOSE 8080
EXPOSE 9092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]