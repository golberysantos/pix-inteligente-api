package br.com.pixinteligente.pagamentopix.presentation.controller;

import br.com.pixinteligente.pagamentopix.presentation.dto.PixRequest;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixResponse;
import br.com.pixinteligente.pagamentopix.presentation.facade.PixFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para operações de transferência Pix.
 *
 * Responsabilidade única: receber requisições HTTP, delegar
 * à PixFacade e retornar respostas HTTP adequadas.
 *
 * Não contém nenhuma regra de negócio — toda a lógica
 * é orquestrada pela PixFacade (Padrão Facade).
 *
 * Endpoints disponíveis:
 * POST /api/pix/transferir   → processa uma transferência Pix
 * GET  /api/pix/{id}         → busca uma transação pelo ID
 * GET  /api/pix              → lista todas as transações
 * GET  /api/pix/cpf          → lista transações por CPF de origem
 *
 * @author Golbery Santos
 */
@RestController
@RequestMapping("/api/pix")
@Tag(name = "Pix", description = "Endpoints para transferências e consultas Pix")
public class PixController {

    private final PixFacade pixFacade;

    public PixController(PixFacade pixFacade) {
        this.pixFacade = pixFacade;
    }

    /**
     * Processa uma nova transferência Pix.
     *
     * Fluxo interno (via Facade):
     * 1. Valida limite via Strategy (diurno/noturno)
     * 2. Analisa com Gemini via Adapter
     * 3. Notifica via Template Method
     *
     * @param request DTO com os dados da transferência.
     * @return HTTP 201 com a transação processada.
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
    public ResponseEntity<PixResponse> transferir(@Valid @RequestBody PixRequest request) {
        PixResponse response = pixFacade.processarTransferencia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca uma transação Pix pelo ID.
     *
     * @param id Identificador da transação.
     * @return HTTP 200 com a transação encontrada.
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
    public ResponseEntity<PixResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pixFacade.buscarPorId(id));
    }

    /**
     * Lista todas as transações Pix registradas.
     *
     * @return HTTP 200 com a lista de transações.
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
    public ResponseEntity<List<PixResponse>> listarTodas() {
        return ResponseEntity.ok(pixFacade.listarTodas());
    }

    /**
     * Lista transações Pix por CPF de origem.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return HTTP 200 com a lista de transações do CPF informado.
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
    public ResponseEntity<List<PixResponse>> listarPorCpfOrigem(
            @RequestParam String cpfOrigem) {
        return ResponseEntity.ok(pixFacade.listarPorCpfOrigem(cpfOrigem));
    }
}