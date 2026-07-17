# Proposta: Sistema de Transferências Pix com Validação Inteligente via Gemini

## Padrões de Projeto aplicados
### Creational — Singleton
O cliente do Gemini API será gerenciado como Singleton pelo Spring (@Service) — uma única instância compartilhada por toda a aplicação.

### Creational — Builder
A entidade Transacao será construída via Builder — evitando construtores longos e tornando a criação de objetos legível.

### Structural — Facade
Uma classe PixFacade vai simplificar o fluxo completo — validar, processar e consultar IA — escondendo a complexidade das camadas internas do Controller.

###  Structural — Adapter
Um GeminiAdapter vai adaptar a resposta bruta da API do Gemini para o formato interno da aplicação.

### Behavioral — Strategy
Diferentes estratégias de validação do Pix — limite diurno, limite noturno, conta VIP — implementadas via interface funcional.

### Behavioral — Template Method
A classe abstrata ServicoNotificacao define o fluxo de notificação — log + envio — e cada subclasse implementa o canal específico.

## Testes
Tecnologias usadas:
- JUnit 5,
- Mockito,
- Spring Boot Test
- Spring Security Tes

Tipos de testes:
- Testes unitários — testam cada classe isoladamente, sem subir o contexto do Spring:
PixServiceTest — valida as regras de negócio; 
ValidadorLimiteDiurnoTest — valida a estratégia de limite;
GeminiAdapterTest — valida a conversão da resposta da IA.

Testes de integração — sobem o contexto do Spring e testam o fluxo completo:
PixControllerTest — testa os endpoints REST com MockMvc.
