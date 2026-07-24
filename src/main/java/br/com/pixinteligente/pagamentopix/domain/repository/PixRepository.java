package br.com.pixinteligente.pagamentopix.domain.repository;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída do domínio de Pagamento Pix.
 *
 * Define o contrato puro de persistência que o domínio precisa —
 * sem nenhuma dependência de Spring Data, JPA ou qualquer tecnologia
 * de infraestrutura. O domínio não sabe como os dados são persistidos,
 * apenas que pode persistir e recuperar transações.
 *
 * Padrão aplicado: Ports and Adapters (Hexagonal Architecture)
 * Esta interface é a "porta" — o adaptador que a implementa
 * fica em infrastructure.adapter.PixRepositoryAdapter.
 *
 * Caso o banco de dados mude de H2 para PostgreSQL, ou o ORM
 * mude de JPA para JDBC puro, apenas o adaptador muda.
 * O domínio permanece intacto.
 *
 * @author Golbery Santos
 */
public interface PixRepository {

    /**
     * Persiste uma transação Pix.
     *
     * @param transacao Transação a ser salva.
     * @return Transação salva com o ID gerado pelo banco.
     */
    Transacao salvar(Transacao transacao);

    /**
     * Busca uma transação Pix pelo seu identificador único.
     *
     * @param id Identificador da transação.
     * @return Optional contendo a transação, ou vazio se não encontrada.
     */
    Optional<Transacao> buscarPorId(Long id);

    /**
     * Retorna todas as transações Pix registradas no sistema.
     *
     * @return Lista de transações. Vazia se não houver nenhuma.
     */
    List<Transacao> listarTodas();

    /**
     * Retorna todas as transações Pix de um determinado CPF de origem.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return Lista de transações do CPF informado.
     */
    List<Transacao> listarPorCpfOrigem(String cpfOrigem);
}
