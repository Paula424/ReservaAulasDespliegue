# --- Etapa de build (usa Maven con JDK 25) ---
FROM infotechsoft/maven:3.9.11-jdk-25 AS build
WORKDIR /app

# Definimos JAVA_HOME (ruta típica de esta imagen)
ENV JAVA_HOME=/usr/lib/jvm/java-25-openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copiamos pom.xml y src
COPY pom.xml .
COPY src ./src

# Construimos dejando pasar tests
RUN mvn clean package -DskipTests

# --- Etapa de runtime ---
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Copiamos el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto que usa tu app
EXPOSE 8080

# Arranca la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
