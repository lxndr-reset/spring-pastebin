FROM eclipse-temurin:21-jdk-jammy as base
EXPOSE 8080
ADD target/pastebin.jar pastebin.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "pastebin.jar"]
