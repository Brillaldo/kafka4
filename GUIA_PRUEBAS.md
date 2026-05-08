# 🧪 Guía de Verificación de Flujos de Eventos

Esta guía detalla los pasos para probar los nuevos flujos asíncronos implementados con Kafka, el patrón Chain of Responsibility y PostgreSQL.

---

## 🏗️ Preparación Inicial

Asegúrate de que todos los servicios estén corriendo y las tablas creadas:
1. Reconstruye el broker: `docker-compose up -d --build broker-message-be`
2. Verifica las tablas en Postgres: 
   `docker exec -it postgres_broker_db psql -U postgres -d broker_db -c "\dt"`

---

## 🔥 Flujo 1: Inventario (Orden -> Descuento de Stock)

**Objetivo:** Verificar que al crear una orden, el stock del producto disminuye automáticamente.

1.  **Crear Producto (POST):**
    *   **URL:** `http://localhost:8080/productos`
    *   **Body:**
        ```json
        {
            "id": "LAPTOP-001",
            "name": "Laptop Pro",
            "price": 1200.0,
            "quantity": 10
        }
        ```
2.  **Crear Orden (POST):**
    *   **URL:** `http://localhost:8080/ordenes`
    *   **Body:**
        ```json
        {
            "usuarioId": "JUAN-01",
            "total": 1200.0,
            "productosIds": ["LAPTOP-001"]
        }
        ```
3.  **Verificar:** Haz un `GET http://localhost:8080/productos/LAPTOP-001`. El stock debe ser **9**.

---

## 🔥 Flujo 2: Envío por Cambio de Estado (Chain of Responsibility)

**Objetivo:** Verificar que al pasar una orden a "Pagado", se genera un envío y se notifican correos.

1.  **Actualizar Estado (PUT):**
    *   **URL:** `http://localhost:8080/ordenes/ID_DE_TU_ORDEN/status?status=Pagado`
2.  **Verificar Chain (Inmediato):**
    *   **MailHog:** `http://localhost:8025` -> Correo: "Cambio de Estado en su Orden".
    *   **Postgres:** `SELECT * FROM envios;` -> Registro con status `PENDING`.
3.  **Verificar Job (Después de 10 seg):**
    *   **MailHog:** Correo: "Su orden está en camino".
    *   **Postgres:** Registro con status `SENT`.

---

## 🔥 Flujo 3: Pagos y Validación (Validación de Chain)

**Objetivo:** Verificar que el pago dispara notificaciones pero solo genera envío si la orden está realmente pagada.

1.  **Crear Nueva Orden (POST):** (Status inicial: `CREATED`).
2.  **Registrar Pago (POST):**
    *   **URL:** `http://localhost:8080/pagos/procesar`
    *   **Body:**
        ```json
        {
            "ordenId": "ID_NUEVA_ORDEN",
            "monto": 1200.0
        }
        ```
3.  **Verificar:**
    *   **MailHog:** Recibes correo de "Pago Recibido".
    *   **Postgres:** **NO** se debe crear registro en `envios` porque la orden sigue en `CREATED`. La cadena se detuvo correctamente en el paso de validación.

---

## 🛠️ Herramientas de Diagnóstico

*   **Logs del Broker (Ver la cadena paso a paso):**
    `docker logs -f broker-message-be`
*   **MailHog (UI de correos):**
    `http://localhost:8025`
*   **Postgres (Consola):**
    `docker exec -it postgres_broker_db psql -U postgres -d broker_db`
*   **MongoDB (Stock de productos):**
    `docker exec -it mongodb-productos mongosh --eval "db.getSiblingDB('productos-db').productos.find()"`


\dt

SELECT * FROM envios;


1. Entrar a la base de datos
  Ejecuta este comando en tu terminal (CMD o PowerShell):

   1 docker exec -it postgres_broker_db psql -U postgres -d broker_db

  2. Comandos útiles dentro de Postgres (broker_db=#)
  Una vez que estés dentro (verás que el prompt cambia a broker_db=#), usa estos comandos:

   * Ver si las tablas existen (Listar tablas):
   1     \dt
      (Debes ver envios, order_retry_jobs, payments_retry_jobs, etc.)

   * Ver todos los envíos registrados:

   1     SELECT * FROM envios;

   * Ver solo los que el Job ya procesó (los que ya se enviaron):

   1     SELECT orden_id, status, sent_at FROM envios WHERE status = 'SENT';

   * Ver los reintentos de pagos (si hubo fallos):

   1     SELECT * FROM payments_retry_jobs;

   * Salir de Postgres:
      Escribe \q y pulsa Enter.