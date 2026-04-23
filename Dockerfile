# Stage 1: Build menggunakan Maven
FROM maven:3.8.4-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run menggunakan JRE yang lebih ringan
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]