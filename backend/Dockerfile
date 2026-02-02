FROM gradle:8-jdk17 AS builder

LABEL author="Soft Uzbek Slogan"

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-damon -x test

FROM openjdk:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-app.jar"]