package br.com.pixinteligente.pagamentopix.presentation.dto;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída para respostas de operações Pix.
 *
 * Usa Record do Java 21 — imutável e sem boilerplate.
 * Expõe apenas os dados necessários para o cliente da API,
 * sem vazar detalhes internos do domínio ou da infraestrutura.
 *
 * O método estático fromDomain() converte o modelo de domínio
 * para este DTO de forma limpa e centralizada.
 *
 * @author Golbery Santos
 */
public record PixResponse(

        Long id,
        String cpfOrigem,
        String cpfDestino,
        BigDecimal valor,
        StatusTransacao status,
        String analiseIa,
        LocalDateTime criadoEm
) {

    /**
     * Converte um modelo de domínio Transacao para o DTO de resposta.
     *
     * Centraliza a conversão — o Controller não precisa conhecer
     * os detalhes do modelo de domínio.
     *
     * @param transacao Modelo de domínio a ser convertido.
     * @return DTO de resposta equivalente.
     */
    public static PixResponse fromDomain(Transacao transacao) {
        return new PixResponse(
                transacao.getId(),
                transacao.getCpfOrigem(),
                transacao.getCpfDestino(),
                transacao.getValor(),
                transacao.getStatus(),
                transacao.getAnaliseIa(),
                transacao.getCriadoEm()
        );
    }
}
