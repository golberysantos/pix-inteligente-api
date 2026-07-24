package br.com.pixinteligente.pagamentopix.presentation.notification;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe abstrata base para serviços de notificação.
 *
 * Padrão aplicado: Template Method (Behavioral)
 * Define o esqueleto do algoritmo de notificação — o fluxo fixo
 * que toda notificação deve seguir — e delega os detalhes
 * de cada canal (log, e-mail, SMS) para as subclasses.
 *
 * Fluxo fixo (imutável):
 * 1. Registrar log antes do envio
 * 2. Executar o envio específico do canal (abstrato — subclasse define)
 * 3. Registrar log após o envio
 *
 * Benefício: garante que toda notificação — independente do canal —
 * sempre registra log antes e depois do envio, sem repetição de código.
 *
 * @author Golbery Santos
 */
public abstract class ServicoNotificacao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Método template — define o fluxo fixo de notificação.
     * Não pode ser sobrescrito pelas subclasses (final).
     *
     * @param transacao Transação Pix a ser notificada.
     */
    public final void notificar(Transacao transacao) {
        log.info("[NOTIFICACAO] Iniciando notificacao para transacao ID: {}",
                transacao.getId());

        // Delega o envio específico para a subclasse
        enviar(transacao);

        log.info("[NOTIFICACAO] Notificacao concluida para transacao ID: {} | Status: {}",
                transacao.getId(), transacao.getStatus());
    }

    /**
     * Método abstrato — cada subclasse define como notifica.
     * Template Method: o esqueleto chama este método no momento certo.
     *
     * @param transacao Transação Pix a ser notificada.
     */
    protected abstract void enviar(Transacao transacao);
}
