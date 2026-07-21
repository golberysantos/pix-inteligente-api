package br.com.pixinteligente.pagamentopix.domain.strategy;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Estratégia de validação do limite noturno para transações Pix.
 *
 * Padrão aplicado: Strategy (Behavioral)
 * Implementa o contrato ValidadorPixComPeriodo com a regra de negócio
 * específica para o período noturno (20h às 06h).
 *
 * Regra de negócio:
 * - Período: 20:00 às 06:00
 * - Limite máximo permitido: R$ 1.000,00
 *
 * @author Golbery Santos
 */
public class ValidadorLimiteNoturno implements ValidadorPixComPeriodo {

    private static final BigDecimal LIMITE_NOTURNO = new BigDecimal("1000.00");

    @Override
    public boolean validar(Transacao transacao) {
        return transacao.getValor().compareTo(LIMITE_NOTURNO) <= 0;
    }

    @Override
    public boolean aceitaHorario(LocalDateTime agora) {
        int hora = agora.getHour();
        return hora >= 20 || hora < 6;
    }

    @Override
    public BigDecimal getLimite() {
        return LIMITE_NOTURNO;
    }
}
