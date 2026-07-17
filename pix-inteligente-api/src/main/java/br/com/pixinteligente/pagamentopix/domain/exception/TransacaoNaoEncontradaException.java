package br.com.pixinteligente.pagamentopix.domain.exception;

/**
 * Exceção lançada quando uma transação Pix não é encontrada
 * pelo ID informado na requisição.
 *
 * Herda de PixException — será capturada pelo @ControllerAdvice
 * e retornará HTTP 404 (Not Found) ao cliente da API.
 *
 * Exemplo de uso:
 * throw new TransacaoNaoEncontradaException(42L);
 *
 * @author Golbery Santos
 */
public class TransacaoNaoEncontradaException extends PixException {

    /**
     * @param id ID da transação que não foi encontrada.
     */
    public TransacaoNaoEncontradaException(Long id) {
        super(
            String.format("Transação Pix não encontrada para o ID: %d", id),
            404
        );
    }
}
