package br.com.pixinteligente.pagamentopix.domain.strategy;

import br.com.pixinteligente.pagamentopix.domain.model.Transacao;

/**
 * Interface que define o contrato do padrão Strategy para validação de transações Pix.
 *
 * Padrão aplicado: Strategy (Behavioral)
 * Permite trocar o algoritmo de validação em tempo de execução sem alterar
 * o código do serviço que o utiliza. Cada implementação encapsula uma
 * regra de validação diferente.
 *
 * Implementações previstas:
 * - ValidadorLimiteDiurno  → limite de R$ 10.000,00 entre 06h e 20h
 * - ValidadorLimiteNoturno → limite de R$ 1.000,00 entre 20h e 06h
 *
 * Por ter um único método abstrato, esta interface também é uma
 * Interface Funcional — compatível com expressões Lambda.
 *
 * Exemplo de uso com Lambda:
 * ValidadorPix validador = transacao -> transacao.getValor()
 *     .compareTo(new BigDecimal("10000.00")) <= 0;
 *
 * @author Golbery Santos
 */
@FunctionalInterface
public interface ValidadorPix {

    /**
     * Valida se a transação Pix está dentro das regras da estratégia ativa.
     *
     * @param transacao Transação a ser validada.
     * @return true se a transação é válida, false caso contrário.
     */
    boolean validar(Transacao transacao);
}