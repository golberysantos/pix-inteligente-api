package br.com.pixinteligente.pagamentopix.domain.strategy;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;

import java.math.BigDecimal;

/**
 * Estratégia de validação do limite noturno para transações Pix.
 *
 * Padrão aplicado: Strategy (Behavioral)
 * Implementa o contrato ValidadorPix com a regra de negócio
 * específica para o período noturno (20h às 06h).
 *
 * Regra de negócio:
 * - Período: 20:00 às 06:00
 * - Limite máximo permitido: R$ 1.000,00
 *
 * @author Golbery Santos
 */
public class ValidadorLimiteNoturno implements ValidadorPix {

    /**
     * Limite máximo permitido para transações Pix no período noturno.
     * Valor reduzido como medida de segurança contra fraudes noturnas.
     */
    private static final BigDecimal LIMITE_NOTURNO = new BigDecimal("1000.00");

    /**
     * Valida se o valor da transação está dentro do limite noturno permitido.
     *
     * @param transacao Transação a ser validada.
     * @return true se o valor for menor ou igual a R$ 1.000,00.
     */
    @Override
    public boolean validar(Transacao transacao) {
        return transacao.getValor().compareTo(LIMITE_NOTURNO) <= 0;
    }

    /**
     * Retorna o limite máximo permitido por esta estratégia.
     * Utilizado pelo serviço para compor a mensagem de erro.
     *
     * @return Limite noturno em BigDecimal.
     */
    public BigDecimal getLimite() {
        return LIMITE_NOTURNO;
    }
}
