# Etapa de build
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos archivos
COPY pom.xml .
COPY src ./src

# Construimos sin tests
RUN mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8

# Etapa runtime
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
