package br.com.pixinteligente.pagamentopix.domain.strategy;

import java.time.LocalDateTime;

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
public class ValidadorLimiteNoturno implements ValidadorPixComPeriodo {
    public boolean aceitaHorario(LocalDateTime agora) {
        int hora = agora.getHour();
        return hora >= 20 || hora < 6;
    }

	@Override
	public ValidadorPix getValidador() {
		// TODO Auto-generated method stub
		return null;
	}
}
