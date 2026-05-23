# Portfolio Manager API

Sistema para gerenciamento de portfólio de projetos de uma empresa, permitindo o acompanhamento completo do ciclo de vida de cada projeto — desde a análise de viabilidade até a finalização — incluindo gerenciamento de equipe, orçamento e risco.

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Security** (autenticação HTTP Basic em memória)
- **Spring Data JPA + Hibernate**
- **PostgreSQL**
- **Flyway** (migrations automáticas de banco)
- **MapStruct** (mapeamento DTO ↔ Entity)
- **Lombok**
- **Springdoc OpenAPI 2 / Swagger UI**
- **JUnit 5 + Mockito + AssertJ** (testes unitários)
- **JaCoCo** (cobertura de testes — mínimo 70% nos services)
- **Maven**

---

## Pré-requisitos

Antes de rodar a aplicação, certifique-se de ter instalado:

| Ferramenta | Versão mínima |
|-----------|--------------|
| Java (JDK) | 21 |
| PostgreSQL | 13+ |
| IntelliJ IDEA | qualquer versão recente |

> **Maven** não precisa estar instalado separadamente. O IntelliJ já inclui uma versão embutida utilizada por este projeto.

---

## Configuração do Banco de Dados

### 1. Criar o banco

Conecte-se ao PostgreSQL com seu cliente preferido (psql, pgAdmin, DBeaver, etc.) e execute:

```sql
CREATE DATABASE "db-code-group";
```

> O banco precisa estar criado antes de subir a aplicação. O schema (tabelas, índices) é criado automaticamente pelo **Flyway** na primeira inicialização.

### 2. Verificar credenciais

As credenciais de acesso ao banco ficam no arquivo `resources/application-local.yml`. Confira se os valores batem com a sua instalação local do PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db-code-group
    username: postgres
    password: admin
```

Altere `username` e `password` conforme necessário.

---

## Arquivos de Configuração

O projeto possui dois arquivos de configuração:

### `resources/application.yml` — configurações base (não alterar)

Contém as configurações comuns a todos os ambientes: porta do servidor, configurações do JPA, Flyway e Swagger. **Não deve conter credenciais.**

```yaml
server:
  port: 8080
  servlet:
    context-path: /api   # todas as rotas ficam em /api/...
```

### `resources/application-local.yml` — configurações do ambiente local

Sobrescreve as configurações base com valores específicos para desenvolvimento local. **Altere aqui** as credenciais do banco, ative logs de SQL, etc.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db-code-group
    username: postgres      # altere para o seu usuário
    password: admin         # altere para a sua senha
  jpa:
    show-sql: true          # exibe as queries no console
logging:
  level:
    br.com.codegroup: DEBUG
    org.hibernate.SQL: DEBUG
```

> O profile `local` é ativado por padrão (`spring.profiles.active: local` no `application.yml`). Em produção, defina a variável de ambiente `SPRING_PROFILES_ACTIVE=prod` e crie o arquivo `application-prod.yml` correspondente.

---

## Como Rodar o Projeto

### Opção 1 — Pelo IntelliJ IDEA (recomendado)

1. Abra o projeto no IntelliJ: **File → Open** → selecione a pasta raiz do projeto
2. Aguarde o IntelliJ indexar o projeto e baixar as dependências Maven
3. Abra o arquivo `src/br/com/codegroup/PortfolioManagerApplication.java`
4. Clique no ícone ▶ verde na margem esquerda, ao lado do método `main`
5. Selecione **Run 'PortfolioManagerApplication'**

### Opção 2 — Pelo painel Maven do IntelliJ

1. Abra o painel **Maven** (lado direito da tela)
2. Expanda **portfolio-manager → Lifecycle**
3. Dê duplo clique em **spring-boot:run**

### O que esperar no console ao subir

```
Flyway ... Successfully applied 1 migration to schema "public"
Tomcat started on port 8080
Started PortfolioManagerApplication in X.XXX seconds
```

A aplicação estará disponível em: **`http://localhost:8080/api`**

---

## Documentação da API — Swagger UI

Acesse no navegador:

```
http://localhost:8080/api/swagger-ui.html
```

### Autenticação

A API utiliza **HTTP Basic Authentication** com usuário e senha fixos (hardcoded em memória):

| Campo | Valor |
|-------|-------|
| Username | `admin` |
| Password | `admin123` |

**Como autenticar no Swagger UI:**

1. Acesse `http://localhost:8080/api/swagger-ui.html`
2. Clique no botão **Authorize** (canto superior direito, ícone de cadeado)
3. Informe `admin` / `admin123`
4. Clique em **Authorize** e depois **Close**

Todos os endpoints estarão liberados para uso.

---

## Endpoints Disponíveis

### Membros — `/api/members`

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/members` | Criar membro |
| `GET` | `/members` | Listar todos os membros |
| `GET` | `/members/{id}` | Buscar membro por ID |

**Atribuições possíveis (`role`):** `FUNCIONARIO`, `GERENTE`

### Projetos — `/api/projects`

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/projects` | Criar projeto |
| `GET` | `/projects` | Listar projetos (com paginação e filtros) |
| `GET` | `/projects/{id}` | Buscar projeto por ID |
| `PUT` | `/projects/{id}` | Atualizar projeto |
| `DELETE` | `/projects/{id}` | Excluir projeto |
| `PATCH` | `/projects/{id}/status` | Atualizar status do projeto |
| `POST` | `/projects/{id}/members` | Adicionar membro ao projeto |
| `DELETE` | `/projects/{id}/members/{memberId}` | Remover membro do projeto |

