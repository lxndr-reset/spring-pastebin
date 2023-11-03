FROM eclipse-temurin:21-jdk-jammy as base
EXPOSE 8080
ADD target/pastebin.jar pastebin.jar
ARG SPRING_ACTIVE_PROFILE
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${SPRING_ACTIVE_PROFILE}", "-jar", "pastebin.jar"]