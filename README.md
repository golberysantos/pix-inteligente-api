# Pix Inteligente API

API REST para transferências Pix com validação inteligente via Google Gemini.
Projeto desenvolvido como desafio de Padrões de Projeto do Bootcamp Santander 2026 - AI Java Back-end (DIO).

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
| **Strategy** | `ValidadorPix` — interface funcional com implementações `ValidadorLimiteDiurno` (R$ 10.000,00) e `ValidadorLimiteNoturno` (R$ 1.000,00), selecionadas em runtime pelo horário |
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
     │  Padrão Strategy
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
    │       ├── ValidadorLimiteDiurno.java
    │       ├── ValidadorLimiteNoturno.java
    │       └── ValidadorPix.java     ← @FunctionalInterface
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

## Autor

Desenvolvido como desafio de Padrões de Projeto do **Bootcamp Santander 2026 - AI Java Back-end** na plataforma [DIO](https://www.dio.me) por Golbery Santos.