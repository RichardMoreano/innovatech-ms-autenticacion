# Guía de Ejecución con Docker - Servicio de Autenticación V2

Este microservicio gestiona el ciclo de vida de las identidades corporativas, la persistencia de usuarios y la emisión de credenciales criptográficas estructuradas (JWT) bajo el enfoque CSR.

## 1. Prerrequisitos y Dependencias
* **Red de Docker:** `innovatech-net`.
* **Persistencia:** Requiere conectividad con el contenedor de base de datos (`innovatech-db`) y la correcta inicialización del esquema de usuarios antes de aceptar peticiones concurrentes.

## 2. Puertos y Mapeo de Red
* **Puerto Interno (Contenedor):** `8082`
* **Puerto Externo (Host):** `8082` (Accesible perimetralmente para flujos de login/registro a través del API Gateway en el puerto `8083`).

---

## 3. Comandos de Operación

### Despliegue y Construcción de Imagen
```bash
docker compose up -d --build innovatech-ms-autenticacion