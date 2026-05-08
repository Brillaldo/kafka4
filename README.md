# Sistema de Microservicios de E-commerce

Este proyecto es una arquitectura de microservicios robusta construida con **Spring Boot** y **Spring Cloud**, diseñada para gestionar operaciones de e-commerce como productos, órdenes y pagos. Utiliza una comunicación tanto síncrona (vía REST) como asíncrona (vía Kafka).

## 🚀 Arquitectura del Sistema

El proyecto está dividido en varios servicios especializados:

### 🏗️ Infraestructura y Core
*   **Eureka Server (`eureka-server`):** Registro de servicios para el descubrimiento dinámico de instancias.
*   **API Gateway (`apigateway`):** Punto de entrada único que enruta las peticiones a los servicios correspondientes (`/productos`, `/ordenes`, `/pagos`).
*   **Kafka:** Broker de mensajería para la comunicación dirigida por eventos entre servicios.
*   **LocalStack:** Simulación de servicios de AWS (utilizado para CloudWatch Logs).
*   **MailHog:** Servidor SMTP de prueba para visualizar correos electrónicos enviados por el sistema.

### 💼 Microservicios de Negocio
*   **Productos Service (`productos-service`):** Gestión del catálogo de productos. Almacena datos en MongoDB.
*   **Órdenes Service (`ordenes-service`):** Gestión del ciclo de vida de los pedidos. Almacena datos en MongoDB.
*   **Pagos Service (`pagos-service`):** Procesamiento y registro de pagos. Almacena datos en MongoDB.
*   **Broker Message BE (`broker-message-be`):** Servicio de soporte que consume eventos de Kafka, gestiona reintentos y envía notificaciones por correo. Utiliza PostgreSQL y MongoDB.

## 🛡️ Resiliencia y Gestión de Eventos

El sistema implementa un patrón de reintentos para asegurar la consistencia de los datos en caso de fallos temporales. El servicio `broker-message-be` escucha específicamente los siguientes tópicos de Kafka:
*   `payments_retry_jobs`: Reintentos para el procesamiento de pagos.
*   `order_retry_jobs`: Reintentos para la creación de órdenes.
*   `product_retry_jobs`: Reintentos para actualizaciones de productos.

Estos trabajos de reintento se almacenan y se gestionan para garantizar que ninguna operación crítica se pierda.

## 🛠️ Tecnologías Utilizadas

*   **Java 17**
*   **Spring Boot 3.x**
*   **Spring Cloud** (Gateway, Eureka)
*   **Kafka** (Spring Kafka)
*   **Bases de Datos:**
    *   MongoDB (NoSQL)
    *   PostgreSQL (Relacional)
*   **Docker & Docker Compose**
*   **AWS SDK** (vía LocalStack)

## 📦 Ejecución del Proyecto

Para levantar todo el ecosistema, se recomienda seguir este orden utilizando Docker:

1.  **Crear la red de Docker:**
    ```bash
    docker network create app-network
    ```

2.  **Levantar la infraestructura (Base de datos y Kafka):**
    ```bash
    docker-compose -f docker-compose-mongo.yml up -d
    docker-compose -f docker-compose-localstack.yml up -d
    ```

3.  **Levantar los microservicios:**
    ```bash
    docker-compose up -d
    ```

## 🛣️ Rutas de la API (vía Gateway - Puerto 8080)

| Servicio | Prefijo de Ruta | Puerto Original |
| :--- | :--- | :--- |
| Productos | `/productos/**` | 8081 |
| Órdenes | `/ordenes/**` | 8082 |
| Pagos | `/pagos/**` | 8083 |

## 📊 Herramientas de Monitoreo
*   **Eureka Dashboard:** `http://localhost:8761`
*   **Kafka UI:** `http://localhost:8090`
*   **MailHog Web UI:** `http://localhost:8025`

---
*Este proyecto fue generado para demostrar una arquitectura escalable y resiliente basada en eventos.*
