package br.com.pixinteligente.pagamentopix.presentation.notification;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;
import org.springframework.stereotype.Component;

/**
 * Implementação de notificação via log do sistema.
 *
 * Padrão aplicado: Template Method (Behavioral)
 * Implementa o método abstrato enviar() definido em ServicoNotificacao.
 * O fluxo de log antes e após o envio é garantido pela classe mãe —
 * esta classe define apenas o que é específico do canal de log.
 *
 * Em produção, outras implementações podem ser adicionadas:
 * - NotificacaoEmail extends ServicoNotificacao
 * - NotificacaoSms extends ServicoNotificacao
 * - NotificacaoPush extends ServicoNotificacao
 * Todas herdarão o fluxo de log automático sem repetir código.
 *
 * @author Golbery Santos
 */
@Component
public class NotificacaoLog extends ServicoNotificacao {

    /**
     * Implementação específica do canal de log.
     * Registra detalhes da transação de forma estruturada,
     * com mensagem diferenciada para transações suspeitas.
     *
     * @param transacao Transação Pix a ser notificada.
     */
    @Override
    protected void enviar(Transacao transacao) {
        if (StatusTransacao.SUSPEITA.equals(transacao.getStatus())) {
            log.warn(
                "[PIX][SUSPEITA] Transacao ID: {} | Valor: R$ {} | Origem: {} | Destino: {} | Analise IA: {}",
                transacao.getId(),
                transacao.getValor(),
                transacao.getCpfOrigem(),
                transacao.getCpfDestino(),
                transacao.getAnaliseIa()
            );
        } else {
            log.info(
                "[PIX][{}] Transacao ID: {} | Valor: R$ {} | Origem: {} | Destino: {}",
                transacao.getStatus(),
                transacao.getId(),
                transacao.getValor(),
                transacao.getCpfOrigem(),
                transacao.getCpfDestino()
            );
        }
    }
}
