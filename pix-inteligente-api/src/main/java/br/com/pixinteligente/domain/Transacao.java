package br.com.pixinteligente.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma transação Pix no sistema.
 *
 * Padrão aplicado: Builder (Creational) Permite a construção de objetos
 * Transacao de forma legível e sem construtores longos com múltiplos
 * parâmetros.
 *
 * Exemplo de uso: Transacao transacao = Transacao.builder()
 * .cpfOrigem("111.111.111-11") .cpfDestino("222.222.222-22") .valor(new
 * BigDecimal("500.00")) .build();
 *
 * @author pix-inteligente-api
 */
@Entity
@Table(name = "transacoes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * CPF do titular da conta de origem. Formato esperado: 000.000.000-00
	 */
	@Column(name = "cpf_origem", nullable = false, length = 14)
	private String cpfOrigem;

	/**
	 * CPF do titular da conta de destino. Formato esperado: 000.000.000-00
	 */
	@Column(name = "cpf_destino", nullable = false, length = 14)
	private String cpfDestino;

	/**
	 * Valor da transferência. BigDecimal garante precisão em operações financeiras.
	 * Nunca use double ou float para valores monetários.
	 */
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal valor;

	/**
	 * Status atual da transação. Armazenado como String no banco para legibilidade.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusTransacao status;

	/**
	 * Análise gerada pelo Gemini sobre a transação. Pode ser nula caso a integração
	 * com IA não seja acionada.
	 */
	@Column(name = "analise_ia", columnDefinition = "TEXT")
	private String analiseIa;

	/**
	 * Data e hora em que a transação foi criada. Preenchida automaticamente no
	 * momento da criação.
	 */
	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm;

	/**
	 * Enum que representa os possíveis status de uma transação Pix.
	 */
	public enum StatusTransacao {
		PENDENTE, APROVADA, REJEITADA, SUSPEITA
	}
}
