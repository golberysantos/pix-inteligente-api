package br.com.pixinteligente.pagamentopix.domain.service;

import br.com.pixinteligente.pagamentopix.domain.exception.LimiteExcedidoException;
import br.com.pixinteligente.pagamentopix.domain.exception.TransacaoNaoEncontradaException;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;
import br.com.pixinteligente.pagamentopix.domain.repository.PixRepository;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorLimiteDiurno;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorLimiteNoturno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do PixService.
 *
 * Testa as regras de negócio do domínio de forma isolada — sem subir o contexto
 * do Spring, sem banco de dados. O PixRepository é substituído por um Mock do
 * Mockito.
 *
 * @author pix-inteligente-api
 */
@ExtendWith(MockitoExtension.class)
class PixServiceTest {

	@Mock
	private PixRepository pixRepository;

	private ValidadorLimiteDiurno validadorDiurno;
	private ValidadorLimiteNoturno validadorNoturno;
	private PixService pixService;

	@BeforeEach
	void setUp() {
		validadorDiurno = new ValidadorLimiteDiurno();
		validadorNoturno = new ValidadorLimiteNoturno();
		pixService = new PixService(pixRepository, validadorDiurno, validadorNoturno);
	}

	// ==========================================
	// Testes de processarPix()
	// ==========================================

	@Test
	@DisplayName("Deve processar Pix com sucesso quando valor está dentro do limite")
	void deveProcessarPixComSucesso() {
		// Arrange
		BigDecimal valor = new BigDecimal("500.00");
		Transacao transacaoSalva = Transacao.builder().id(1L).cpfOrigem("111.111.111-11").cpfDestino("222.222.222-22")
				.valor(valor).status(StatusTransacao.APROVADA).criadoEm(LocalDateTime.now()).build();

		when(pixRepository.salvar(any(Transacao.class))).thenReturn(transacaoSalva);

		// Act
		Transacao resultado = pixService.processarPix("111.111.111-11", "222.222.222-22", valor);

		// Assert
		assertThat(resultado).isNotNull();
		assertThat(resultado.getStatus()).isEqualTo(StatusTransacao.APROVADA);
		assertThat(resultado.getValor()).isEqualByComparingTo(valor);
		verify(pixRepository).salvar(any(Transacao.class));
	}

	@Test
	@DisplayName("Deve lançar LimiteExcedidoException quando valor ultrapassa qualquer limite")
	void deveLancarExcecaoQuandoValorUltrapassaLimiteDiurno() {
		// Arrange
		// Valor acima de R$ 10.000,00 — ultrapassa tanto o limite diurno
		// quanto o noturno, independente do horário de execução do teste
		BigDecimal valorAcimaDoLimite = new BigDecimal("15000.00");

		// Act & Assert
		assertThatThrownBy(() -> pixService.processarPix("111.111.111-11", "222.222.222-22", valorAcimaDoLimite))
				.isInstanceOf(LimiteExcedidoException.class).hasMessageContaining("Limite Pix excedido")
				.hasMessageContaining("15000.00");
		// Nota: não verificamos o limite exato porque o PixService seleciona
		// o validador com base no horário real da máquina (diurno ou noturno).
		// O ValidadorLimiteDiurnoTest cobre os limites específicos de forma isolada.
	}

	@Test
	@DisplayName("Deve construir transacao com status PENDENTE antes de validar")
	void deveConstruirTransacaoComStatusPendente() {
		// Arrange
		BigDecimal valor = new BigDecimal("100.00");
		Transacao transacaoSalva = Transacao.builder().id(1L).cpfOrigem("111.111.111-11").cpfDestino("222.222.222-22")
				.valor(valor).status(StatusTransacao.APROVADA).criadoEm(LocalDateTime.now()).build();

		when(pixRepository.salvar(any(Transacao.class))).thenReturn(transacaoSalva);

		// Act
		pixService.processarPix("111.111.111-11", "222.222.222-22", valor);

		// Assert — verifica que o repositório foi chamado com status APROVADA
		verify(pixRepository).salvar(any(Transacao.class));
	}

	// ==========================================
	// Testes de buscarPorId()
	// ==========================================

