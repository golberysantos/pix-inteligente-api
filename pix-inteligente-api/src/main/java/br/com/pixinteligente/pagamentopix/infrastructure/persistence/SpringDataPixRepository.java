package br.com.pixinteligente.pagamentopix.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface Spring Data JPA para persistência de transações Pix.
 *
 * Esta interface fica na camada de infraestrutura — ela conhece
 * Spring Data e JPA, mas o domínio não a conhece diretamente.
 * O domínio acessa os dados apenas via PixRepository (porta).
 *
 * O PixRepositoryAdapter implementa PixRepository usando
 * esta interface como mecanismo de persistência.
 *
 * @author Golbery Santos
 */
@Repository
public interface SpringDataPixRepository extends JpaRepository<TransacaoEntity, Long> {

    /**
     * Busca todas as transações pelo CPF da conta de origem.
     * Spring Data gera a query automaticamente pelo nome do método.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return Lista de entidades JPA correspondentes.
     */
    List<TransacaoEntity> findByCpfOrigem(String cpfOrigem);
}