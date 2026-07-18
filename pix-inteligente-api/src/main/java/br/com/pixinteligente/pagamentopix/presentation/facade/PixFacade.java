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
 * Fachada que simplifica a interação entre o Controller e as camadas internas.
 *
 * Padrão aplicado: Facade (Structural)
 * Esconde a complexidade do fluxo completo de uma transferência Pix:
 * 1. Processar a transação via PixService (regras de negócio + Strategy)
 * 2. Analisar com IA via GeminiAdapter (Adapter Pattern)
 * 3. Notificar via ServicoNotificacao (Template Method Pattern)
 *
 * O Controller não precisa conhecer nenhum desses detalhes —
 * ele apenas chama a Facade e recebe o resultado pronto.
 *
 * Sem a Facade, o Controller precisaria orquestrar tudo isso,
 * violando o princípio de responsabilidade única.
 *
 * @author Golbery Santos
 */
@Component
public class PixFacade {

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
    public PixResponse processarTransferencia(PixRequest request) {

        // 1. Processa a transação — Strategy em ação (diurno/noturno)
        Transacao transacao = pixService.processarPix(
                request.cpfOrigem(),
                request.cpfDestino(),
                request.valor()
        );

        // 2. Analisa com IA — Adapter em ação
        Transacao transacaoAnalisada = geminiAdapter.analisarEAplicar(transacao);

        // 3. Notifica o resultado — Template Method em ação
        servicoNotificacao.notificar(transacaoAnalisada);

        // 4. Converte para DTO e retorna
        return PixResponse.fromDomain(transacaoAnalisada);
    }

    /**
     * Busca uma transação Pix pelo ID.
     *
     * @param id Identificador da transação.
     * @return PixResponse com os dados da transação.
     */
    public PixResponse buscarPorId(Long id) {
        return PixResponse.fromDomain(pixService.buscarPorId(id));
    }

    /**
     * Lista todas as transações Pix registradas.
     *
     * @return Lista de PixResponse com todas as transações.
     */
    public List<PixResponse> listarTodas() {
        return pixService.listarTodas()
                .stream()
                .map(PixResponse::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * Lista transações Pix por CPF de origem.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return Lista de PixResponse do CPF informado.
     */
    public List<PixResponse> listarPorCpfOrigem(String cpfOrigem) {
        return pixService.listarPorCpfOrigem(cpfOrigem)
                .stream()
                .map(PixResponse::fromDomain)
                .collect(Collectors.toList());
    }
}
