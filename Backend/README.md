# GestorPyME

Sistema web de controle de estoque e contabilidade para pequenas e médias empresas.

## Documentação

- [PRD](PRD_sistema_estoque_contabilidade.md) — requisitos do produto
- [Backend](backend/README.md) — API REST Java/Spring Boot
- [Contrato API](docs/api/README.md) — OpenAPI, endpoints e exemplos de DTO
- [Entrega para o frontend](docs/api/ENTREGA_TIME.md) — resumo do que o time pediu

## Backend (início rápido)

Requisitos: **JDK 17+** e **Maven 3.9+**

```bash
cd backend
cp .env.example .env
mvn spring-boot:run
```

Swagger: http://localhost:8080/swagger-ui.html
