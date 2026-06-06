# GestorPyME — Backend

API REST em Java 171 + Spring Boot 3 para o sistema de estoque e contabilidade.

## Requisitos

- JDK 17+ (`java -version`)
- Maven 3.9+ (ou `mvnw.cmd` / `./mvnw` no projeto)
- Docker (perfis `dev` e `prod`)

## Perfis

| Perfil | Uso | Autenticação | Banco | Swagger |
|--------|-----|--------------|-------|---------|
| `mock` (padrão) | Integração frontend | Nenhuma | Em memória | Sim |
| `dev` | Desenvolvimento local | JWT | PostgreSQL + seed demo | Sim |
| `prod` | Apresentação / demo | JWT obrigatório | PostgreSQL limpo | Não |
| `test` | Testes (`mvnw verify`) | JWT | H2 em memória | Não |

## Usuários de demonstração (dev / prod)

Criados automaticamente na primeira subida (`UserSeedInitializer`):

| Perfil | E-mail | Senha |
|--------|--------|-------|
| GESTOR | admin@gestorpyme.local | admin123 |
| OPERADOR | operador@gestorpyme.local | operador123 |
| ESTOQUE | estoque@gestorpyme.local | estoque123 |
| CONTABIL | contabil@gestorpyme.local | contabil123 |

## Início rápido (mock — frontend)

```powershell
cd backend
copy .env.example .env
.\mvnw.cmd spring-boot:run
```

- API: http://localhost:8080/api/products
- Swagger: http://localhost:8080/swagger-ui.html

## Perfil dev (PostgreSQL + JWT + dados demo)

```powershell
docker compose up -d
# .env: SPRING_PROFILES_ACTIVE=dev
.\mvnw.cmd spring-boot:run
```

## Perfil prod (apresentação)

Sem seed de produtos/contas fictícios; apenas os 4 usuários acima.

**Opção A — local:**

```powershell
docker compose up -d
# .env: SPRING_PROFILES_ACTIVE=prod, JWT_SECRET (mín. 32 chars, único)
.\mvnw.cmd spring-boot:run
```

**Opção B — Docker (app + banco):**

```powershell
# .env com JWT_SECRET=...
docker compose -f docker-compose.prod.yml up --build
```

- Health: http://localhost:8080/actuator/health
- Auditoria (GESTOR): `GET /api/audit`

Checklist completo: [CHECKLIST_DEMO.md](CHECKLIST_DEMO.md)

## Testes

```powershell
.\mvnw.cmd verify
```

Cobertura: mock (produtos/movimentos), test (auth, payables, dashboard, audit, matriz de roles).

## Permissões (RF13)

| Recurso | Roles |
|---------|-------|
| Produtos / estoque | GESTOR, OPERADOR, ESTOQUE |
| Contas a pagar/receber | GESTOR, OPERADOR, CONTABIL |
| Dashboard / caixa | GESTOR, OPERADOR, ESTOQUE, CONTABIL |
| Relatórios | GESTOR, CONTABIL |
| Auditoria (`GET /api/audit`) | GESTOR |

## Documentação da API

- [docs/api/openapi.yaml](../docs/api/openapi.yaml)
- [docs/api/ENDPOINTS.md](../docs/api/ENDPOINTS.md)
- [docs/api/examples/](../docs/api/examples/)
- [docs/api/ENTREGA_TIME.md](../docs/api/ENTREGA_TIME.md)

## Auditoria (RF14)

Escritas registram em `audit_logs`; consulta via `GET /api/audit` (somente GESTOR).
