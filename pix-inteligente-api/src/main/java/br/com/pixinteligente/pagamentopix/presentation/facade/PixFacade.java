package br.com.pixinteligente.pagamentopix.presentation.facade;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.service.PixService;
import br.com.pixinteligente.pagamentopix.infrastructure.gemini.GeminiAdapter;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixRequest;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixResponse;
import br.com.pixinteligente.pagamentopix.presentation.notification.ServicoNotificacao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação da porta PixFacadePort.
 *
 * Padrão aplicado: Facade (Structural)
 * Esconde a complexidade do fluxo completo de uma transferência Pix:
 * 1. Processar a transação via PixService (regras de negócio + Strategy)
 * 2. Analisar com IA via GeminiAdapter (Adapter Pattern)
 * 3. Notificar via ServicoNotificacao (Template Method Pattern)
 *
 * Princípio SOLID aplicado: Dependency Inversion (DIP)
 * Implementa PixFacadePort — o Controller depende da interface,
 * não desta classe concreta.
 *
 * @author Golbery Santos
 */
@Component
public class PixFacade implements PixFacadePort {

    private final PixService pixService;
    private final GeminiAdapter geminiAdapter;
    private final ServicoNotificacao servicoNotificacao;

    public PixFacade(PixService pixService,
                     GeminiAdapter geminiAdapter,
                     ServicoNotificacao servicoNotificacao) {
        this.pixService = pixService;
        this.geminiAdapter = geminiAdapter;
        this.servicoNotificacao = servicoNotificacao;
    }

    /**
     * Orquestra o fluxo completo de uma transferência Pix.
     *
     * Fluxo:
     * 1. Processa a transação via PixService — valida limite (Strategy) e persiste.
     * 2. Analisa com o Gemini via GeminiAdapter — marca como SUSPEITA se necessário.
     * 3. Notifica o resultado via ServicoNotificacao (Template Method).
     * 4. Converte o resultado final para PixResponse e retorna ao Controller.
     *
     * @param request DTO de entrada com os dados da transferência.
     * @return PixResponse com o resultado da operação.
     */
    @Override
    public PixResponse processarTransferencia(PixRequest request) {
        Transacao transacao = pixService.processarPix(
                request.cpfOrigem(),
                request.cpfDestino(),
                request.valor()
        );
        Transacao transacaoAnalisada = geminiAdapter.analisarEAplicar(transacao);
        servicoNotificacao.notificar(transacaoAnalisada);
        return PixResponse.fromDomain(transacaoAnalisada);
    }

    @Override
    public PixResponse buscarPorId(Long id) {
        return PixResponse.fromDomain(pixService.buscarPorId(id));
    }

    @Override
    public List<PixResponse> listarTodas() {
        return pixService.listarTodas()
                .stream()
                .map(PixResponse::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PixResponse> listarPorCpfOrigem(String cpfOrigem) {
        return pixService.listarPorCpfOrigem(cpfOrigem)
                .stream()
                .map(PixResponse::fromDomain)
                .collect(Collectors.toList());
    }
}