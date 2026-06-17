# ====================== STAGE DE COMPILACIÓN ======================
FROM bellsoft/liberica-openjdk-alpine:25 AS builder
WORKDIR /app

# Instalar Maven de forma nativa sobre Alpine
RUN apk add --no-cache maven

# Copiar configuración pom.xml y descargar dependencias en caché
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y empaquetar omitiendo pruebas radicalmente
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# ====================== STAGE DE EJECUCIÓN ======================
FROM bellsoft/liberica-openjdk-alpine:25
WORKDIR /app

# Instalar curl para Healthchecks y configurar usuario no-root seguro
RUN apk add --no-cache curl && \
    addgroup -S spring && adduser -S spring -G spring

# Copiar el binario generado
COPY --from=builder /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]