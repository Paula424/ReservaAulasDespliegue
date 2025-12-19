# --- Etapa de build ---
FROM infotechsoft/maven:3.9.11-jdk-25 AS build
WORKDIR /app

# Definimos JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-25-openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copiamos pom y src
COPY pom.xml .
COPY src ./src

# Construimos sin tests
RUN mvn clean package -DskipTests

# --- Etapa de runtime ---
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
