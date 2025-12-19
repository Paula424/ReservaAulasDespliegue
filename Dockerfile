# --- Etapa de build (usa Maven con JDK 25) ---
FROM infotechsoft/maven:3.9.11-jdk-25 AS build
WORKDIR /app

# Copiamos el pom.xml y el src
COPY pom.xml .
COPY src ./src

# Construimos dejando pasar tests
RUN mvn clean package -DskipTests

# --- Etapa de runtime (solo para ejecutar) ---
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Copiamos el JAR resultante
COPY --from=build /app/target/*.jar app.jar

# Puerto que usa tu app
EXPOSE 8080

# Arranca la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
