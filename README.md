# 🧰 TecnoGuard — Sistema de Manutenção Corretiva e Preventiva

## 🚀 Visão Geral

O TecnoGuard é um sistema de manutenção industrial que gerencia o ciclo de vida de Ordens de Serviço (Work Orders) —
desde a abertura até o fechamento — com controle de usuários, papéis (roles), rastreabilidade e segurança baseada em
JWT.

> 🎯 Objetivo: Garantir rastreabilidade, responsabilidade e qualidade das manutenções corretivas e preventivas.
---

## 🧩 Estrutura do Projeto

    src/
    ├─ main/java/com/github/tecnoguard
    │   ├─ TecnoguardApplication.java
    │   ├─ application/
    │   │   ├─ dto/
    │   │   │   ├─ auth/
    │   │   │   │   ├─ request/
    │   │   │   │   └─ response/
    │   │   │   ├─ user/
    │   │   │   │   ├─ request/
    │   │   │   │   └─ response/
    │   │   │   └─ workorder/
    │   │   │       ├─ request/
    │   │   │       └─ response/
    │   │   └─ mappers/
    │   │         ├─ users/
    │   │         └─ workorder/
    │   ├─ core/
    │   │   ├─ exceptions/
    │   │   └─ shared/
    │   ├─ domain/
    │   │   ├─ enums/
    │   │   ├─ models/
    │   │   ├─ service/
    │   │   └─ shared/
    │   │       ├─ models/
    │   │       └─ service/
    │   └─ infrastructure/
    │       ├─ config/
    │       ├─ persistence/
    │       ├─ security/
    │       ├─ service/
    │       └─ web/
    │           ├─ controller/
    │           └─ handler/
    └─ test/java/com/github/tecnoguard

### Arquitetura em camadas:

- **Controller**: pontos de entrada (rotas, requests/responses)
- **Service**: lógica de negócio e validações
- **Repository**: persistência (H2 no dev, PostgreSQL no prod)
- **Entity (Model)**: domínio e estado
- **DTO / Mapper**: transporte de dados e conversão

---

## ⚙️ Tecnologias Principais

| **Categoria**  | **Tecnologia**               |
|----------------|------------------------------|
| Framework      | Spring Boot 3                |
| Segurança      | Spring Security + JWT        | 
| Banco de Dados | H2 (dev) / PostgreSQL (prod) |
| ORM            | Spring Data JPA              |
| Testes         | JUnit 5 + MockMvc            |
| Documentação   | OpenAPI (Swagger)            |
| Build          | Maven                        |
| Logs           | SLF4J / Logback              |

---

## 👥 Perfis e Permissões

| Role       | Descrição           | Acesso                         |
|------------|---------------------|--------------------------------|
| ADMIN      | Administração total | CRUD completo (usuários e OS)  |
| SUPERVISOR | Valida e fecha OS   | leitura, aprovação, fechamento |
| PLANNER    | Planeja manutenções | criação, agendamento           |
| TECHNICIAN | Executa OS          | start/complete                 |
| OPERATOR   | Reporta falhas      | cria OS corretivas             |

---

## 🧱 Entidades Principais

### 🧍‍♂️ User

Campos:

> id, username, password, name, email, role, active,
> createdAt, updatedAt, lastLogin

- Implementa UserDetails
- role define permissões
- active controla login
- password criptografado (BCrypt)

---

### ⚙️ WorkOrder

Campos:
> id, description, equipment, client, type,
> status, assignedTechnician, scheduleDate,
> completedAt, cancelReason, workOrderLog, createdAt, updatedAt

Fluxo de estados:
> OPEN → SCHEDULED → IN_PROGRESS → COMPLETED
> ↘ CANCELLED

Cada transição é validada pelo service conforme regras do PO.

---

### 📜 SystemLog

Campos:
> id, timestamp, actorUsername, action, targetType, targetId, details

Registra ações críticas (criação/edição de usuários, alterações de OS, login/logout).

---

## 🔐 Segurança

