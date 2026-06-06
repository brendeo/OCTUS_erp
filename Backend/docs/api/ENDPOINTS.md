# GestorPyME — Endpoints da API

**Base URL:** `http://localhost:8080/api`

**Swagger UI (mock/dev):** `http://localhost:8080/swagger-ui.html`

## Perfis

| Perfil | Auth | Swagger |
|--------|------|---------|
| `mock` | Não | Sim |
| `dev` | JWT | Sim |
| `prod` | JWT obrigatório | Não |

Configure via `SPRING_PROFILES_ACTIVE` no `.env` — ver [backend/.env.example](../../backend/.env.example).

---

## Autenticação (perfis `dev`, `prod`, `test`)

| Método | Path | Descrição |
|--------|------|-----------|
| POST | `/auth/login` | Login → JWT |
| GET | `/auth/me` | Usuário autenticado (`Authorization: Bearer {token}`) |

---

## Permissões (JWT — perfis com security)

| Módulo | Roles |
|--------|-------|
| Produtos / movimentações | GESTOR, OPERADOR, ESTOQUE |
| Contas a pagar/receber | GESTOR, OPERADOR, CONTABIL |
| Dashboard / fluxo de caixa | GESTOR, OPERADOR, ESTOQUE, CONTABIL |
| Relatórios | GESTOR, CONTABIL |

Respostas de erro padronizadas: `401 UNAUTHORIZED`, `403 FORBIDDEN` — ver `examples/error-response.json`.

---

## Produtos

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/products` | Lista paginada (`search`, `category`, `active`, `page`, `size`) |
| GET | `/products/{id}` | Detalhe do produto |
| POST | `/products` | Criar produto |
| PUT | `/products/{id}` | Atualizar produto |
| PATCH | `/products/{id}/deactivate` | Inativar produto |
| GET | `/products/alerts/low-stock` | Produtos com estoque baixo |

---

## Movimentação de estoque

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/products/{productId}/movements` | Histórico (`from`, `to`, `type`) |
| POST | `/products/{productId}/movements` | Registrar entrada ou saída |

---

## Contas a pagar

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/payables` | Lista (`status`, `from`, `to`) |
| GET | `/payables/{id}` | Detalhe |
| POST | `/payables` | Criar |
| PUT | `/payables/{id}` | Atualizar |
| PATCH | `/payables/{id}/mark-paid` | Marcar como paga |

---

## Contas a receber

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/receivables` | Lista (`status`, `from`, `to`) |
| GET | `/receivables/{id}` | Detalhe |
| POST | `/receivables` | Criar |
| PUT | `/receivables/{id}` | Atualizar |
| PATCH | `/receivables/{id}/mark-received` | Marcar como recebida |

---

## Dashboard e fluxo de caixa

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/dashboard` | Indicadores consolidados |
| GET | `/cash-flow` | Fluxo de caixa (`from`, `to`) |

---

## Relatórios

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/reports/stock-movements` | Movimentações (`from`, `to`) |
| GET | `/reports/payables` | Contas a pagar |
| GET | `/reports/receivables` | Contas a receber |
| GET | `/reports/cash-flow` | Resumo de caixa |

---

## Auditoria (somente GESTOR)

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/audit` | Histórico (`entidade`, `from`, `to`, `page`, `size`) |

---

## Exemplos de DTO

Ver pasta [`examples/`](examples/).

## Códigos HTTP

| Código | Uso |
|--------|-----|
| 200 | Sucesso (GET, PUT, PATCH) |
| 201 | Criado (POST) |
| 400 | Validação / regra de negócio |
| 401 | Não autenticado |
| 403 | Sem permissão |
| 404 | Recurso não encontrado |
