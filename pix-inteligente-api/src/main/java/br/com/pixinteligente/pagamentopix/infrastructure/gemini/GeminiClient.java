package br.com.pixinteligente.pagamentopix.infrastructure.gemini;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Cliente responsável pela comunicação com a API do Gemini.
 *
 * Padrão aplicado: Singleton (Creational)
 * Gerenciado pelo Spring como @Component — uma única instância
 * compartilhada por toda a aplicação, evitando múltiplas
 * conexões desnecessárias com o serviço externo.
 *
 * Implementação atual: simulação (stub) para desenvolvimento.
 * Quando a API Key estiver disponível, substituir o corpo do
 * método analisar() pela chamada real ao endpoint do Gemini
 * via RestClient ou WebClient.
 *
 * Endpoint real (futuro):
 * POST https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
 *
 * System Prompt sugerido para produção:
 * "Você é um analista de fraudes do sistema Pix.
 *  Analise a transação e responda APENAS em JSON com os campos:
 *  analise (String) e suspeita (boolean)."
 *
 * @author Golbery Santos
 */
@Component
public class GeminiClient {

    /**
     * Limite a partir do qual o stub considera a transação suspeita.
     * Simula o comportamento da IA para valores acima de R$ 5.000,00.
     */
    private static final BigDecimal LIMITE_SUSPEITO = new BigDecimal("5000.00");

    /**
     * Analisa uma transação Pix e retorna a avaliação do Gemini.
     *
     * Implementação atual: stub que simula a resposta da IA.
     * - Valores acima de R$ 5.000,00 → suspeita = true
     * - Valores até R$ 5.000,00     → suspeita = false
     *
     * @param transacao Transação a ser analisada pelo Gemini.
     * @return GeminiResponse com a análise e indicador de suspeita.
     */
    public GeminiResponse analisar(Transacao transacao) {

        // Stub — simula a resposta do Gemini em desenvolvimento
        // Substituir pelo código abaixo quando a API Key estiver disponível:
        //
        // String prompt = buildPrompt(transacao);
        // String resposta = restClient.post()
        //         .uri(geminiApiUrl)
        //         .header("Authorization", "Bearer " + apiKey)
        //         .body(Map.of("contents", List.of(Map.of("parts",
        //                 List.of(Map.of("text", prompt))))))
        //         .retrieve()
        //         .body(String.class);
        // return parseResposta(resposta);

        boolean suspeita = transacao.getValor()
                .compareTo(LIMITE_SUSPEITO) > 0;

        String analise = suspeita
                ? String.format(
                    "Transacao de R$ %s de %s para %s classificada como SUSPEITA. " +
                    "Valor acima do limiar de monitoramento de R$ %s. " +
                    "Recomenda-se revisao manual antes da liberacao.",
                    transacao.getValor(),
                    transacao.getCpfOrigem(),
                    transacao.getCpfDestino(),
                    LIMITE_SUSPEITO)
                : String.format(
                    "Transacao de R$ %s de %s para %s analisada pelo Gemini. " +
                    "Nenhum indicador de fraude detectado.",
                    transacao.getValor(),
                    transacao.getCpfOrigem(),
                    transacao.getCpfDestino());

        return new GeminiResponse(analise, suspeita);
    }

    /**
     * Monta o System Prompt para o Gemini.
     * Método preparado para uso futuro com a API Key real.
     *
     * @param transacao Transação a ser descrita no prompt.
     * @return Prompt formatado para envio ao Gemini.
     */
    private String buildPrompt(Transacao transacao) {
        return """
                Você é um analista de fraudes do sistema Pix do Santander.
                Analise a transação abaixo e responda APENAS em JSON com os campos:
                - analise: String com sua avaliação detalhada
                - suspeita: boolean indicando se a transação é suspeita
                
                Transação:
                - CPF Origem: %s
                - CPF Destino: %s
                - Valor: R$ %s
                - Data/Hora: %s
                """.formatted(
                transacao.getCpfOrigem(),
                transacao.getCpfDestino(),
                transacao.getValor(),
                transacao.getCriadoEm()
        );
    }
}
