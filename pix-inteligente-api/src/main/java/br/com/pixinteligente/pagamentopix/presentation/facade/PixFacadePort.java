package br.com.pixinteligente.pagamentopix.presentation.facade;

import br.com.pixinteligente.pagamentopix.presentation.dto.PixRequest;
import br.com.pixinteligente.pagamentopix.presentation.dto.PixResponse;

import java.util.List;

/**
 * Porta da Facade de Pagamento Pix.
 *
 * Princípio SOLID aplicado: Dependency Inversion (DIP)
 * Define o contrato que o Controller usa para se comunicar
 * com a camada de orquestração — sem depender da implementação concreta.
 *
 * Benefícios:
 * - O Controller depende de abstração, não de implementação.
 * - Facilita testes — basta mockar esta interface no PixControllerTest.
 * - Permite múltiplas implementações da Facade sem alterar o Controller.
 *
 * @author pix-inteligente-api
 */
public interface PixFacadePort {

    /**
     * Orquestra o fluxo completo de uma transferência Pix.
     *
     * @param request DTO com os dados da transferência.
     * @return PixResponse com o resultado da operação.
     */
    PixResponse processarTransferencia(PixRequest request);

    /**
     * Busca uma transação Pix pelo ID.
     *
     * @param id Identificador da transação.
     * @return PixResponse com os dados da transação.
     */
    PixResponse buscarPorId(Long id);

    /**
     * Lista todas as transações Pix registradas.
     *
     * @return Lista de PixResponse com todas as transações.
     */
    List<PixResponse> listarTodas();

    /**
     * Lista transações Pix por CPF de origem.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return Lista de PixResponse do CPF informado.
     */
    List<PixResponse> listarPorCpfOrigem(String cpfOrigem);
}