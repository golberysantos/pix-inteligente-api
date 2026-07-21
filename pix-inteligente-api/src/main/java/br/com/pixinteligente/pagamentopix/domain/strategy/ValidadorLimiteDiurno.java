package br.com.pixinteligente.pagamentopix.domain.strategy;

import java.time.LocalDateTime;

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
public class ValidadorLimiteDiurno implements ValidadorPixComPeriodo {
    public boolean aceitaHorario(LocalDateTime agora) {
        int hora = agora.getHour();
        return hora >= 6 && hora < 20;
    }

	@Override
	public ValidadorPix getValidador() {
		// TODO Auto-generated method stub
		return null;
	}
}
