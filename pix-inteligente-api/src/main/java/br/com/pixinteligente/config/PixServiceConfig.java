package br.com.pixinteligente.config;

import br.com.pixinteligente.pagamentopix.domain.repository.PixRepository;
import br.com.pixinteligente.pagamentopix.domain.service.PixService;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorLimiteDiurno;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorLimiteNoturno;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorPixComPeriodo;
import br.com.pixinteligente.pagamentopix.domain.strategy.SeletorDeValidador;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração dos beans do domínio de Pagamento Pix.
 *
 * O domínio não possui annotations do Spring (@Service, @Component)
 * para manter a independência de framework. Esta classe de configuração
 * é responsável por registrar os beans do domínio no contexto do Spring,
 * seguindo o princípio de inversão de dependência.
 *
 * Padrão aplicado: Singleton (Creational)
 * Todos os beans declarados aqui são gerenciados pelo Spring
 * como singletons — uma única instância por contexto de aplicação.
 *
 * @author pix-inteligente-api
 */
@Configuration
public class PixServiceConfig {

    /**
     * Registra o ValidadorLimiteDiurno como bean singleton.
     *
     * @return Instância do validador diurno.
     */
    @Bean
    public ValidadorPixComPeriodo validadorLimiteDiurno() {
        return new ValidadorLimiteDiurno();
    }

    /**
     * Registra o ValidadorLimiteNoturno como bean singleton.
     *
     * @return Instância do validador noturno.
     */
    @Bean
    public ValidadorPixComPeriodo validadorLimiteNoturno() {
        return new ValidadorLimiteNoturno();
    }

    /**
     * Registra o SeletorDeValidador como bean singleton,
     * coletando todos os beans do tipo ValidadorPixComPeriodo.
     *
     * @param validadores Lista de todos os validadores injetados automaticamente pelo Spring.
     * @return Instância do SeletorDeValidador.
     */
    @Bean
    public SeletorDeValidador seletorDeValidador(List<ValidadorPixComPeriodo> validadores) {
        return new SeletorDeValidador(validadores);
    }

    /**
     * Registra o PixService como bean singleton.
     *
     * Injeta o PixRepository (porta) e o SeletorDeValidador via construtor.
     * O Spring resolve automaticamente o PixRepositoryAdapter
     * como implementação concreta de PixRepository.
     *
     * @param pixRepository       Porta de saída — resolvida pelo Spring como PixRepositoryAdapter.
     * @param seletorDeValidador  Seletor dinâmico de estratégias de validação.
     * @return Instância do PixService.
     */
    @Bean
    public PixService pixService(PixRepository pixRepository,
                                 SeletorDeValidador seletorDeValidador) {
        return new PixService(pixRepository, seletorDeValidador);
    }
}