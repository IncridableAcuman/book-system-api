FROM gradle:8-jdk17 AS builder

LABEL author="Soft Uzbek Slogan"

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar"]
