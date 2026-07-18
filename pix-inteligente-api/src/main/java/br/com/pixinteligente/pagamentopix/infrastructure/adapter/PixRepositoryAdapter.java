package br.com.pixinteligente.pagamentopix.infrastructure.adapter;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.repository.PixRepository;
import br.com.pixinteligente.pagamentopix.infrastructure.persistence.SpringDataPixRepository;
import br.com.pixinteligente.pagamentopix.infrastructure.persistence.TransacaoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa a porta de saída PixRepository.
 *
 * Padrão aplicado: Adapter (Structural)
 * Faz a ponte entre o domínio puro e a infraestrutura de persistência,
 * convertendo objetos de domínio (Transacao) em entidades JPA
 * (TransacaoEntity) e vice-versa.
 *
 * O domínio conhece apenas PixRepository (interface/porta).
 * Apenas este adaptador conhece SpringDataPixRepository e TransacaoEntity.
 *
 * Benefício: se o banco mudar de H2 para PostgreSQL, ou o ORM
 * mudar de JPA para JDBC puro, apenas este arquivo muda.
 * O domínio permanece completamente intacto.
 *
 * @author Golbery Santos
 */
@Component
public class PixRepositoryAdapter implements PixRepository {

    private final SpringDataPixRepository springDataPixRepository;

    public PixRepositoryAdapter(SpringDataPixRepository springDataPixRepository) {
        this.springDataPixRepository = springDataPixRepository;
    }

    /**
     * Persiste uma transação Pix.
     *
     * Converte o modelo de domínio para entidade JPA (fromDomain),
     * persiste via Spring Data e converte o resultado de volta
     * para o modelo de domínio (toDomain).
     *
     * @param transacao Transação de domínio a ser salva.
     * @return Transação de domínio com o ID gerado pelo banco.
     */
    @Override
    public Transacao salvar(Transacao transacao) {
        TransacaoEntity entity = TransacaoEntity.fromDomain(transacao);
        TransacaoEntity salva = springDataPixRepository.save(entity);
        return salva.toDomain();
    }

    /**
     * Busca uma transação Pix pelo ID.
     *
     * Converte a entidade JPA encontrada para o modelo de domínio.
     *
     * @param id Identificador da transação.
     * @return Optional com a transação de domínio, ou vazio se não encontrada.
     */
    @Override
    public Optional<Transacao> buscarPorId(Long id) {
        return springDataPixRepository.findById(id)
                .map(TransacaoEntity::toDomain);
    }

    /**
     * Retorna todas as transações Pix persistidas.
     *
     * Converte a lista de entidades JPA para lista de modelos de domínio
     * usando Stream API com Method Reference.
     *
     * @return Lista de transações de domínio.
     */
    @Override
    public List<Transacao> listarTodas() {
        return springDataPixRepository.findAll()
                .stream()
                .map(TransacaoEntity::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas as transações de um determinado CPF de origem.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return Lista de transações de domínio do CPF informado.
     */
    @Override
    public List<Transacao> listarPorCpfOrigem(String cpfOrigem) {
        return springDataPixRepository.findByCpfOrigem(cpfOrigem)
                .stream()
                .map(TransacaoEntity::toDomain)
                .collect(Collectors.toList());
    }
}