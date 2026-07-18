package br.com.pixinteligente.config;

import br.com.pixinteligente.pagamentopix.domain.exception.PixException;
import br.com.pixinteligente.pagamentopix.presentation.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Interceptador global de exceções da API.
 *
 * Padrão aplicado: @ControllerAdvice (Spring)
 * Centraliza o tratamento de todas as exceções em um único lugar,
 * eliminando try-catch nos Controllers e garantindo respostas
 * JSON padronizadas via ErroResponse para todos os casos de erro.
 *
 * Hierarquia de captura:
 * 1. PixException e subclasses     → erros de negócio do domínio
 * 2. MethodArgumentNotValidException → erros de Bean Validation
 * 3. Exception                     → erros inesperados (fallback)
 *
 * @author Golbery Santos
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura todas as exceções de negócio do domínio Pix.
     *
     * PixException carrega o statusHttp embutido — não é necessário
     * um @ExceptionHandler separado para cada subclasse.
     *
     * Subclasses capturadas automaticamente:
     * - LimiteExcedidoException     → HTTP 422
     * - TransacaoNaoEncontradaException → HTTP 404
     *
     * @param ex Exceção de negócio lançada pelo domínio.
     * @return ResponseEntity com ErroResponse e status HTTP correto.
     */
    @ExceptionHandler(PixException.class)
    public ResponseEntity<ErroResponse> handlePixException(PixException ex) {
        return ResponseEntity
                .status(ex.getStatusHttp())
                .body(ErroResponse.of(ex.getStatusHttp(), ex.getMessage()));
    }

    /**
     * Captura erros de validação do Bean Validation (@Valid).
     *
     * Ocorre quando o PixRequest não passa nas validações —
     * CPF em formato inválido, valor nulo ou negativo, etc.
     * Consolida todos os erros de campo em uma única mensagem.
     *
     * @param ex Exceção lançada pelo Spring quando @Valid falha.
     * @return ResponseEntity HTTP 400 com todos os erros de validação.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErroResponse.of(HttpStatus.BAD_REQUEST.value(), mensagem));
    }

    /**
     * Fallback — captura qualquer exceção não tratada pelos handlers acima.
     *
     * Evita que stack traces internos vazem para o cliente da API.
     * Em produção, registrar o erro no sistema de monitoramento.
     *
     * @param ex Exceção inesperada.
     * @return ResponseEntity HTTP 500 com mensagem genérica.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno. Tente novamente mais tarde."));
    }
}
