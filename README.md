# ⚡ Pix Inteligente API

[![Java Version](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20Screaming-blue?style=for-the-badge)](https://blog.cleancoder.com/)

O **Pix Inteligente** é um sistema de transferências financeiras (Pix) com um motor de validação baseado em Inteligência Artificial. A aplicação analisa padrões de transações em tempo real utilizando a **API do Gemini** (Google) para detectar potenciais fraudes ou anomalias antes de aprovar a transferência.

Este projeto foi desenvolvido como um demonstrativo técnico, aplicando práticas de **Clean Architecture**, **Screaming Architecture**, **SOLID** e diversos **Design Patterns** de mercado em um ecossistema Java moderno.

---

## 🏗️ Decisões Arquiteturais

### 📁 Estrutura de Pacotes: *Package-by-Feature* + *Screaming Architecture*
Para evitar o acoplamento do MVC tradicional e a desorganização de projetos grandes, adotamos o padrão de **Pacotes por Funcionalidade (Package-by-Feature)** combinado com camadas isoladas de **Clean Architecture** dentro de cada módulo. 

A arquitetura do projeto "grita" o seu propósito de negócio (`pagamentopix`):

```text
br.com.pixinteligente	
├── config                     # Configurações globais (Security, Swagger, JPA, etc.)
├── pagamentopix               # Domínio/Módulo funcional de Pagamentos Pix
│   │
│   ├── domain                 # 1. CAMADA DE DOMÍNIO (100% agnóstica a frameworks/bancos)
│   │   ├── model              # Entidades e Value Objects puras (ex: Transacao)
│   │   ├── repository         # Interfaces e Portas de Saída (Contracts/Ports)
│   │   ├── exception          # Exceções exclusivas de regras de negócio
│   │   ├── strategy           # Contratos e lógicas de validação de limites
│   │   └── service            # Casos de uso (Orquestradores de regras de negócio)
│   │
│   ├── infrastructure         # 2. CAMADA DE INFRAESTRUTURA (Detalhes de implementação)
│   │   ├── persistence        # Entidades JPA (@Entity) e Interfaces Spring Data
│   │   ├── adapter            # Implementação concreta do repositório (Domain <=> JPA)
│   │   └── gemini             # Integração com a API do Gemini (HTTP Client & Adaptador)
│   │
│   └── presentation           # 3. CAMADA DE APRESENTAÇÃO / MVC
│       ├── controller         # Endpoints HTTP RESTful (@RestController)
│       ├── dto                # Validação de payload de entrada/saída (Bean Validation)
│       ├── facade             # Orquestrador do fluxo Controller -> Service/IA
│       └── notification       # Subcamada de alertas e notificações
│
└── PixInteligenteApiApplication.java

```

---

## 🎨 Design Patterns Aplicados

Abaixo estão detalhados os padrões de projeto utilizados e suas respectivas responsabilidades:

### 🧩 Padrões Criacionais

* **Singleton:** O cliente da API do Gemini é gerenciado como um Singleton gerenciado pelo Spring IoC (`@Service`), garantindo o reuso de conexões e eficiência de recursos.
* **Builder:** A entidade de domínio `Transacao` utiliza o padrão Builder (via Lombok) para garantir uma construção fluida, imutável e livre de construtores com parâmetros excessivos.

### 📐 Padrões Estruturais

* **Facade (`PixFacade`):** Simplifica o fluxo de execução para a camada de apresentação. O controller interage apenas com a Facade, que orquestra as validações de regras de negócio, a persistência e a análise de inteligência artificial.
* **Adapter (`GeminiAdapter`):** Isola o domínio de contratos externos de terceiros. Ele mapeia os dados brutos recebidos da API do Gemini (infraestrutura) para o modelo de análise interno (`ResultadoAnalise`) aceito pelo domínio.

### ⚙️ Padrões Comportamentais

* **Strategy (`ValidadorPixStrategy`):** Implementa políticas dinâmicas de validação de limite do Pix (Ex: Diurno, Noturno, VIP). Permite adicionar novas regras de limite sem alterar o código existente (**Open/Closed Principle**).
* **Template Method (`ServicoNotificacao`):** Define o esqueleto do algoritmo de envio de alertas (Log + Disparo) em uma classe abstrata, permitindo que subclasses específicas (`EmailNotificacao`, `SmsNotificacao`) implementem o envio físico sem alterar o fluxo principal.

---

## 🛠️ Tecnologias e Ecossistema

* **Java 21** (Uso de Virtual Threads e Records para máxima performance)
* **Spring Boot 3.2.x** (Web, Data JPA, Security, Validation)
* **H2 Database** (Banco de dados em memória para facilidade de testes)
* **Spring Doc / OpenAPI (Swagger)** para documentação interativa das APIs
* **Lombok** para redução de código boilerplate

---

## 🧪 Estrutura de Testes (Princípio AAA)

Garantimos a confiabilidade do sistema utilizando a filosofia **AAA (Arrange, Act, Assert)** com testes unitários rápidos e testes de integração de ponta a ponta:

```text
src/test/java/br/com/pixinteligente/pagamentopix
├── domain
│   ├── service
│   │   └── PixServiceTest.java         # Teste Unitário (Regras de negócio puras com Mockito)
│   └── strategy
│       └── ValidadorLimiteDiurnoTest.java # Teste Unitário (Verificação isolada de regras de limite)
├── infrastructure
│   └── gemini
│       └── GeminiAdapterTest.java      # Teste Unitário (Validação de mapeamento de payloads da IA)
└── presentation
    └── controller
        └── PixControllerTest.java      # Teste de Integração (MockMvc + Spring Security Context)

```

---

## 🚀 Como Executar o Projeto

1. Clone o repositório:
```bash
git clone [https://github.com/seu-usuario/pix-inteligente.git](https://github.com/seu-usuario/pix-inteligente.git)

```


2. Configure sua chave de API do Gemini nas variáveis de ambiente:
```bash
export GEMINI_API_KEY="sua-chave-api-aqui"

```


3. Execute o projeto usando o Maven:
```bash
mvn spring-boot:run

```


4. Acesse a documentação do Swagger UI em:
`http://localhost:8080/swagger-ui.html`

---

Desenvolvido por Golbery Santos  🚀
