# Checklist — demo GestorPyME (perfil prod)

## Pré-requisitos

- [ ] JDK 17+ (`java -version`)
- [ ] Docker Desktop rodando
- [ ] Arquivo `.env` com `JWT_SECRET` único (mín. 32 caracteres)

## Subir ambiente

```powershell
cd backend
copy .env.example .env
# Editar .env: JWT_SECRET=...
docker compose -f docker-compose.prod.yml up --build -d
```

Aguardar health: http://localhost:8080/actuator/health

## Usuários de demonstração

| Perfil | E-mail | Senha |
|--------|--------|-------|
| GESTOR | admin@gestorpyme.local | admin123 |
| OPERADOR | operador@gestorpyme.local | operador123 |
| ESTOQUE | estoque@gestorpyme.local | estoque123 |
| CONTABIL | contabil@gestorpyme.local | contabil123 |

## Testes RF13 (permissões)

1. **Login GESTOR** → `POST /api/auth/login` → copiar `token`
2. **Header:** `Authorization: Bearer {token}`
3. **GESTOR** → `GET /api/audit` → **200**
4. **OPERADOR** → login operador → `GET /api/reports/payables` → **403**
5. **OPERADOR** → `GET /api/products` → **200**
6. **CONTABIL** → `GET /api/products` → **403**
7. **CONTABIL** → `GET /api/payables` → **200**
8. **ESTOQUE** → `GET /api/products` → **200**

## Verificações MVP

- [ ] Swagger **não** abre em prod (`/swagger-ui.html` indisponível ou desabilitado)
- [ ] Sem token → `401` com JSON `UNAUTHORIZED`
- [ ] `POST /api/products` + movimentação atualiza saldo
- [ ] `GET /api/dashboard` retorna indicadores

## Testes automatizados (local)

```powershell
.\mvnw.cmd verify
```
