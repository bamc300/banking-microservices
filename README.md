# 🏦 Sistema Bancario - Microservicios

Este es un proyecto práctico para simular un sistema bancario usando **Microservicios** y **Arquitectura Hexagonal**. La idea es tener separado lo que son Clientes (datos personales) de lo que son Cuentas (dinero y movimientos), pero que hablen entre ellos cuando hace falta.

---

## Cómo ejecitar

Lo más fácil es usar Docker. Si tienes Docker Desktop instalado, solo abre una terminal en la raíz del proyecto y corre:

```bash
docker compose up -d --build
```
Para desplegar unicamente los servicios para no tener que bajar las imagenes de la base de datos y Rabbit MQ
```bash
docker compose up -d --build ms-clientes-personas ms-cuentas-movimientos
```

Y se crea las imagenes postgress, Rabbit y los microservicios ejecutandose en un contenedor

---


## Stack Tecnológico

*   **Java 21 + Spring Boot 3.3** (lo último).
*   **PostgreSQL** (una base de datos para cada servicio).
*   **RabbitMQ** (para mensajería asíncrona).
*   **Docker** (para correr los servicios en contenedor).

---

Puertos a consumir, los 8081 para el endpoint de clientes, 8082 para el endpoint de cuenta y 5432 para la base de datos


Tambien cuena con Swagger:

Clientes: http://localhost:8081/swagger-ui/index.html#/
Cuentas: http://localhost:8082/swagger-ui/index.html#/
