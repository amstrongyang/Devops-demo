# Order Service CI/CD Demo

A deliberately small Java 17/Spring Boot service. The business code is only a vehicle for practising the path from source code to a production-like deployment.

## Day 1 scope

- `POST /api/orders`
- `GET /api/orders/{id}`
- `GET /api/orders`
- Request validation and consistent error responses
- H2 for zero-setup local development and tests
- PostgreSQL for production-like execution
- Unit, web-slice, context and optional Testcontainers tests
- Health endpoints for later container/orchestrator probes
- A usable first Dockerfile and Docker Compose stack

CI workflows, Kubernetes/OpenShift manifests, registry publishing and security scanning are intentionally not implemented yet. Their empty homes are present for the later exercises.

## Requirements

- Java 17+
- Maven 3.9+ (or use your IDE's Maven support)
- Docker Desktop only for PostgreSQL/Testcontainers/container exercises

## Run locally (fastest)

```bash
mvn clean test
mvn spring-boot:run
```

The default `dev` profile uses an in-memory H2 database. Check health at `http://localhost:8080/actuator/health`.

Create and read orders:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Alex","product":"Mechanical Keyboard","quantity":2,"unitPrice":89.90}'

curl http://localhost:8080/api/orders
curl http://localhost:8080/api/orders/1
```

PowerShell equivalent:

```powershell
$body = @{ customerName='Alex'; product='Mechanical Keyboard'; quantity=2; unitPrice=89.90 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/orders -ContentType application/json -Body $body
Invoke-RestMethod http://localhost:8080/api/orders
```

## Run the production-like stack

```bash
docker compose up --build
docker compose down
```

Data is kept in the `postgres-data` volume. To remove it deliberately, use `docker compose down -v`.

## Configuration

| Profile | Database | Intended use |
|---|---|---|
| `dev` (default) | H2 memory | quick local work |
| `test` | H2 memory | deterministic automated tests |
| `prod` | PostgreSQL via environment variables | containers and deployments |

Production environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`; optional `SERVER_PORT`.

> `ddl-auto: update` is acceptable for this short training project, but production systems normally use Flyway or Liquibase migrations. Add that as a stretch exercise after the seven-day core.

## Project map

```text
src/main/java/nz/co/demo/orders/
├── config/       shared Spring configuration
├── controller/   HTTP boundary
├── dto/          API request/response models
├── entity/       persistence model
├── exception/    API errors
├── repository/   JPA data access
└── service/      business/use-case layer
.github/workflows/ reserved for Day 3
infra/             reserved for Days 5–6
```

## Test layers

- `OrderServiceTest`: fast Mockito unit test.
- `OrderControllerTest`: HTTP contract and validation without a real server.
- `OrderServiceApplicationTest`: full application context using H2.
- `OrderRepositoryContainerTest`: real PostgreSQL via Testcontainers; automatically skipped when Docker is unavailable.

## Definition of done for Day 1

- `mvn clean test` passes.
- The service starts locally and `/actuator/health` returns `UP`.
- You can create an order and retrieve it.
- You can explain why unit, slice and integration tests belong at different points in a pipeline.

Continue with [TRAINING_PLAN.md](TRAINING_PLAN.md). Do not copy later-day configuration blindly: build each step, break it, inspect the evidence, then repair it.
