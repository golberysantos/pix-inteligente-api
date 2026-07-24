package br.com.pixinteligente.pagamentopix.presentation.controller;

import br.com.pixinteligente.pagamentopix.presentation.dto.PixRequest;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixResponse;
import br.com.pixinteligente.pagamentopix.presentation.facade.PixFacadePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implementação da API REST de Pagamento Pix.
 *
 * Princípios SOLID aplicados:
 *
 * S — Single Responsibility:
 * Responsabilidade única — receber requisições HTTP e delegar
 * à PixFacadePort. Sem regra de negócio, sem documentação Swagger.
 *
 * D — Dependency Inversion:
 * Depende da abstração PixFacadePort (interface), não da
 * implementação concreta PixFacade. O Spring injeta a implementação
 * correta em tempo de execução.
 *
 * I — Interface Segregation:
 * Implementa PixControllerApi — as anotações Swagger e os
 * mapeamentos de rota ficam na interface, mantendo esta classe
 * limpa e focada apenas na implementação dos métodos.
 *
 * @author Golbery Santos
 */
@RestController
public class PixController implements PixControllerApi {

    private final PixFacadePort pixFacade;

    /**
     * Injeção via construtor da abstração PixFacadePort.
     * O Spring resolve automaticamente a implementação concreta (PixFacade).
     *
     * @param pixFacade Porta da Facade — injetada pelo Spring.
     */
    public PixController(PixFacadePort pixFacade) {
        this.pixFacade = pixFacade;
    }

    @Override
    public ResponseEntity<PixResponse> transferir(@Valid @RequestBody PixRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pixFacade.processarTransferencia(request));
    }

    @Override
    public ResponseEntity<PixResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pixFacade.buscarPorId(id));
    }

    @Override
    public ResponseEntity<List<PixResponse>> listarTodas() {
        return ResponseEntity.ok(pixFacade.listarTodas());
    }

    @Override
    public ResponseEntity<List<PixResponse>> listarPorCpfOrigem(
            @RequestParam String cpfOrigem) {
        return ResponseEntity.ok(pixFacade.listarPorCpfOrigem(cpfOrigem));
    }
}