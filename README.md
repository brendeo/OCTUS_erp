##  Sobre o Projeto
O **OCTUS_erp** é um sistema web Fullstack desenvolvido como Projeto de Extensão universitário. Seu objetivo principal é fornecer uma ferramenta acessível, prática e sem burocracia para que pequenos e microempreendedores consigam organizar o fluxo de caixa, separar as finanças pessoais das empresariais e gerenciar o estoque.

A ideia central é provar que a tecnologia pode ser uma aliada simples no dia a dia do pequeno negócio, entregando controle sem a complexidade dos grandes sistemas do mercado.

---

## Tecnologias Utilizadas

**Frontend (Interface):**
* React
* TypeScript
* Vite

**Backend (API e Regras de Negócio):**
* Java
* Spring Boot
* Maven

---

## Como executar o projeto localmente

### Pré-requisitos
Certifique-se de ter as seguintes ferramentas instaladas na sua máquina:
* **Java** (JDK 17 ou superior)
* **Node.js** (Versão LTS)

O sistema é dividido em duas partes que precisam ser iniciadas em terminais separados.

### Passo 1: Iniciando o Backend
1. Abra um terminal e navegue até a pasta do backend:
   cd backend
   
2. Execute o projeto utilizando o Maven Wrapper:
   .\\mvnw.cmd spring-boot:run
   *(Nota: Se estiver usando Mac ou Linux, utilize `./mvnw spring-boot:run`)*
   
3. Aguarde a mensagem "Started Application". O servidor estará rodando na porta 8080. **Mantenha este terminal aberto.**

### Passo 2: Iniciando o Frontend
1. Abra um **novo terminal** (sem fechar o anterior) e navegue até a pasta do frontend:
   cd frontend
   
2. Instale as dependências do projeto (necessário apenas na primeira vez):
   npm install
   
3. Inicie o servidor de desenvolvimento:
   npm run dev
   
4. O terminal exibirá um link local (ex: http://localhost:5173/). Acesse este link no seu navegador.

---

## Acesso ao Sistema (Modo de Teste)
O projeto conta com um perfil "mock" configurado para facilitar testes e demonstrações sem a necessidade de configurar um banco de dados local.

Para acessar o painel, utilize as seguintes credenciais na tela de login:
* **E-mail:** teste@teste.com
* **Senha:** teste
