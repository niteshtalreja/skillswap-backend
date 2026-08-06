FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

RUN ./mvnw clean install -DskipTests

EXPOSE 8080

CMD ["java", "-Xms256m", "-Xmx384m", "-jar", "target/skillswap-backend-*.jar"]