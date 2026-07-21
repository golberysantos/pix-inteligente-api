package br.com.pixinteligente.pagamentopix.domain.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ValidadorPixComPeriodo extends ValidadorPix {
    boolean aceitaHorario(LocalDateTime agora);
    BigDecimal getLimite();
}
