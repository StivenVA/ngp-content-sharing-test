# ===== Builder =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests clean package

# ===== Runtime =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/content-sharing-test-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/content_sharing_test?useSSL=false&serverTimezone=UTC \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=password123 \
    SERVER_PORT=8080

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
