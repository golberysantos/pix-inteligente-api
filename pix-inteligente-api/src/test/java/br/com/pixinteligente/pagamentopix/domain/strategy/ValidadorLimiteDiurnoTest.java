package br.com.pixinteligente.pagamentopix.domain.strategy;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários do padrão Strategy — ValidadorLimiteDiurno.
 *
 * Valida as regras de limite do período diurno (06h às 20h).
 * Testes puros — sem Spring, sem banco, sem mocks.
 *
 * @author Golbery Santos
 */
class ValidadorLimiteDiurnoTest {

    private ValidadorLimiteDiurno validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorLimiteDiurno();
    }

    @Test
    @DisplayName("Deve aprovar transacao com valor abaixo do limite diurno")
    void deveAprovarTransacaoAbaixoDoLimite() {
        // Arrange
        Transacao transacao = buildTransacao(new BigDecimal("500.00"));

        // Act
        boolean resultado = validador.validar(transacao);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Deve aprovar transacao com valor exatamente no limite diurno")
    void deveAprovarTransacaoNoLimiteExato() {
        // Arrange — valor exatamente no limite: R$ 10.000,00
        Transacao transacao = buildTransacao(new BigDecimal("10000.00"));

        // Act
        boolean resultado = validador.validar(transacao);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar transacao com valor acima do limite diurno")
    void deveRejeitarTransacaoAcimaDoLimite() {
        // Arrange
        Transacao transacao = buildTransacao(new BigDecimal("10000.01"));

        // Act
        boolean resultado = validador.validar(transacao);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar transacao com valor muito acima do limite diurno")
    void deveRejeitarTransacaoMuitoAcimaDoLimite() {
        // Arrange
        Transacao transacao = buildTransacao(new BigDecimal("50000.00"));

        // Act
        boolean resultado = validador.validar(transacao);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Deve retornar o limite correto para composicao da mensagem de erro")
    void deveRetornarLimiteCorreto() {
        // Act
        BigDecimal limite = validador.getLimite();

        // Assert
        assertThat(limite).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Deve aprovar transacao com valor minimo permitido")
    void deveAprovarTransacaoComValorMinimo() {
        // Arrange
        Transacao transacao = buildTransacao(new BigDecimal("0.01"));

        // Act
        boolean resultado = validador.validar(transacao);

        // Assert
        assertThat(resultado).isTrue();
    }

    /**
     * Monta uma transação de teste com o valor informado.
     */
    private Transacao buildTransacao(BigDecimal valor) {
        return Transacao.builder()
                .cpfOrigem("111.111.111-11")
                .cpfDestino("222.222.222-22")
                .valor(valor)
                .status(StatusTransacao.PENDENTE)
                .criadoEm(LocalDateTime.now())
                .build();
    }
}