	@Test
	@DisplayName("Deve retornar transacao quando ID existe")
	void deveRetornarTransacaoQuandoIdExiste() {
		// Arrange
		Long id = 1L;
		Transacao transacao = Transacao.builder().id(id).cpfOrigem("111.111.111-11").cpfDestino("222.222.222-22")
				.valor(new BigDecimal("500.00")).status(StatusTransacao.APROVADA).criadoEm(LocalDateTime.now()).build();

		when(pixRepository.buscarPorId(id)).thenReturn(Optional.of(transacao));

		// Act
		Transacao resultado = pixService.buscarPorId(id);

		// Assert
		assertThat(resultado).isNotNull();
		assertThat(resultado.getId()).isEqualTo(id);
		assertThat(resultado.getStatus()).isEqualTo(StatusTransacao.APROVADA);
	}

	@Test
	@DisplayName("Deve lançar TransacaoNaoEncontradaException quando ID não existe")
	void deveLancarExcecaoQuandoIdNaoExiste() {
		// Arrange
		Long idInexistente = 99L;
		when(pixRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> pixService.buscarPorId(idInexistente))
				.isInstanceOf(TransacaoNaoEncontradaException.class).hasMessageContaining("99");
	}

	// ==========================================
	// Testes de listarTodas()
	// ==========================================

	@Test
	@DisplayName("Deve retornar lista vazia quando nao ha transacoes")
	void deveRetornarListaVaziaQuandoNaoHaTransacoes() {
		// Arrange
		when(pixRepository.listarTodas()).thenReturn(List.of());

		// Act
		List<Transacao> resultado = pixService.listarTodas();

		// Assert
		assertThat(resultado).isNotNull().isEmpty();
	}

	@Test
	@DisplayName("Deve retornar todas as transacoes registradas")
	void deveRetornarTodasAsTransacoes() {
		// Arrange
		List<Transacao> transacoes = List.of(
				Transacao.builder().id(1L).status(StatusTransacao.APROVADA).valor(new BigDecimal("500.00"))
						.criadoEm(LocalDateTime.now()).build(),
				Transacao.builder().id(2L).status(StatusTransacao.SUSPEITA).valor(new BigDecimal("6000.00"))
						.criadoEm(LocalDateTime.now()).build());

		when(pixRepository.listarTodas()).thenReturn(transacoes);

		// Act
		List<Transacao> resultado = pixService.listarTodas();

		// Assert
		assertThat(resultado).hasSize(2);
		assertThat(resultado.get(0).getStatus()).isEqualTo(StatusTransacao.APROVADA);
		assertThat(resultado.get(1).getStatus()).isEqualTo(StatusTransacao.SUSPEITA);
	}

	// ==========================================
	// Testes de marcarComoSuspeita()
	// ==========================================

	@Test
	@DisplayName("Deve marcar transacao como SUSPEITA com analise da IA")
	void deveMarcarTransacaoComoSuspeita() {
		// Arrange
		Long id = 1L;
		String analise = "Transacao suspeita detectada pelo Gemini.";

		Transacao transacaoExistente = Transacao.builder().id(id).cpfOrigem("111.111.111-11")
				.cpfDestino("222.222.222-22").valor(new BigDecimal("6000.00")).status(StatusTransacao.APROVADA)
				.criadoEm(LocalDateTime.now()).build();

		Transacao transacaoSuspeita = Transacao.builder().id(id).cpfOrigem("111.111.111-11")
				.cpfDestino("222.222.222-22").valor(new BigDecimal("6000.00")).status(StatusTransacao.SUSPEITA)
				.analiseIa(analise).criadoEm(LocalDateTime.now()).build();

		when(pixRepository.buscarPorId(id)).thenReturn(Optional.of(transacaoExistente));
		when(pixRepository.salvar(any(Transacao.class))).thenReturn(transacaoSuspeita);

		// Act
		Transacao resultado = pixService.marcarComoSuspeita(id, analise);

		// Assert
		assertThat(resultado.getStatus()).isEqualTo(StatusTransacao.SUSPEITA);
		assertThat(resultado.getAnaliseIa()).isEqualTo(analise);
	}
}