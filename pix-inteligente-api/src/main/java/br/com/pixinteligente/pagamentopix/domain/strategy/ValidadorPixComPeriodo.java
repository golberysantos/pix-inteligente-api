package br.com.pixinteligente.pagamentopix.domain.strategy;

import java.time.LocalDateTime;

public interface ValidadorPixComPeriodo {
    boolean aceitaHorario(LocalDateTime agora);
    ValidadorPix getValidador();
}
