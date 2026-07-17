package br.com.pixinteligente.pagamentopix.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de domínio que representa uma transação Pix.
 *
 * Esta classe é pura — não possui nenhuma dependência de framework,
 * JPA, Spring ou qualquer tecnologia de infraestrutura.
 * Ela representa apenas o conceito de negócio de uma transação Pix.
 *
 * Padrão aplicado: Builder (Creational)
 * Permite a construção de objetos Transacao de forma legível,
 * sem construtores longos com múltiplos parâmetros.
 *
 * Exemplo de uso:
 * Transacao transacao = Transacao.builder()
 *     .cpfOrigem("111.111.111-11")
 *     .cpfDestino("222.222.222-22")
 *     .valor(new BigDecimal("500.00"))
 *     .status(StatusTransacao.PENDENTE)
 *     .criadoEm(LocalDateTime.now())
 *     .build();
 *
 * @author pix-inteligente-api
 */
public class Transacao {

    private Long id;
    private String cpfOrigem;
    private String cpfDestino;
    private BigDecimal valor;
    private StatusTransacao status;
    private String analiseIa;
    private LocalDateTime criadoEm;

    /**
     * Construtor privado — uso exclusivo do Builder.
     */
    private Transacao(Builder builder) {
        this.id = builder.id;
        this.cpfOrigem = builder.cpfOrigem;
        this.cpfDestino = builder.cpfDestino;
        this.valor = builder.valor;
        this.status = builder.status;
        this.analiseIa = builder.analiseIa;
        this.criadoEm = builder.criadoEm;
    }

    // --- Getters ---

    public Long getId() { return id; }
    public String getCpfOrigem() { return cpfOrigem; }
    public String getCpfDestino() { return cpfDestino; }
    public BigDecimal getValor() { return valor; }
    public StatusTransacao getStatus() { return status; }
    public String getAnaliseIa() { return analiseIa; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    /**
     * Ponto de entrada do Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder interno da classe Transacao.
     *
     * Implementação manual do padrão Builder (Creational) sem dependência
     * de frameworks como Lombok — tornando o padrão explícito e didático
     * para fins de avaliação do desafio.
     */
    public static class Builder {

        private Long id;
        private String cpfOrigem;
        private String cpfDestino;
        private BigDecimal valor;
        private StatusTransacao status;
        private String analiseIa;
        private LocalDateTime criadoEm;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder cpfOrigem(String cpfOrigem) {
            this.cpfOrigem = cpfOrigem;
            return this;
        }

        public Builder cpfDestino(String cpfDestino) {
            this.cpfDestino = cpfDestino;
            return this;
        }

        public Builder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public Builder status(StatusTransacao status) {
            this.status = status;
            return this;
        }

        public Builder analiseIa(String analiseIa) {
            this.analiseIa = analiseIa;
            return this;
        }

        public Builder criadoEm(LocalDateTime criadoEm) {
            this.criadoEm = criadoEm;
            return this;
        }

        public Transacao build() {
            return new Transacao(this);
        }
    }

    /**
     * Enum que representa os possíveis status de uma transação Pix.
     *
     * PENDENTE  — transação criada, aguardando validação.
     * APROVADA  — transação validada e processada com sucesso.
     * REJEITADA — transação bloqueada por violação de limite ou regra de negócio.
     * SUSPEITA  — transação sinalizada pela IA do Gemini para revisão manual.
     */
    public enum StatusTransacao {
        PENDENTE,
        APROVADA,
        REJEITADA,
        SUSPEITA
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", cpfOrigem='" + cpfOrigem + '\'' +
                ", cpfDestino='" + cpfDestino + '\'' +
                ", valor=" + valor +
                ", status=" + status +
                ", criadoEm=" + criadoEm +
                '}';
    }
}