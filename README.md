# Merjane - Inventory Management

## Running tests

From the api folder:

```bash
./mvnw verify
```

## Architecture

```
controllers/    HTTP layer only, no business logic
services/       orchestration + product type handlers
persistence/    repositories and entities
domain/         shared types (ProductType enum)
messaging/      request/response DTOs
```
