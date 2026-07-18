package br.com.pixinteligente.pagamentopix.presentation.dto;

import java.time.LocalDateTime;

/**
 * DTO de resposta padronizada para erros da API.
 *
 * Usa Record do Java 21 — imutável e sem boilerplate.
 * Retornado pelo GlobalExceptionHandler em todos os casos de erro,
 * garantindo que o cliente receba uma resposta JSON.
 *
 * Exemplo de resposta:
 * {
 *   "status": 422,
 *   "mensagem": "Limite Pix excedido. Valor solicitado: R$ 1500.00 | Limite permitido: R$ 1000.00",
 *   "timestamp": "2026-06-13T14:30:00"
 * }
 *
 * @author Golbery Santos
 */
public record ErroResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp
) {

    /**
     * Cria um ErroResponse com o timestamp atual.
     *
     * @param status   Código HTTP do erro.
     * @param mensagem Mensagem de negócio clara para o cliente.
     * @return ErroResponse preenchido com o timestamp do momento do erro.
     */
    public static ErroResponse of(int status, String mensagem) {
        return new ErroResponse(status, mensagem, LocalDateTime.now());
    }
}
