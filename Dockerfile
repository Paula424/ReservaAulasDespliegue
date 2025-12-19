# --- Etapa de build ---
FROM maven:3.9.4-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos pom.xml y src
COPY pom.xml .
COPY src ./src

# Construimos la aplicación y saltamos tests
RUN mvn clean package -DskipTests

# --- Etapa de runtime ---
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Copiamos el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java","-jar","app.jar"]
