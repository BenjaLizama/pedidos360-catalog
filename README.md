# ms-catalog — Pedidos360

Microservicio responsable de la gestión del catálogo de productos y categorías de **Pedidos360**. Desarrollado con Java 21 y Spring Boot, utiliza MongoDB como base de datos, Spring Security con OAuth2 Resource Server para la protección de endpoints y Amazon SQS para la integración mediante mensajería.

## Tabla de contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Funcionalidades](#funcionalidades)
- [Arquitectura](#arquitectura)
- [Requisitos](#requisitos)
- [Configuración del entorno](#configuración-del-entorno)
- [Ejecución con Docker Compose](#ejecución-con-docker-compose)
- [Ejecución local](#ejecución-local)
- [Endpoints](#endpoints)
- [Documentación de la API](#documentación-de-la-api)
- [Persistencia e infraestructura](#persistencia-e-infraestructura)
- [Pruebas](#pruebas)
- [Seguridad](#seguridad)
- [Estado del desarrollo](#estado-del-desarrollo)

## Descripción

`ms-catalog` centraliza las operaciones relacionadas con el catálogo de Pedidos360, proporcionando endpoints REST para administrar productos y categorías.

El servicio está preparado para ejecutarse de forma independiente durante el desarrollo y como parte del entorno distribuido del proyecto mediante Docker Compose.

## Tecnologías

| Tecnología | Propósito |
|---|---|
| Java 21 | Lenguaje y plataforma de ejecución |
| Spring Boot 4.1.1 | Framework de aplicación |
| Maven | Gestión de dependencias y construcción |
| Spring Web | API REST |
| Spring Data MongoDB | Persistencia de documentos |
| Spring Security | Seguridad de la aplicación |
| OAuth2 Resource Server | Validación de tokens JWT |
| Spring Cloud AWS SQS | Integración con Amazon SQS |
| Spring Boot Actuator | Monitorización y health checks |
| Springdoc OpenAPI | Documentación de endpoints |
| MongoDB 8 | Base de datos documental |
| LocalStack | Emulación local de servicios AWS |
| Docker Compose | Orquestación del entorno local |
| Testcontainers | Infraestructura para pruebas de integración |

## Funcionalidades

### Productos

- Crear productos.
- Consultar productos por identificador.
- Buscar productos.
- Actualizar información de productos.
- Cambiar el estado de un producto.
- Actualizar precios.
- Ajustar existencias.
- Reabastecer stock.
- Eliminar productos.
- Registrar información histórica de precios y movimientos de stock, según la lógica implementada en el servicio.

### Categorías

- Crear categorías.
- Consultar y listar categorías.
- Actualizar categorías.
- Eliminar categorías.

### Integración e infraestructura

- Protección de endpoints mediante JWT.
- Integración con MongoDB.
- Configuración de mensajería con Amazon SQS.
- Ejecución local con Docker Compose.
- Exposición de métricas operacionales mediante Spring Boot Actuator.

## Arquitectura

El microservicio utiliza una arquitectura por capas para separar las responsabilidades de la aplicación.

```text
ms-catalog
├── controller       # Endpoints REST
├── dto              # Objetos de entrada y salida
├── entity           # Modelos de persistencia
├── mapper           # Conversión entre entidades y DTOs
├── repository       # Acceso a MongoDB
├── service          # Contratos de negocio
├── service/impl     # Implementaciones de negocio
├── config           # Configuración de la aplicación
├── security         # Seguridad y autorización
├── exception        # Excepciones y manejo de errores
└── observability    # Correlation ID y observabilidad
```

La estructura anterior representa las responsabilidades principales; los paquetes concretos pueden variar según la organización actual del código.

## Requisitos

Para ejecutar el servicio localmente necesitas:

- Java 21.
- Maven 3.9 o compatible con el proyecto.
- Docker Engine.
- Docker Compose v2.
- Git.

## Configuración del entorno

La configuración se encuentra distribuida entre los perfiles de Spring Boot y las variables de entorno.

Archivos principales:

```text
ms-catalog/
├── Dockerfile
├── pom.xml
├── README.md
└── src/
    └── main/
        └── resources/
            ├── application.yaml
            ├── application-dev.yaml
            └── application-prod.yaml
```

El archivo `.env` y el `docker-compose.yml` se encuentran en la raíz del monorepo.

### Variables de entorno

| Variable | Descripción | Valor de desarrollo de ejemplo |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring activo | `dev` |
| `SERVER_PORT` | Puerto de la aplicación | `8081` |
| `MONGODB_URI` | URI de conexión a MongoDB | `mongodb://mongo:27017/pedidos360_catalog?replicaSet=rs0` |
| `MONGO_DATABASE` | Nombre lógico de la base de datos | `pedidos360_catalog` |
| `MONGO_PORT` | Puerto publicado de MongoDB | `27017` |
| `KEYCLOAK_ISSUER_URI` | Emisor esperado de los JWT | `http://host.docker.internal:8080/realms/pedidos360` |
| `JWT_ROLES_CLAIM` | Ruta del claim que contiene los roles | `realm_access.roles` |
| `JWT_ROLES_PREFIX` | Prefijo de las autoridades de Spring Security | `ROLE_` |
| `AWS_REGION` | Región de AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Credencial AWS para desarrollo local | `test` |
| `AWS_SECRET_ACCESS_KEY` | Credencial AWS para desarrollo local | `test` |
| `AWS_SQS_ENDPOINT` | Endpoint de SQS | `http://localstack:4566` |
| `LOCALSTACK_PORT` | Puerto publicado de LocalStack | `4566` |
| `SERVICES` | Servicios AWS habilitados en LocalStack | `sqs` |

Los valores de ejemplo corresponden al entorno local descrito en la configuración. Verifica los nombres y valores efectivos contra el `.env.example` y los archivos YAML del repositorio.

**Importante:** dentro de Docker Compose, los contenedores deben comunicarse utilizando los nombres de servicio, como `mongo` y `localstack`. Las direcciones `localhost` y `host.docker.internal` tienen un significado distinto dentro de los contenedores.

No subas el archivo `.env` con credenciales reales al repositorio. Utiliza `.env.example` para documentar las variables necesarias sin incluir secretos.

## Ejecución con Docker Compose

Los siguientes comandos se ejecutan desde la raíz del monorepo, donde se encuentra `docker-compose.yml`.

### 1. Preparar las variables

Crea tu archivo `.env` a partir del ejemplo, si todavía no existe:

```bash
cp .env.example .env
```

Ajusta las variables de desarrollo según tu entorno.

### 2. Validar la configuración

```bash
docker compose config
```

### 3. Construir y levantar los servicios

```bash
docker compose up -d --build
```

Para reconstruir solamente el microservicio:

```bash
docker compose build ms-catalog
docker compose up -d ms-catalog
```

### 4. Revisar el estado

```bash
docker compose ps
```

Consultar los logs del microservicio:

```bash
docker compose logs -f ms-catalog
```

Consultar los logs de MongoDB:

```bash
docker compose logs -f mongo
```

Consultar los logs de LocalStack:

```bash
docker compose logs -f localstack
```

### 5. Detener los servicios

```bash
docker compose down
```

Este comando detiene y elimina los contenedores de Compose, pero normalmente conserva los volúmenes nombrados. Para eliminar también los datos persistidos, se requiere `docker compose down -v`; utiliza esa opción únicamente cuando realmente quieras borrar los datos locales.

## Ejecución local

Para iniciar la aplicación fuera de Docker, asegúrate de que MongoDB y los servicios externos requeridos estén disponibles y de que las URI utilicen direcciones accesibles desde el equipo anfitrión.

Desde el directorio del microservicio:

```bash
cd ms-catalog
mvn spring-boot:run
```

Para activar explícitamente el perfil de desarrollo:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

La aplicación utiliza el puerto configurado por `SERVER_PORT`, cuyo valor de desarrollo es `8081`.

## Endpoints

Los endpoints siguientes corresponden a las rutas conocidas del microservicio. Los permisos efectivos deben verificarse en la configuración de seguridad y en las anotaciones de cada controlador.

### Productos

| Método | Endpoint | Operación |
|---|---|---|
| `POST` | `/api/v1/products` | Crear producto |
| `GET` | `/api/v1/products/{productId}` | Consultar producto por ID |
| `GET` | `/api/v1/products/search` | Buscar productos |
| `PUT` | `/api/v1/products/{productId}` | Actualizar producto |
| `PATCH` | `/api/v1/products/{productId}/status` | Cambiar estado |
| `PATCH` | `/api/v1/products/{productId}/price` | Actualizar precio |
| `PATCH` | `/api/v1/products/{productId}/stock` | Ajustar stock |
| `PATCH` | `/api/v1/products/{productId}/restock` | Reabastecer stock |
| `DELETE` | `/api/v1/products/{productId}` | Eliminar producto |

### Categorías

| Método | Endpoint | Operación |
|---|---|---|
| `GET` | `/api/v1/categories` | Listar categorías |
| `POST` | `/api/v1/categories` | Crear categoría |
| `PUT` | `/api/v1/categories/{categoryId}` | Actualizar categoría |
| `DELETE` | `/api/v1/categories/{categoryId}` | Eliminar categoría |

### Historial de precios y movimientos de stock

El dominio contempla información histórica de precios y movimientos de inventario. Antes de documentar endpoints de consulta específicos, debe comprobarse si existen controladores para estas operaciones.

Las rutas siguientes son propuestas de diseño, **no endpoints confirmados**:

- `GET /api/v1/products/{productId}/price-history`
- `GET /api/v1/products/{productId}/stock-movements`

Si todavía no existen, el historial y los movimientos pueden registrarse internamente al ejecutar operaciones de actualización de precio, ajuste de stock o reabastecimiento, según las reglas de negocio del proyecto.

## Documentación de la API

Con la aplicación en ejecución, consulta:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- Health check: `http://localhost:8081/actuator/health`
- Información del servicio: `http://localhost:8081/actuator/info`

Los endpoints de Actuator y la documentación de OpenAPI pueden estar restringidos según el perfil y las reglas de Spring Security.

## Persistencia e infraestructura

### MongoDB

El microservicio utiliza MongoDB para almacenar la información del catálogo. En el entorno Docker, la conexión se configura mediante `MONGODB_URI`.

La configuración del replica set `rs0` debe coincidir con la inicialización efectiva del contenedor MongoDB. Una URI que incluya `replicaSet=rs0` requiere que dicho replica set esté correctamente configurado.

### Amazon SQS y LocalStack

LocalStack permite desarrollar y probar integraciones con SQS sin utilizar recursos reales de AWS.

En el entorno Docker, el endpoint de SQS debe apuntar al servicio `localstack` y al puerto interno `4566`.

Las colas requeridas deben crearse o inicializarse según la configuración efectiva de mensajería del microservicio. Tener LocalStack en ejecución no garantiza, por sí solo, que todas las colas estén creadas.

## Pruebas

Ejecutar las pruebas desde el directorio `ms-catalog`:

```bash
mvn test
```

Empaquetar la aplicación:

```bash
mvn clean package
```

Las pruebas de integración que utilizan Testcontainers requieren un entorno compatible con Docker.

## Seguridad

El servicio utiliza Spring Security OAuth2 Resource Server para validar tokens JWT.

La validación del emisor se configura mediante `KEYCLOAK_ISSUER_URI`. La conversión de roles a autoridades de Spring Security depende de los valores configurados en `JWT_ROLES_CLAIM` y `JWT_ROLES_PREFIX`.

En el entorno de producción:

- Utiliza un emisor JWT accesible y correctamente configurado.
- No uses credenciales de prueba de LocalStack.
- Gestiona los secretos mediante variables seguras o un gestor de secretos.
- Expón únicamente los puertos y endpoints necesarios.
- Mantén deshabilitados los detalles sensibles de los health checks.
- Verifica los permisos de escritura y lectura de cada endpoint.

## Estado del desarrollo

Este README documenta la estructura general, la configuración del entorno y los endpoints conocidos. Antes de considerar la documentación definitiva, se deben validar contra el código fuente:

- Las rutas reales de consulta de historial de precios y movimientos de stock.
- Los roles requeridos por cada endpoint.
- La inicialización de las colas SQS.
- La configuración efectiva del replica set de MongoDB.
- La ejecución de las pruebas de integración.

---

**Pedidos360 — `ms-catalog`**

Microservicio de catálogo, gestión de productos y categorías.