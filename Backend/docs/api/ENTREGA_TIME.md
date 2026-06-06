# Entrega para o time de frontend

Olá! Segue o que vocês pediram no grupo:

## 1. Endpoints

Lista completa: [ENDPOINTS.md](ENDPOINTS.md)  
OpenAPI: [openapi.yaml](openapi.yaml)  
**Swagger (perfil mock/dev):** http://localhost:8080/swagger-ui.html

Base URL: `http://localhost:8080/api`

## 2. Modelos (DTOs) — exemplos JSON

Pasta [examples/](examples/) — inclui `audit-log-response.json` para histórico de auditoria.

## 3. Dados mockados

Perfil **mock** (padrão): 8 produtos, movimentações e contas de exemplo — sem login.

## 4. Autenticação e perfis

| Perfil | Uso | Login |
|--------|-----|-------|
| **mock** | Integração frontend | Não precisa |
| **dev** / **prod** | Demo com JWT | `POST /api/auth/login` |

### Usuários de teste (dev/prod)

| Perfil | E-mail | Senha |
|--------|--------|-------|
| GESTOR | admin@gestorpyme.local | admin123 |
| OPERADOR | operador@gestorpyme.local | operador123 |
| ESTOQUE | estoque@gestorpyme.local | estoque123 |
| CONTABIL | contabil@gestorpyme.local | contabil123 |

Header: `Authorization: Bearer {token}`

## 5. Variáveis de ambiente

Ver [backend/.env.example](../../backend/.env.example)

## Como rodar (frontend)

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Teste: `GET http://localhost:8080/api/products`

## Demo da disciplina

Perfil **prod** — ver [backend/CHECKLIST_DEMO.md](../../backend/CHECKLIST_DEMO.md) e [backend/README.md](../../backend/README.md).
