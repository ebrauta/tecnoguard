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
    │   │   ├─ dtos/
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
    │   │   ├─ dto/
    │   │   ├─ exceptions/
    │   │   ├─ models/
    │   │   ├─ service/
    │   │   └─ utils/
    │   ├─ domain/
    │   │   ├─ enums/
    │   │   ├─ models/
    │   │   └─ service/
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

**Propósito:** Representa qualquer pessoa que acessa o sistema — operadores, técnicos, planejadores, supervisores e administradores.

Controla autenticação, autorização e rastreabilidade de ações no sistema.

- Campos:
    - String username
    - String password
    - String name
    - String email
    - UserRole role
    - LocalDateTime lastLogin
- Métodos:
    - Collection<? extends GrantedAuthority> getAuthorities (herdado do Spring Security UserDetails)
    - Boolean isAccountNonExpired (herdado do Spring Security UserDetails)
    - Boolean isAccountNonLocked (herdado do Spring Security UserDetails)
    - Boolean isCredentialsNonExpired (herdado do Spring Security UserDetails)
    - Boolean isEnabled (herdado do Spring Security UserDetails)
    - Boolean isActive (herdado do Spring Security UserDetails)
    - void changePassword(PasswordEncorder encoder, String curPass, String newPass)
    - void validatePassword(PasswordEncoder encoder, String currentPassword)
    - void deactivate
    - void reactivate
- Regras:
    - **Criação de usuário**
        - Apenas **ADMIN** pode criar novos usuários.
        - Campos obrigatórios: username, password, email, role.
        - username e email devem ser **únicos**.
        - Senha deve ser armazenada **criptografada (BCrypt)**.
        - Usuário novo inicia com active = true.
        - Campos createdAt e createdBy são preenchidos automaticamente (auditoria).
    - **Edição de usuário**
        - Apenas **ADMIN** pode editar dados de outro usuário.
        - O próprio usuário pode alterar **somente sua senha** e dados pessoais não críticos (ex: nome).
        - Alteração de role deve gerar **registro de auditoria** (SystemLog).
    - **Desativação**
        - Usuários não são excluídos fisicamente; use active = false.
        - A desativação bloqueia login e ações no sistema.
        - Todas as referências (OS criadas, notas etc.) permanecem.
    - **Autenticação**
        - Login por username e password.
        - Bloqueia usuários inactive.
        - Após login, grava lastLogin e emite token JWT com claims (username, role).
    - **Autorização**
        - O role (UserRole) define o escopo de acesso:
            - **ADMIN** → total;
            - **SUPERVISOR** → validação e acompanhamento;
            - **PLANNER** → criação e planejamento de OS;
            - **TECHNICIAN** → execução de OS;
            - **OPERATOR** → abertura de OS corretiva.
- Restrições:
    - username e email não podem se repetir.
    - password nunca é retornado nas respostas da API.
    - Nenhum usuário pode alterar o próprio role.
    - Auditoria (createdBy, updatedBy) deve estar sempre preenchida.

### ⚙️ WorkOrder

**Propósito:** Representa o **registro formal de uma manutenção** — desde a solicitação até o fechamento.

É o **agregado raiz do domínio** de manutenção.

Estados ( WOStatus ):  OPEN ⇒ SCHEDULED ⇒ IN_PROGRESS ⇒ COMPLETED ⇒ CANCELLED

Tipos ( WOType ) : CORRETIVE, PREVENTIVE, PREDITIVE

Prioridade ( WOPriority ): HIGH ⇒ MEDIUM ⇒ LOW

- Campos:
    - String description
    - String equipment
    - String client
    - List<WorkOrderNote> notes (ManyToOne)
    - WOStatus status
    - WOType type
    - String assignedTechnician
    - LocalDate scheduleDate
    - LocalDateTime openingDate
    - LocalDateTime closingDate
    - LocalDateTime cancelDate
    - String cancelReason
    - WOPriority priority
- Métodos:
    - void create
    - void assign(String technician, LocalDate date)
    - void start
    - void complete(String log)
    - void cancel(String reason)
- Regras:
    - **Criação**
        - Pode ser criada por **OPERATOR**, **PLANNER**, **SUPERVISOR** ou **ADMIN**.
        - Campos obrigatórios: description, equipment, type, priority.
        - Ao criar, o status inicial é sempre OPEN.
        - openingDate é preenchida automaticamente.
        - O createdBy (usuário logado) é gravado na auditoria.
        - Operator só cria tipo CORRETIVE
    - **Agendamento**
        - Somente **PLANNER** ou **ADMIN** podem agendar uma OS.
        - Transição de status: OPEN → SCHEDULED.
        - É necessário informar assignedTechnician e data de agendamento.
        - Data de agendamento não pode ser menor que a data atual.
    - **Execução**
        - Apenas o **TECHNICIAN designado** pode iniciar a OS.
        - Transição: SCHEDULED → IN_PROGRESS.
        - O sistema grava startDate.
    - **Conclusão**
        - Apenas o **TECHNICIAN designado** ou o **SUPERVISOR** podem concluir.
        - Transição: IN_PROGRESS → COMPLETED.
        - Deve ter pelo menos uma WorkOrderNote de encerramento.
        - Registra closingDate.
    - **Cancelamento**
        - Pode ser feito por **SUPERVISOR** ou **ADMIN**.
        - Transições válidas:
            - OPEN → CANCELLED
            - SCHEDULED → CANCELLED
            - IN_PROGRESS → CANCELLED
        - Deve registrar cancelDate e cancelReason.
    - **Rastreabilidade**
        - Cada mudança de status deve gerar uma entrada em WorkOrderNotes.
        - O relacionamento com WorkOrderNote forma o histórico completo da OS.
