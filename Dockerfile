# Usa la imagen oficial de Temurin 17 como base
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo .jar generado por Maven al contenedor
COPY target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
