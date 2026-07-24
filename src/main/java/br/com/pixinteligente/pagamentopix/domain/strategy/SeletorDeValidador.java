package br.com.pixinteligente.pagamentopix.domain.strategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seletor de estratégias de validação Pix.
 *
 * Padrão aplicado: Chain of Responsibility / Strategy Selector (Behavioral)
 * Permite selecionar dinamicamente a estratégia de validação adequada com base
 * na data/hora da transação, sem acoplar a classe de serviço aos validadores
 * concretos (Princípio Open/Closed).
 *
 * @author Golbery Santos
 */
public class SeletorDeValidador {

    private final List<ValidadorPixComPeriodo> validadores;

    /**
     * Construtor que recebe todos os validadores cadastrados no sistema.
     * Graças ao Spring, novos validadores que implementem ValidadorPixComPeriodo
     * serão injetados aqui automaticamente.
     *
     * @param validadores Lista de validadores disponíveis.
     */
    public SeletorDeValidador(List<ValidadorPixComPeriodo> validadores) {
        this.validadores = validadores;
    }

    /**
     * Filtra e seleciona a primeira estratégia que aceita o horário informado.
     *
     * @param agora Data/Hora atual da transação.
     * @return Estratégia de validação correspondente.
     * @throws IllegalStateException se nenhuma estratégia for encontrada para o horário.
     */
    public ValidadorPixComPeriodo selecionar(LocalDateTime agora) {
        return validadores.stream()
                .filter(v -> v.aceitaHorario(agora))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhuma estrategia de validacao Pix encontrada para o horario: " + agora));
    }
}
