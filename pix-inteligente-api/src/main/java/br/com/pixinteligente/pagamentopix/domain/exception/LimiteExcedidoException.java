package br.com.pixinteligente.pagamentopix.domain.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando o valor da transação Pix
 * ultrapassa o limite permitido pela estratégia de validação ativa.
 *
 * Herda de PixException — será capturada pelo @ControllerAdvice
 * e retornará HTTP 422 (Unprocessable Entity) ao cliente da API.
 *
 * Exemplo de uso:
 * throw new LimiteExcedidoException(new BigDecimal("1500.00"), new BigDecimal("1000.00"));
 *
 * @author Golbery Santos
 */
public class LimiteExcedidoException extends PixException {

    private final BigDecimal valorSolicitado;
    private final BigDecimal limitePermitido;

    /**
     * @param valorSolicitado Valor que o cliente tentou transferir.
     * @param limitePermitido Limite máximo permitido pela estratégia ativa.
     */
    public LimiteExcedidoException(BigDecimal valorSolicitado, BigDecimal limitePermitido) {
        super(
            String.format(
                "Limite Pix excedido. Valor solicitado: R$ %s | Limite permitido: R$ %s",
                valorSolicitado, limitePermitido
            ),
            422
        );
        this.valorSolicitado = valorSolicitado;
        this.limitePermitido = limitePermitido;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }

    public BigDecimal getLimitePermitido() {
        return limitePermitido;
    }
}
