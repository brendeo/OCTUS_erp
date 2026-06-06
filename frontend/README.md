# OCTUS — Frontend

Interface React para o backend GestorPyME (estoque e dashboard).

## Requisitos

- Node.js 18+
- Backend rodando em `http://localhost:8080`

## Configuração

```powershell
cd C:\Users\joseb\OneDrive\Documents\Desenvolvimento\Projetos\front_novo
copy .env.example .env
npm install
```

Ajuste `VITE_API_URL` se o backend usar outra porta.

## Executar

```powershell
npm run dev
```

Abra http://localhost:5173

## Autenticação JWT

Use o backend com perfil **dev** ou **prod** (`app.security.enabled=true`):

| E-mail | Senha |
|--------|-------|
| admin@gestorpyme.local | admin123 |

No perfil **mock** a API não exige token; o login ainda funciona se o endpoint estiver habilitado.

## Telas

1. **Login** — `POST /api/auth/login`, token em `localStorage`
2. **Dashboard** — gráficos + histórico de movimentações (`/api/reports/stock-movements` ou fallback por produto)
3. **Estoque de Produtos** — CRUD, busca, detalhes, entrada/saída de estoque
4. **Finanças (Contabilidade)** — relatórios `/api/reports/*` (perfis **GESTOR** ou **CONTABIL**)

| Perfil | E-mail | Senha |
|--------|--------|-------|
| CONTABIL | contabil@gestorpyme.local | contabil123 |

## Build

```powershell
npm run build
```
