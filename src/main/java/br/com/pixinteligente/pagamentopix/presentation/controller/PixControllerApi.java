package br.com.pixinteligente.pagamentopix.presentation.controller;

import br.com.pixinteligente.pagamentopix.presentation.dto.PixRequest;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Contrato da API REST de Pagamento Pix.
 *
 * Princípio SOLID aplicado: Interface Segregation (ISP)
 * Separa o contrato (esta interface) da implementação (PixController),
 * permitindo que as anotações do Swagger fiquem concentradas aqui —
 * mantendo o Controller limpo e focado apenas na implementação.
 *
 * Benefícios:
 * - O Controller não precisa conhecer detalhes de documentação.
 * - É possível ter múltiplas implementações do contrato (ex: versão v2).
 * - Facilita testes — basta mockar a interface.
 *
 * @author Golbery Santos
 */
@Tag(name = "Pix", description = "Endpoints para transferências e consultas Pix")
@RequestMapping("/api/pix")
public interface PixControllerApi {

    /**
     * Processa uma nova transferência Pix.
     */
    @PostMapping("/transferir")
    @Operation(
        summary = "Processar transferência Pix",
        description = "Processa uma transferência Pix com validação de limite e análise de fraude via Gemini"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transferência processada com sucesso"),
        @ApiResponse(responseCode = "422", description = "Limite Pix excedido"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<PixResponse> transferir(@Valid @RequestBody PixRequest request);

    /**
     * Busca uma transação Pix pelo ID.
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar transação por ID",
        description = "Retorna os detalhes de uma transação Pix pelo seu identificador único"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transação encontrada"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<PixResponse> buscarPorId(@PathVariable Long id);

    /**
     * Lista todas as transações Pix registradas.
     */
    @GetMapping
    @Operation(
        summary = "Listar todas as transações",
        description = "Retorna todas as transações Pix registradas no sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<PixResponse>> listarTodas();

    /**
     * Lista transações Pix por CPF de origem.
     */
    @GetMapping("/cpf")
    @Operation(
        summary = "Listar transações por CPF",
        description = "Retorna todas as transações Pix de um determinado CPF de origem"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<PixResponse>> listarPorCpfOrigem(@RequestParam String cpfOrigem);
}