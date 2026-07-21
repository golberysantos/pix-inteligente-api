package br.com.pixinteligente.pagamentopix.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta rica para erros de limite excedido (HTTP 422).
 *
 * Usa Record do Java 21 — imutável, limpo e sem boilerplate.
 *
 * @param status          Código HTTP do erro (422).
 * @param mensagem        Mensagem explicativa legível.
 * @param valorSolicitado O valor monetário que excedeu o limite.
 * @param limitePermitido O limite máximo configurado para o período ativo.
 * @param timestamp       Data/hora em que o erro ocorreu.
 *
 * @author Golbery Santos
 */
public record LimiteExcedidoResponse(
        int status,
        String mensagem,
        BigDecimal valorSolicitado,
        BigDecimal limitePermitido,
        LocalDateTime timestamp
) {

    /**
     * Cria uma resposta estruturada de limite excedido com timestamp atual.
     */
    public static LimiteExcedidoResponse of(int status, String mensagem, BigDecimal valorSolicitado, BigDecimal limitePermitido) {
        return new LimiteExcedidoResponse(status, mensagem, valorSolicitado, limitePermitido, LocalDateTime.now());
    }
}
