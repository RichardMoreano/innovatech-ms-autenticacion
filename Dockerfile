# ====================== STAGE DE COMPILACIÓN ======================
FROM bellsoft/liberica-openjdk-alpine:25 AS builder
WORKDIR /app

# Instalar Maven dinámicamente sobre el entorno nativo Java 25 de Alpine
RUN apk add --no-cache maven

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ====================== STAGE DE EJECUCIÓN ======================
FROM bellsoft/liberica-openjdk-alpine:25
WORKDIR /app

# Instalar curl para los healthchecks y configurar usuario seguro no-root
RUN apk add --no-cache curl && \
	addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

# Puerto del servicio de autenticación
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
