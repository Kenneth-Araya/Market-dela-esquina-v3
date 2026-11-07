# ETAPA 1: Construcción (Docker descarga Maven y compila por ti)
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
# Copiamos el archivo de dependencias y el código fuente
COPY pom.xml .
COPY src ./src
# Ejecutamos la compilación dentro de Docker (sin usar el wrapper de tu Windows)
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Se toma el .jar fresco y se levanta)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copiamos el .jar que se acaba de crear en la Etapa 1
COPY --from=builder /app/target/*.jar app.jar
# Exponemos el puerto de producto
EXPOSE 8084

ENTRYPOINT ["java","-jar","app.jar"]