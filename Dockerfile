FROM gradle:8.14.3-jdk21 as builder
WORKDIR /home/gradle/pollApp
COPY . .
RUN ./gradlew bootJar
RUN mv backend/build/libs/backend-0.0.1-SNAPSHOT.jar pollApp.jar

FROM eclipse-temurin:21-alpine
RUN addgroup -g 1000 app
RUN adduser -G app -D -u 1000 -h /app PollApp

USER PollApp
WORKDIR /app

COPY --from=builder --chown=1000:1000 /home/gradle/pollApp/pollApp.jar .

CMD ["java", "-jar", "pollApp.jar"]