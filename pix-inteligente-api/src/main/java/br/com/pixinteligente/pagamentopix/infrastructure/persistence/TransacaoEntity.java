package br.com.pixinteligente.pagamentopix.infrastructure.persistence;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;
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
 * Entidade JPA que representa a transação Pix no banco de dados.
 *
 * Esta classe pertence à camada de infraestrutura — ela conhece
 * os detalhes de persistência (JPA, H2) mas o domínio não a conhece.
 *
 * Responsabilidade: mapear o modelo de domínio Transacao para
 * a tabela "transacoes" no banco de dados e vice-versa.
 *
 * Padrão aplicado: Adapter (Structural)
 * Os métodos toDomain() e fromDomain() fazem a conversão
 * entre o modelo de domínio puro e a entidade JPA,
 * desacoplando completamente as duas camadas.
 *
 * @author pix-inteligente-api
 */
@Entity
@Table(name = "transacoes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_origem", nullable = false, length = 14)
    private String cpfOrigem;

    @Column(name = "cpf_destino", nullable = false, length = 14)
    private String cpfDestino;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTransacao status;

    @Column(name = "analise_ia", columnDefinition = "TEXT")
    private String analiseIa;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    /**
     * Converte esta entidade JPA para o modelo de domínio puro.
     *
     * Padrão Adapter: TransacaoEntity → Transacao (domínio)
     * O domínio não conhece TransacaoEntity — apenas Transacao.
     *
     * @return Objeto de domínio Transacao equivalente.
     */
    public Transacao toDomain() {
        return Transacao.builder()
                .id(this.id)
                .cpfOrigem(this.cpfOrigem)
                .cpfDestino(this.cpfDestino)
                .valor(this.valor)
                .status(this.status)
                .analiseIa(this.analiseIa)
                .criadoEm(this.criadoEm)
                .build();
    }

    /**
     * Converte um modelo de domínio puro para esta entidade JPA.
     *
     * Padrão Adapter: Transacao (domínio) → TransacaoEntity
     * Chamado pelo PixRepositoryAdapter antes de persistir.
     *
     * @param transacao Objeto de domínio a ser convertido.
     * @return Entidade JPA equivalente pronta para persistência.
     */
    public static TransacaoEntity fromDomain(Transacao transacao) {
        return TransacaoEntity.builder()
                .id(transacao.getId())
                .cpfOrigem(transacao.getCpfOrigem())
                .cpfDestino(transacao.getCpfDestino())
                .valor(transacao.getValor())
                .status(transacao.getStatus())
                .analiseIa(transacao.getAnaliseIa())
                .criadoEm(transacao.getCriadoEm())
                .build();
    }
}