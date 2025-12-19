# Usamos una imagen base de OpenJDK 25
FROM eclipse-temurin:25-jdk-alpine

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copiamos el JAR generado en target/
COPY target/*.jar app.jar

# Exponemos el puerto que usará Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
