package br.com.pixinteligente.pagamentopix.domain.exception;

/**
 * Exceção base do domínio de Pagamento Pix.
 *
 * Todas as exceções de negócio do sistema herdam desta classe,
 * permitindo que o @ControllerAdvice capture qualquer exceção
 * do domínio Pix com um único @ExceptionHandler.
 *
 * Por ser Unchecked (extends RuntimeException), não obriga
 * o uso de try-catch em quem a lança — o @ControllerAdvice
 * centraliza o tratamento globalmente.
 *
 * @author Golbery Santos
 */
public class PixException extends RuntimeException {

    private final int statusHttp;

    /**
     * @param mensagem  Mensagem de negócio clara para o cliente da API.
     * @param statusHttp Código HTTP que será retornado na resposta.
     */
    public PixException(String mensagem, int statusHttp) {
        super(mensagem);
        this.statusHttp = statusHttp;
    }

    /**
     * @param mensagem  Mensagem de negócio clara para o cliente da API.
     * @param statusHttp Código HTTP que será retornado na resposta.
     * @param causa     Exceção original — preservada via encadeamento para diagnóstico em produção.
     */
    public PixException(String mensagem, int statusHttp, Throwable causa) {
        super(mensagem, causa);
        this.statusHttp = statusHttp;
    }

    public int getStatusHttp() {
        return statusHttp;
    }
}
