package br.com.pixinteligente.pagamentopix.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * DTO de entrada para requisições de transferência Pix.
 *
 * Usa Record do Java 21 — imutável, sem boilerplate,
 * ideal para objetos de transferência de dados de entrada.
 *
 * Bean Validation garante que dados inválidos são rejeitados
 * antes de chegar ao domínio — o serviço nunca recebe dados sujos.
 *
 * @author Golbery Santos
 */
public record PixRequest(

        /**
         * CPF do titular da conta de origem.
         * Formato esperado: 000.000.000-00
         */
        @NotBlank(message = "CPF de origem é obrigatório")
        @Pattern(
            regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF de origem deve estar no formato 000.000.000-00"
        )
        String cpfOrigem,

        /**
         * CPF do titular da conta de destino.
         * Formato esperado: 000.000.000-00
         */
        @NotBlank(message = "CPF de destino é obrigatório")
        @Pattern(
            regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF de destino deve estar no formato 000.000.000-00"
        )
        String cpfDestino,

        /**
         * Valor da transferência.
         * Mínimo permitido: R$ 0,01
         * BigDecimal garante precisão em operações financeiras.
         */
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor mínimo para transferência Pix é R$ 0,01")
        BigDecimal valor
) {}