- Autenticação via JWT (Bearer Token)
- Rotas públicas: /api/auth/**, /swagger-ui/**, /h2-console/**
- Roles e permissões via SecurityConfig
- Senhas com BCryptPasswordEncoder
- Tokens contêm: username, role, exp

Exemplo de header:
> Authorization: Bearer <token>
---

## 📘 Endpoints Principais

### Auth

| Método | Endpoint         | Descrição                           | Role  |
|--------|------------------|-------------------------------------|-------|
| POST   | /api/auth/login  | Retorna JWT                         | todos |
| GET    | /api/auth/whoami | Retorna info do usuário autenticado | todos | 

### Users

| Método | Endpoint                   | Descrição                    | Role              |
|--------|----------------------------|------------------------------|-------------------|
| GET    | /api/users                 | Retorna lista todos usuários | ADMIN, SUPERVISOR |
| GET    | /api/users/{id}            | Retorna info do usuário      | ADMIN, SUPERVISOR |
| POST   | /api/users                 | Registra usuário             | ADMIN             |
| PATCH  | /api/users/{id}            | Atualiza dados do usuário    | ADMIN, SUPERVISOR |
| PATCH  | /api/users/password/{id}   | Muda senha do usuário        | SELF, ADMIN       |
| PATCH  | /api/users/deactivate/{id} | Desativa usuário             | ADMIN             |
| PATCH  | /api/users/reactivate/{id} | Reativa usuário              | ADMIN             |

### WorkOrders
| Método | Endpoint                       | Descrição                  | Role                           |
|--------|--------------------------------|----------------------------|--------------------------------|
| GET    | /api/workorders                | Retorna lista de todas OS  | Todos                          |
| GET    | /api/workorders/{id}           | Retorna info da OS         | Todos                          |
| POST   | /api/workorders                | Cria nova OS               | OPERATOR, PLANNER              |
| PATCH  | /api/workorders/assign/{id}    | Agenda técnico para OS     | PLANNER, ADMIN                 |
| PATCH  | /api/workorders/start/{id}     | Inicia OS                  | TECHNICIAN, ADMIN              |                
| PATCH  | /api/workorders/complete/{id}  | Finaliza a OS              | TECHNICIAN, SUPERVISOR, ADMIN  | 
| PATCH  | /api/workorders/cancel/{id}    | Cancela a OS               | PLANNER, SUPERVISOR, ADMIN     |      
---

## 🧪 Testes
- **Integração**: controllers com MockMvc e autenticação simulada.
- **Unitários**: services com regras de negócio isoladas.
- **Fluxos testados**:
  - Criação, atribuição, execução e cancelamento de OS. 
  - CRUD de usuários e troca de senha.
  - Login e registro com JWT.
- Cobertura de segurança (403/401) e erros de validação.

---

## 🧠 Design e Padrões Aplicados
- **DTOs + Mapper**: isolamento da API e do domínio.
- **Services finos**: validações de negócio e logs de auditoria.
- **Entities ricas**: métodos de estado (start(), complete(), etc.).
- **Auditoria**: *createdAt*, *updatedAt*, *lastLogin*, *createdBy* (em evolução).
- **Testes de integração completos**: garantem comportamento real do sistema.

---

## 🪜 Próximos Passos
1. Refinar auditoria (SystemLog completo).
2. Migrar workOrderLog → entidade WorkOrderNote.
3. Criar entidades Equipment e Client.
4. Melhorar documentação Swagger (exemplos e security scheme).
5. Docker Compose + PostgreSQL + Flyway (migrations).
6. Testes unitários adicionais para serviços.

## 🧭 Execução local (modo dev)
### Requisitos
- JDK 21+
- Maven 3.9+
- (opcional) Docker + PostgreSQL
### Rodar aplicação
> mvn spring-boot:run
### Testar com H2
- Acesse [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: 
  > jdbc:h2:mem:tecnoguard
- User: sa 
- Password: (vazio)

### Swagger (documentação)
Acesse [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🧱 Licença
Projeto para fins educacionais / acadêmicos.

Autor: [Eduardo B. Rauta](https://github.com/ebrauta) — Desenvolvido em Java + Spring Boot.
