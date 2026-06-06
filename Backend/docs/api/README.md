# Contrato de API — GestorPyME

Documentação do contrato REST compartilhado entre as equipes de frontend e backend.

## Arquivos

| Arquivo | Descrição |
|---------|-----------|
| [openapi.yaml](openapi.yaml) | Especificação OpenAPI 3 completa |
| [ENDPOINTS.md](ENDPOINTS.md) | Resumo legível dos endpoints |
| [examples/](examples/) | JSONs de exemplo para cada DTO |

## Autenticação

- **Perfil `mock`:** sem autenticação; registros usam `createdBy: "admin"`.
- **Perfil `dev`:** JWT obrigatório (exceto `POST /auth/login`). Ver `examples/login-request.json`.

## Padrões

- Datas: ISO-8601 (`2026-06-01` ou `2026-06-01T10:00:00Z`)
- Valores monetários: número JSON com até 2 casas decimais
- Paginação: `page` (0-based), `size` (padrão 20)

## Como rodar o backend

```bash
cd backend
cp .env.example .env
# Perfil mock (padrão):
mvn spring-boot:run

# Perfil dev com PostgreSQL:
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Swagger: http://localhost:8080/swagger-ui.html
