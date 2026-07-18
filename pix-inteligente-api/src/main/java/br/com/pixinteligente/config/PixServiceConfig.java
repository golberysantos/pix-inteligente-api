package br.com.pixinteligente.config;

import br.com.pixinteligente.pagamentopix.domain.repository.PixRepository;
import br.com.pixinteligente.pagamentopix.domain.service.PixService;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorLimiteDiurno;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorLimiteNoturno;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ValidadorLimiteDiurno validadorLimiteDiurno() {
        return new ValidadorLimiteDiurno();
    }

    /**
     * Registra o ValidadorLimiteNoturno como bean singleton.
     *
     * @return Instância do validador noturno.
     */
    @Bean
    public ValidadorLimiteNoturno validadorLimiteNoturno() {
        return new ValidadorLimiteNoturno();
    }

    /**
     * Registra o PixService como bean singleton.
     *
     * Injeta o PixRepository (porta) e os validadores via construtor.
     * O Spring resolve automaticamente o PixRepositoryAdapter
     * como implementação concreta de PixRepository.
     *
     * @param pixRepository    Porta de saída — resolvida pelo Spring como PixRepositoryAdapter.
     * @param validadorDiurno  Estratégia de validação diurna.
     * @param validadorNoturno Estratégia de validação noturna.
     * @return Instância do PixService.
     */
    @Bean
    public PixService pixService(PixRepository pixRepository,
                                 ValidadorLimiteDiurno validadorDiurno,
                                 ValidadorLimiteNoturno validadorNoturno) {
        return new PixService(pixRepository, validadorDiurno, validadorNoturno);
    }
}