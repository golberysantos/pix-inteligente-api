# Pix Inteligente API

[![Java 21](https://img.shields.io/badge/JAVA-21-E56E25?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/SPRING%20BOOT-3.4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Google Gemini](https://img.shields.io/badge/GOOGLE%20GEMINI-AI-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)](https://ai.google.dev/)
[![SOLID Principles](https://img.shields.io/badge/PRINCIPLES-SOLID-232F3E?style=for-the-badge&logo=codeforces&logoColor=white)](https://en.wikipedia.org/wiki/SOLID)
[![Clean Architecture](https://img.shields.io/badge/ARCHITECTURE-CLEAN%20%2B%20SCREAMING-007EC6?style=for-the-badge)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
[![Bootcamp DIO](https://img.shields.io/badge/BOOTCAMP-SANTANDER%202026-E20E17?style=for-the-badge&logo=santander&logoColor=white)](https://www.dio.me/)
[![JUnit 5](https://img.shields.io/badge/JUNIT%205-TESTING-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/MOCKITO-MOCKING-C52F24?style=for-the-badge&logo=mockito&logoColor=white)](https://site.mockito.org/)
[![AAA Pattern](https://img.shields.io/badge/PATTERN-ARRANGE%20ACT%20ASSERT-00599C?style=for-the-badge)](https://wiki.c2.com/?ArrangeActAssert)
[![Swagger](https://img.shields.io/badge/OPENAPI-SWAGGER-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)
[![Lombok](https://img.shields.io/badge/LOMBOK-REDESIGNED-BC022C?style=for-the-badge&logo=projectlombok&logoColor=white)](https://projectlombok.org/)
[![Apache Maven](https://img.shields.io/badge/APACHE%20MAVEN-BUILD-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![GoF Design Patterns](https://img.shields.io/badge/DESIGN%20PATTERNS-GoF-4B0082?style=for-the-badge)](https://en.wikipedia.org/wiki/Design_Patterns)

API REST para transferências Pix com validação inteligente via Google Gemini.
Projeto desenvolvido como desafio de projeto Design Patterns com Java: Dos Clássicos (GoF) ao Spring Framework.

O **Pix Inteligente** é um sistema de transferências financeiras (Pix) com um motor de validação baseado em Inteligência Artificial. A aplicação analisa padrões de transações em tempo real utilizando a **API do Gemini** (Google) para detectar potenciais fraudes ou anomalias antes de aprovar a transferência.

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Padrões de Projeto Aplicados](#padrões-de-projeto-aplicados)
- [Arquitetura](#arquitetura)
- [Diagrama de Fluxo](#diagrama-de-fluxo)
- [Tecnologias](#tecnologias)
- [Como Executar](#como-executar)
- [Endpoints](#endpoints)
- [Cenários de Teste](#cenários-de-teste)
- [Estrutura de Pacotes](#estrutura-de-pacotes)

---

## Sobre o Projeto

O **Pix Inteligente API** simula um sistema bancário de transferências Pix com três camadas de inteligência:

1. **Validação por horário** — limites diferentes para período diurno e noturno.
2. **Análise de fraude via IA** — integração com Google Gemini para classificar transações suspeitas.
3. **Notificação automática** — registro estruturado de todas as operações com alertas para transações suspeitas.

---

## Padrões de Projeto Aplicados

### Creational (Criacionais)

| Padrão | Onde foi aplicado |
|--------|------------------|
| **Singleton** | `GeminiClient` — gerenciado pelo Spring como `@Component`, uma única instância compartilhada por toda a aplicação |
| **Builder** | `Transacao` — construção fluente do objeto de domínio sem construtores longos. Implementação manual (sem Lombok) para tornar o padrão explícito |

### Structural (Estruturais)

| Padrão | Onde foi aplicado |
|--------|------------------|
| **Facade** | `PixFacade` — simplifica o fluxo completo (Service + Gemini + Notificação) em uma única chamada para o Controller |
| **Adapter** | `PixRepositoryAdapter` — converte `Transacao` (domínio) ↔ `TransacaoEntity` (JPA). `GeminiAdapter` — converte a resposta da IA em ações do domínio |

### Behavioral (Comportamentais)

| Padrão | Onde foi aplicado |
|--------|------------------|
| **Strategy** | `ValidadorPix` / `ValidadorPixComPeriodo` — estratégias polimórficas de limite (`ValidadorLimiteDiurno` e `ValidadorLimiteNoturno`) selecionadas dinamicamente via `SeletorDeValidador` sem acoplamento rígido, respeitando o princípio Open/Closed (OCP) |
| **Template Method** | `ServicoNotificacao` — define o esqueleto fixo de notificação (log antes + envio + log depois). `NotificacaoLog` implementa o canal específico |

---

## Arquitetura

O projeto segue os princípios da **Clean Architecture** com pacotes organizados por feature:

```
Presentation Layer  ──►  Domain Layer  ──►  Infrastructure Layer
  (Controller)              (Service)           (Persistence + Gemini)
  (Facade)                  (Strategy)
  (DTO)                     (Repository Port)
  (Notification)            (Exception)
```

Regra fundamental: **o domínio não conhece nenhum framework**. Sem `@Service`, `@Entity` ou `@Autowired` nas classes de domínio. A injeção é feita via `PixServiceConfig`.

---

## Diagrama de Fluxo

```
Cliente (Swagger UI / Postman)
         |
         | POST /api/pix/transferir
         ▼
┌─────────────────────┐
│    PixController    │  Recebe a requisição e delega à Facade
│   @RestController   │  Sem regra de negócio
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│     PixFacade       │  Padrão Facade — orquestra o fluxo completo
│    @Component       │
└──┬──────────┬───────┘
   │          │
   │          │
   ▼          ▼
┌──────────┐  ┌──────────────────┐
│PixService│  │  GeminiAdapter   │  Padrão Adapter
│  (domínio│  │  @Component      │
│   puro)  │  └────────┬─────────┘
└────┬─────┘           │
     │                 ▼
     │          ┌──────────────────┐
     │          │  GeminiClient    │  Padrão Singleton
     │          │  @Component      │
     │          │  (stub/real API) │
     │          └──────────────────┘
     │
     │  Padrão Strategy (via SeletorDeValidador)
     ├──► ValidadorLimiteDiurno  (06h-20h → R$ 10.000,00)
     └──► ValidadorLimiteNoturno (20h-06h → R$  1.000,00)
          │
          │ LimiteExcedidoException
          ▼
   ┌──────────────────────┐
   │ GlobalExceptionHandler│  @RestControllerAdvice
   │  HTTP 422 + JSON     │
   └──────────────────────┘

     Persistência (Ports and Adapters)
          │
          ▼
┌─────────────────────┐
│ PixRepositoryAdapter│  Padrão Adapter — converte domínio ↔ JPA
│    @Component       │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│SpringDataPixRepository  Spring Data JPA
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   H2 (in-memory)    │  Banco de dados em memória
│  tabela: transacoes │
└─────────────────────┘

     Notificação (Template Method)
          │
          ▼
┌─────────────────────┐
│ ServicoNotificacao  │  Classe abstrata — esqueleto fixo
│  (Template Method)  │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   NotificacaoLog    │  Implementação concreta do canal
│   INFO / WARN       │  WARN para transações SUSPEITAS
└─────────────────────┘
```

---

## Tecnologias

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.1 | Framework principal |
| Spring Web MVC | 3.4.1 | API REST |
| Spring Data JPA | 3.4.1 | Persistência |
| Spring Security | 3.4.1 | Autenticação HTTP Basic |
| H2 Database | Runtime | Banco em memória |
| Springdoc OpenAPI | 2.8.0 | Swagger UI |
| Lombok | Latest | Redução de boilerplate |
| JUnit 5 | 3.4.1 | Testes unitários |
| Mockito | 3.4.1 | Mocks nos testes |
| Maven | 3.x | Gerenciamento de dependências |

---

## Como Executar

### Pré-requisitos

- Java 21 ou superior
- Maven 3.x

### Clonar o repositório

```bash
git clone https://github.com/seu-usuario/pix-inteligente-api.git
cd pix-inteligente-api
```

### Executar a aplicação

```bash
mvn spring-boot:run
```

### Executar os testes

```bash
mvn test
```

### Acessar a aplicação

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| H2 Console | http://localhost:8080/h2-console |
| API Base URL | http://localhost:8080/api/pix |

### Credenciais

| Usuário | Senha | Perfil |
|---------|-------|--------|
| admin | admin123 | ADMIN |
| user | user123 | USER |

### H2 Console

```
JDBC URL : jdbc:h2:mem:pixdb
Username : sa
Password : (vazio)
```

### Integração com Gemini (opcional)

Para ativar a integração real com o Google Gemini, edite o `application.properties`:

```properties
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
gemini.api.key=SUA_API_KEY_AQUI
```

E substitua o stub no `GeminiClient.java` pelo código de chamada HTTP comentado no arquivo.

---

## Endpoints

### POST /api/pix/transferir

Processa uma transferência Pix com validação de limite e análise de fraude via Gemini.

**Request body:**
```json
{
  "cpfOrigem": "111.111.111-11",
  "cpfDestino": "222.222.222-22",
  "valor": 500.00
}
```

**Validações:**
- `cpfOrigem` e `cpfDestino`: obrigatórios, formato `000.000.000-00`
- `valor`: obrigatório, mínimo R$ 0,01

**Responses:**

`201 Created` — transferência aprovada:
```json
{
  "id": 1,
  "cpfOrigem": "111.111.111-11",
  "cpfDestino": "222.222.222-22",
  "valor": 500.00,
  "status": "APROVADA",
  "analiseIa": null,
  "criadoEm": "2026-07-18T19:24:58"
}
```

`201 Created` — transferência marcada como suspeita pelo Gemini:
```json
{
  "id": 2,
  "cpfOrigem": "333.333.333-33",
  "cpfDestino": "444.444.444-44",
  "valor": 6000.00,
  "status": "SUSPEITA",
  "analiseIa": "Transacao de R$ 6000.00 classificada como SUSPEITA. Valor acima do limiar de monitoramento de R$ 5000.00. Recomenda-se revisao manual antes da liberacao.",
  "criadoEm": "2026-07-18T19:27:05"
}
```

`422 Unprocessable Entity` — limite excedido:
```json
{
  "status": 422,
  "mensagem": "Limite Pix excedido. Valor solicitado: R$ 15000.00 | Limite permitido: R$ 10000.00",
  "valorSolicitado": 15000.00,
  "limitePermitido": 10000.00,
  "timestamp": "2026-07-18T19:29:30"
}
```

`400 Bad Request` — dados inválidos:
```json
{
  "status": 400,
  "mensagem": "CPF de origem deve estar no formato 000.000.000-00",
  "timestamp": "2026-07-18T19:30:00"
}
```

---

### GET /api/pix/{id}

Busca uma transação pelo ID.

`200 OK` | `404 Not Found`

---

### GET /api/pix

Lista todas as transações.

`200 OK`

---

### GET /api/pix/cpf?cpfOrigem={cpf}

Lista transações por CPF de origem.

`200 OK`

---

## Cenários de Teste

| Cenário | Valor | Horário | Status esperado | HTTP |
|---------|-------|---------|-----------------|------|
| Transferência normal | R$ 500,00 | Qualquer | APROVADA | 201 |
| Análise de fraude | R$ 6.000,00 | Qualquer | SUSPEITA | 201 |
| Limite diurno excedido | R$ 15.000,00 | 06h-20h | — | 422 |
| Limite noturno excedido | R$ 1.500,00 | 20h-06h | — | 422 |
| CPF inválido | Qualquer | Qualquer | — | 400 |
| Sem autenticação | Qualquer | Qualquer | — | 401 |
| ID inexistente | — | — | — | 404 |

---

## Estrutura de Pacotes

```
br.com.pixinteligente
├── config
│   ├── GlobalExceptionHandler.java   ← @RestControllerAdvice
│   ├── PixServiceConfig.java         ← beans do domínio
│   ├── SecurityConfig.java           ← HTTP Basic Authentication
│   └── SwaggerConfig.java            ← Springdoc OpenAPI
│
└── pagamentopix
    ├── domain                        ← DOMÍNIO PURO (sem Spring/JPA)
    │   ├── exception
    │   │   ├── LimiteExcedidoException.java
    │   │   ├── PixException.java
    │   │   └── TransacaoNaoEncontradaException.java
    │   ├── model
    │   │   └── Transacao.java        ← Builder Pattern
    │   ├── repository
    │   │   └── PixRepository.java    ← Porta de saída (interface pura)
    │   ├── service
    │   │   └── PixService.java       ← Casos de uso + Strategy
    │   └── strategy
    │       ├── SeletorDeValidador.java  ← Padrão Strategy Selector
    │       ├── ValidadorLimiteDiurno.java
    │       ├── ValidadorLimiteNoturno.java
    │       ├── ValidadorPix.java     ← @FunctionalInterface
    │       └── ValidadorPixComPeriodo.java
    │
    ├── infrastructure                ← DETALHES DE TECNOLOGIA
    │   ├── adapter
    │   │   └── PixRepositoryAdapter.java  ← Adapter Pattern
    │   ├── gemini
    │   │   ├── GeminiAdapter.java    ← Adapter Pattern
    │   │   ├── GeminiClient.java     ← Singleton Pattern
    │   │   └── GeminiResponse.java   ← Record Java 21
    │   └── persistence
    │       ├── SpringDataPixRepository.java
    │       └── TransacaoEntity.java  ← @Entity JPA
    │
    └── presentation                  ← CAMADA REST / MVC
        ├── controller
        │   └── PixController.java    ← @RestController
        ├── dto
        │   ├── ErroResponse.java     ← Record Java 21
        │   ├── LimiteExcedidoResponse.java ← Record Java 21
        │   ├── PixRequest.java       ← Record Java 21 + Bean Validation
        │   └── PixResponse.java      ← Record Java 21
        ├── facade
        │   └── PixFacade.java        ← Facade Pattern
        └── notification
            ├── NotificacaoLog.java   ← Template Method (concreto)
            └── ServicoNotificacao.java ← Template Method (abstrato)
```

---

## Cobertura de Testes

| Classe | Testes | Resultado |
|--------|--------|-----------|
| `PixServiceTest` | 8 | ✅ |
| `ValidadorLimiteDiurnoTest` | 6 | ✅ |
| `PixControllerTest` | 7 | ✅ |
| **Total** | **21** | **✅ 100%** |

---


## Profiles de Ambiente

O projeto utiliza o sistema de profiles do Spring Boot para separar as configurações por ambiente:

| Arquivo | Profile | Banco | Uso |
|---------|---------|-------|-----|
| `application.properties` | default | H2 em memória | Base comum — roda sem configuração |
| `application-dev.properties` | dev | PostgreSQL | Desenvolvimento local com PostgreSQL |
| `application-prod.properties` | prod | PostgreSQL | Produção via variáveis de ambiente |

### Como ativar um profile

```bash
# Via Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Via variável de ambiente
export SPRING_PROFILES_ACTIVE=dev

# Via IDE (VM arguments)
-Dspring.profiles.active=dev
```

### Para testar com PostgreSQL (profile dev)

1. Crie o banco de dados:
```sql
CREATE DATABASE pixdb;
```

2. Ative o profile `dev`:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

O `application-dev.properties` já está configurado com as credenciais padrão do PostgreSQL (`postgres/postgres`). Ajuste se necessário.

---

## Boas Praticas de Segurança

> **Nota:** O `application-dev.properties` deste projeto contém credenciais explícitas para facilitar a avaliação e os testes do desafio. Essa foi uma decisão consciente para que qualquer avaliador consiga rodar o projeto com PostgreSQL sem esforço de configuração.

**Em um projeto real de produção, nunca faça isso.** A estrutura correta é:

### 1. Nunca exponha credenciais no repositório

```properties
# ERRADO — nunca faça isso em producao
spring.datasource.password=minha_senha_secreta

# CORRETO — use variaveis de ambiente
spring.datasource.password=${DB_PASSWORD}
```

### 2. Use variáveis de ambiente no servidor

```bash
# Linux / Mac
export DB_URL=jdbc:postgresql://localhost:5432/pixdb
export DB_USERNAME=postgres
export DB_PASSWORD=senha_segura
export GEMINI_API_KEY=sua_chave_gemini

# Windows
set DB_URL=jdbc:postgresql://localhost:5432/pixdb
set DB_USERNAME=postgres
set DB_PASSWORD=senha_segura
set GEMINI_API_KEY=sua_chave_gemini
```

### 3. Use .gitignore para proteger arquivos sensíveis

```gitignore
# Arquivos com credenciais locais
application-dev.properties
application-local.properties
.env
*.env
```

### 4. A estrutura deste projeto já está pronta

O `application.properties` principal usa a notação `${VARIAVEL:valor_padrao}` em todas as propriedades sensíveis. Para migrar para produção segura, basta:

1. Definir as variáveis de ambiente no servidor.
2. Ativar o profile `prod`.
3. Remover ou ignorar o `application-dev.properties`.

Nenhuma linha de código precisa ser alterada.

---

## Autor

Desenvolvido como desafio de Padrões de Projeto do **Bootcamp Santander 2026 - AI Java Back-end** na plataforma [DIO](https://www.dio.me) por Golbery Santos.