**Filtros disponíveis na listagem:**
```
GET /api/projects?status=EM_ANALISE&name=sistema&page=0&size=10&sort=name,asc
```

**Sequência de status (ordem obrigatória):**
```
EM_ANALISE → ANALISE_REALIZADA → ANALISE_APROVADA → INICIADO → PLANEJADO → EM_ANDAMENTO → ENCERRADO
```
> `CANCELADO` pode ser aplicado a partir de qualquer status, exceto `ENCERRADO`.

### Relatório — `/api/reports`

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/reports/portfolio` | Relatório resumido do portfólio |

---

## Fluxo Básico de Uso

```bash
# 1. Criar um gerente
POST /api/members
{ "name": "Carlos Silva", "role": "GERENTE" }

# 2. Criar um funcionário
POST /api/members
{ "name": "Ana Souza", "role": "FUNCIONARIO" }

# 3. Criar um projeto (usando o ID do gerente)
POST /api/projects
{
  "name": "Sistema de RH",
  "startDate": "2026-01-01",
  "expectedEndDate": "2026-06-01",
  "totalBudget": 150000.00,
  "description": "Modernização do RH",
  "managerId": 1
}

# 4. Associar funcionário ao projeto
POST /api/projects/1/members
{ "memberId": 2 }

# 5. Avançar o status
PATCH /api/projects/1/status
{ "status": "ANALISE_REALIZADA" }

# 6. Ver o relatório
GET /api/reports/portfolio
```

---

## Regras de Negócio Principais

- **Risco dinâmico** — calculado automaticamente com base em orçamento e prazo:
  - `BAIXO`: orçamento ≤ R$ 100.000 **e** prazo ≤ 3 meses
  - `MEDIO`: orçamento entre R$ 100.001 e R$ 500.000 **ou** prazo entre 3 e 6 meses
  - `ALTO`: orçamento > R$ 500.000 **ou** prazo > 6 meses

- **Exclusão bloqueada** para projetos com status `INICIADO`, `EM_ANDAMENTO` ou `ENCERRADO`

- **Alocação de membros**: somente `FUNCIONARIO`; mínimo 1, máximo 10 por projeto

- **Limite de alocação**: um membro não pode estar em mais de 3 projetos ativos simultaneamente

---

## Rodando os Testes

### Pelo IntelliJ

1. Clique com botão direito na pasta `test`
2. Selecione **Run 'All Tests'**

### Pelo painel Maven

1. Abra o painel **Maven → Lifecycle**
2. Dê duplo clique em **test**

---

## Cobertura de Testes (JaCoCo)

O projeto exige **mínimo de 70% de cobertura de linhas** nas classes de serviço (`br.com.codegroup.service`).

### Como gerar e visualizar o relatório

1. No painel **Maven → Lifecycle**, dê duplo clique em **verify**
2. Aguarde o build terminar — se a cobertura estiver abaixo de 70%, o build falhará com `BUILD FAILURE`
3. Se bem-sucedido, abra o relatório HTML em:

```
target/site/jacoco/index.html
```

Navegue até essa pasta no Windows Explorer e abra o arquivo no navegador. O relatório detalha a cobertura por pacote, por classe e por linha.

### Resultado atual

| Pacote | Cobertura |
|--------|-----------|
| `service` | **85%** ✅ |
| `entity` | **94%** ✅ |
| `enums` | **98%** ✅ |

---

## Estrutura do Projeto

```
technical-test-code-group/
├── src/
│   └── br/com/codegroup/
│       ├── PortfolioManagerApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java
│       │   └── OpenApiConfig.java
│       ├── controller/
│       │   ├── MemberController.java
│       │   ├── ProjectController.java
│       │   └── ReportController.java
│       ├── dto/
│       │   ├── MemberRequestDto.java
│       │   ├── MemberResponseDto.java
│       │   ├── ProjectRequestDto.java
│       │   ├── ProjectResponseDto.java
│       │   ├── StatusUpdateRequestDto.java
│       │   ├── MemberIdRequestDto.java
│       │   └── PortfolioReportDto.java
│       ├── entity/
│       │   ├── Member.java
│       │   └── Project.java
│       ├── enums/
│       │   ├── MemberRole.java
│       │   ├── ProjectStatus.java
│       │   └── RiskLevel.java
│       ├── exception/
│       │   ├── BusinessException.java
│       │   ├── ResourceNotFoundException.java
│       │   └── GlobalExceptionHandler.java
│       ├── mapper/
│       │   ├── MemberMapper.java
│       │   └── ProjectMapper.java
│       ├── repository/
│       │   ├── MemberRepository.java
│       │   └── ProjectRepository.java
│       └── service/
│           ├── MemberService.java
│           ├── ProjectService.java
│           └── ReportService.java
├── test/
│   └── br/com/codegroup/
│       ├── entity/
│       │   └── ProjectRiskTest.java
│       ├── enums/
│       │   └── ProjectStatusTest.java
│       └── service/
│           ├── MemberServiceTest.java
│           ├── ProjectServiceTest.java
│           └── ReportServiceTest.java
├── resources/
│   ├── application.yml
│   ├── application-local.yml
│   └── db/migration/
│       └── V1__create_initial_schema.sql
├── pom.xml
└── .gitignore
```

---

## Tratamento de Erros

Todos os erros são retornados no formato padronizado:

```json
{
  "timestamp": "2026-05-23T12:00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Descrição do erro de negócio"
}
```

| Código | Situação |
|--------|----------|
| `400` | Dados de entrada inválidos (validação) |
| `401` | Não autenticado |
| `404` | Recurso não encontrado |
| `422` | Violação de regra de negócio |
| `500` | Erro interno inesperado |
