package br.com.pixinteligente.pagamentopix.domain.strategy;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Estratégia de validação do limite diurno para transações Pix.
 *
 * Padrão aplicado: Strategy (Behavioral)
 * Implementa o contrato ValidadorPixComPeriodo com a regra de negócio
 * específica para o período diurno (06h às 20h).
 *
 * Regra de negócio:
 * - Período: 06:00 às 20:00
 * - Limite máximo permitido: R$ 10.000,00
 *
 * @author Golbery Santos
 */
public class ValidadorLimiteDiurno implements ValidadorPixComPeriodo {

    private static final BigDecimal LIMITE_DIURNO = new BigDecimal("10000.00");

    @Override
    public boolean validar(Transacao transacao) {
        return transacao.getValor().compareTo(LIMITE_DIURNO) <= 0;
    }

    @Override
    public boolean aceitaHorario(LocalDateTime agora) {
        int hora = agora.getHour();
        return hora >= 6 && hora < 20;
    }

    @Override
    public BigDecimal getLimite() {
        return LIMITE_DIURNO;
    }
}
