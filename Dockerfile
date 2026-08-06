FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

RUN ./mvnw clean install -DskipTests

EXPOSE 8080

CMD ["java", "-Xms256m", "-Xmx384m", "-jar", "target/skillswap-backend-*.jar"]