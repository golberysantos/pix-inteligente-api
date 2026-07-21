package br.com.pixinteligente.pagamentopix.presentation.controller;

import br.com.pixinteligente.pagamentopix.domain.exception.LimiteExcedidoException;
import br.com.pixinteligente.pagamentopix.domain.exception.TransacaoNaoEncontradaException;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixRequest;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixResponse;
import br.com.pixinteligente.pagamentopix.presentation.facade.PixFacadePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração do PixController.
 *
 * Sobe apenas a camada web (Controller + Security) sem o banco de dados.
 * A PixFacade é substituída por um Mock do Mockito.
 * Usa MockMvc para simular requisições HTTP.
 *
 * @MockitoBean substitui @MockBean a partir do Spring Boot 3.4.x.
 *
 * @author Golbery Santos
 */
@WebMvcTest(PixController.class)
class PixControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PixFacadePort pixFacade;

    // ==========================================
    // Testes de POST /api/pix/transferir
    // ==========================================

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar HTTP 201 quando transferencia Pix e processada com sucesso")
    void deveRetornar201QuandoTransferenciaProcessada() throws Exception {
        // Arrange
        PixRequest request = new PixRequest(
                "111.111.111-11",
                "222.222.222-22",
                new BigDecimal("500.00"));

        PixResponse response = new PixResponse(
                1L,
                "111.111.111-11",
                "222.222.222-22",
                new BigDecimal("500.00"),
                StatusTransacao.APROVADA,
                null,
                LocalDateTime.now());

        when(pixFacade.processarTransferencia(any(PixRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/pix/transferir")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APROVADA"))
                .andExpect(jsonPath("$.cpfOrigem").value("111.111.111-11"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar HTTP 422 quando limite Pix e excedido")
    void deveRetornar422QuandoLimiteExcedido() throws Exception {
        // Arrange
        PixRequest request = new PixRequest(
                "111.111.111-11",
                "222.222.222-22",
                new BigDecimal("15000.00"));

        when(pixFacade.processarTransferencia(any(PixRequest.class)))
                .thenThrow(new LimiteExcedidoException(
                        new BigDecimal("15000.00"),
                        new BigDecimal("10000.00")));

        // Act & Assert
        mockMvc.perform(post("/api/pix/transferir")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensagem").value(
                        "Limite Pix excedido. Valor solicitado: R$ 15000.00 | Limite permitido: R$ 10000.00"))
                .andExpect(jsonPath("$.valorSolicitado").value(15000.00))
                .andExpect(jsonPath("$.limitePermitido").value(10000.00));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar HTTP 400 quando CPF de origem e invalido")
    void deveRetornar400QuandoCpfOrigemInvalido() throws Exception {
        // Arrange — CPF sem formatação
        PixRequest request = new PixRequest(
                "11111111111",
                "222.222.222-22",
                new BigDecimal("500.00"));

        // Act & Assert
        mockMvc.perform(post("/api/pix/transferir")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Deve retornar HTTP 401 quando nao autenticado")
    void deveRetornar401QuandoNaoAutenticado() throws Exception {
        // Arrange
        PixRequest request = new PixRequest(
                "111.111.111-11",
                "222.222.222-22",
                new BigDecimal("500.00"));

        // Act & Assert
        mockMvc.perform(post("/api/pix/transferir")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // Testes de GET /api/pix/{id}
    // ==========================================

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar HTTP 200 quando transacao e encontrada pelo ID")
    void deveRetornar200QuandoTransacaoEncontrada() throws Exception {
        // Arrange
        PixResponse response = new PixResponse(
                1L,
                "111.111.111-11",
                "222.222.222-22",
                new BigDecimal("500.00"),
                StatusTransacao.APROVADA,
                null,
                LocalDateTime.now());

        when(pixFacade.buscarPorId(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/pix/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar HTTP 404 quando transacao nao e encontrada")
    void deveRetornar404QuandoTransacaoNaoEncontrada() throws Exception {
        // Arrange
        when(pixFacade.buscarPorId(99L))
                .thenThrow(new TransacaoNaoEncontradaException(99L));

        // Act & Assert
        mockMvc.perform(get("/api/pix/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value(
                        "Transação Pix não encontrada para o ID: 99"));
    }

    // ==========================================
    // Testes de GET /api/pix
    // ==========================================

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Deve retornar HTTP 200 com lista de transacoes")
    void deveRetornar200ComListaDeTransacoes() throws Exception {
        // Arrange
        List<PixResponse> lista = List.of(
                new PixResponse(1L, "111.111.111-11", "222.222.222-22",
                        new BigDecimal("500.00"), StatusTransacao.APROVADA,
                        null, LocalDateTime.now()),
                new PixResponse(2L, "333.333.333-33", "444.444.444-44",
                        new BigDecimal("6000.00"), StatusTransacao.SUSPEITA,
                        "Suspeita detectada", LocalDateTime.now())
        );

        when(pixFacade.listarTodas()).thenReturn(lista);

        // Act & Assert
        mockMvc.perform(get("/api/pix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("APROVADA"))
                .andExpect(jsonPath("$[1].status").value("SUSPEITA"));
    }
}