package br.com.pixinteligente.pagamentopix.domain.strategy;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;

import java.math.BigDecimal;

/**
 * Estratégia de validação do limite diurno para transações Pix.
 *
 * Padrão aplicado: Strategy (Behavioral)
 * Implementa o contrato ValidadorPix com a regra de negócio
 * específica para o período diurno (06h às 20h).
 *
 * Regra de negócio:
 * - Período: 06:00 às 20:00
 * - Limite máximo permitido: R$ 10.000,00
 *
 * @author Golbery Santos
 */
public class ValidadorLimiteDiurno implements ValidadorPix {

    /**
     * Limite máximo permitido para transações Pix no período diurno.
     * BigDecimal garante precisão na comparação de valores monetários.
     */
    private static final BigDecimal LIMITE_DIURNO = new BigDecimal("10000.00");

    /**
     * Valida se o valor da transação está dentro do limite diurno permitido.
     *
     * @param transacao Transação a ser validada.
     * @return true se o valor for menor ou igual a R$ 10.000,00.
     */
    @Override
    public boolean validar(Transacao transacao) {
        return transacao.getValor().compareTo(LIMITE_DIURNO) <= 0;
    }

    /**
     * Retorna o limite máximo permitido por esta estratégia.
     * Utilizado pelo serviço para compor a mensagem de erro.
     *
     * @return Limite diurno em BigDecimal.
     */
    public BigDecimal getLimite() {
        return LIMITE_DIURNO;
    }
}