- Restrições:
    - Nenhum campo essencial (description, status, priority, type) pode ser nulo.
    - Transições de status só são válidas conforme as regras de permissão.
    - Uma OS CANCELLED ou COMPLETED não pode ser modificada.

### WorkOrderNote

**Propósito:** Registrar observações, atualizações e comunicações relacionadas à execução de uma OS.

Funciona como um “log de atividade” técnico e administrativo.

Relacionamento: 1 OS → Várias Notes (1:N - id ⇒ workorder_id)

- Campos:
    - Workorder workOrder (ManyToOne - JoinColumn: workorder_id)
    - String message
    - String author
- Métodos:
  nenhum método interno

- Regras:
    - **Criação**
        - Pode ser criada por qualquer usuário **envolvido na OS** (criou, planejou, executou ou supervisionou).
        - Campos obrigatórios: message.
        - O workOrder deve existir e estar **ativa** (!cancelled).
        - createdBy e createdAt são preenchidos automaticamente.
    - **Visibilidade**
        - Você só vê os Logs da Os específica no id
    - **Vinculação**
        - Uma nota **sempre** pertence a uma OS (ManyToOne).
        - Ao deletar uma OS, as notas associadas devem ser removidas em cascata (ou marcadas inativas).
- Restrições:
    - message não pode ser vazio.
    - workOrder não pode ser nulo.

### BaseEntity*

**Propósito:** Base de auditoria de tabelas, serve para auditoria de User, WorkOrder e WorkorderNote.

- Campos:
    - Long id (anotation: Id e GeneraredValue - jakarta.persistence)
    - LocalDateTime createdAt (anotation: CreatedAt - springframework.data.anotation)
    - LocalDateTime updatedAt (anotation: UpdatedAt - springframework.data.anotation)
    - Boolean active
- Métodos:
  Não há métodos

- Regras:
  Sem regras

- Restrições:
  Sem restrições


### AuditableEntity*

**Propósito:** Complementa a auditoria do BaseEntity. É uma extensão da mesma. Como os dados dependem do User, essa auditoria é utilizada somente nas entidades diferentes de User. ****

- Campos:
    - User createdBy (anotation: CreatedBy - springframework.data.anotation)
    - User updatedBy (anotation: UpdatedBy - springframework.data.anotation)
- Métodos:
  Não há métodos

- Regras:
  Sem regras

- Restrições:
  Sem restrições


### 📜 SystemLog*

**Propósito:** Log de registro de auditoria, detalha o que tá sendo feito com todo o sistema.

- Campos:
    - Long id (anotation: Id e GeneraredValue - jakarta.persistence)
    - LocalDateTime timestamp
    - String actorUsername
    - String action
    - String targetType
    - Long targetId
    - String details
- Métodos:
  Não há métodos

- Regras:
  Sem regras

- Restrições:
  Sem restrições

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
| Método | Endpoint                      | Descrição                  | Role                           |
|--------|-------------------------------|----------------------------|--------------------------------|
| GET    | /api/workorders               | Retorna lista de todas OS  | Todos                          |
| GET    | /api/workorders/{id}          | Retorna info da OS         | Todos                          |
| GET    | /api/workorders/log/{id}      | Retorna as anotações da OS | Todos                          |
| POST   | /api/workorders               | Cria nova OS               | OPERATOR, PLANNER              |
| POST   | /api/workorders/log           | Cria nova anotação na OS   | OPERATOR, PLANNER              |
| PATCH  | /api/workorders/assign/{id}   | Agenda técnico para OS     | PLANNER, ADMIN                 |
| PATCH  | /api/workorders/start/{id}    | Inicia OS                  | TECHNICIAN, ADMIN              |                
| PATCH  | /api/workorders/complete/{id} | Finaliza a OS              | TECHNICIAN, SUPERVISOR, ADMIN  | 
| PATCH  | /api/workorders/cancel/{id}   | Cancela a OS               | PLANNER, SUPERVISOR, ADMIN     |      

### SystemLog

| Método | Endpoint  | Descrição                | Role  |
|--------|-----------|--------------------------|-------|
| GET    | /api/logs | Retorna o log do sistema | ADMIN |



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
1. Refinar auditoria.
2. Criar entidades Technician, Equipment e Client.
3. Melhorar documentação Swagger (exemplos e security scheme).
4. Docker Compose + PostgreSQL + Flyway (migrations).
5. Testes unitários adicionais para serviços.

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
