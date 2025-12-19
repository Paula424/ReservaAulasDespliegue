# --- Etapa de build ---
FROM infotechsoft/maven:3.9.11-jdk-25 AS build
WORKDIR /app

# Definimos JAVA_HOME y agregamos bin al PATH
ENV JAVA_HOME=/usr/lib/jvm/java-25-openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copiamos archivos
COPY pom.xml .
COPY src ./src

# Construimos sin tests
RUN mvn clean package -DskipTests
