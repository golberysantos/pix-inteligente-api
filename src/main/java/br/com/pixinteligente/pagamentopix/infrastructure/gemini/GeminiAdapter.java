package br.com.pixinteligente.pagamentopix.infrastructure.gemini;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.service.PixService;
import org.springframework.stereotype.Component;

/**
 * Adaptador que integra o Gemini ao domínio de Pagamento Pix.
 *
 * Padrão aplicado: Adapter (Structural)
 * Converte a resposta bruta do GeminiClient para ações
 * concretas no domínio — como marcar uma transação como SUSPEITA
 * via PixService quando a IA detectar risco.
 *
 * O domínio não conhece o Gemini diretamente.
 * Apenas este adaptador faz a ponte entre os dois.
 *
 * Fluxo:
 * PixFacade → GeminiAdapter → GeminiClient → GeminiResponse
 *                          ↓
 *                     PixService.marcarComoSuspeita()
 *
 * @author Golbery Santos
 */
@Component
public class GeminiAdapter {

    private final GeminiClient geminiClient;
    private final PixService pixService;

    public GeminiAdapter(GeminiClient geminiClient, PixService pixService) {
        this.geminiClient = geminiClient;
        this.pixService = pixService;
    }

    /**
     * Analisa uma transação Pix com o Gemini e aplica a ação correspondente.
     *
     * Se o Gemini considerar a transação suspeita, o status é atualizado
     * para SUSPEITA via PixService e a análise é persistida.
     * Caso contrário, a transação permanece com o status atual.
     *
     * @param transacao Transação a ser analisada.
     * @return Transação após a análise — com status e analiseIa atualizados se suspeita.
     */
    public Transacao analisarEAplicar(Transacao transacao) {
        GeminiResponse resposta = geminiClient.analisar(transacao);

        if (resposta.suspeita()) {
            return pixService.marcarComoSuspeita(
                    transacao.getId(),
                    resposta.analise());
        }

        return transacao;
    }
}
