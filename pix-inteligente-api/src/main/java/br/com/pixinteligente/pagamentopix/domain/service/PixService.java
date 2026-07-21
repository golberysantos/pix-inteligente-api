package br.com.pixinteligente.pagamentopix.domain.service;

import br.com.pixinteligente.pagamentopix.domain.exception.LimiteExcedidoException;
import br.com.pixinteligente.pagamentopix.domain.exception.TransacaoNaoEncontradaException;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao;
import br.com.pixinteligente.pagamentopix.domain.model.Transacao.StatusTransacao;
import br.com.pixinteligente.pagamentopix.domain.repository.PixRepository;
import br.com.pixinteligente.pagamentopix.domain.strategy.SeletorDeValidador;
import br.com.pixinteligente.pagamentopix.domain.strategy.ValidadorPixComPeriodo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de domínio responsável pelos casos de uso de Pagamento Pix.
 *
 * Esta classe contém as regras de negócio puras do sistema —
 * sem dependência de Spring, JPA ou qualquer framework.
 * Ela orquestra as estratégias de validação, aplica as regras
 * de negócio e delega a persistência ao PixRepository.
 *
 * Padrões aplicados:
 * - Strategy (Behavioral): seleciona o validador correto (diurno/noturno)
 *   com base no horário da transação.
 * - Ports and Adapters: depende da interface PixRepository (porta),
 *   não da implementação concreta (adaptador).
 *
 * @author Golbery Santos
 */
public class PixService {

    private final PixRepository pixRepository;
    private final SeletorDeValidador seletorDeValidador;

    /**
     * Construtor com injeção das dependências do serviço.
     * Sem @Autowired — o domínio não conhece Spring.
     * A injeção será realizada pelo adaptador ou pelo config do Spring.
     *
     * @param pixRepository       Porta de saída para persistência.
     * @param seletorDeValidador  Seletor dinâmico de estratégias de validação.
     */
    public PixService(PixRepository pixRepository,
                      SeletorDeValidador seletorDeValidador) {
        this.pixRepository = pixRepository;
        this.seletorDeValidador = seletorDeValidador;
    }

    /**
     * Caso de uso: processar uma nova transação Pix.
     *
     * Fluxo:
     * 1. Monta a transação com status PENDENTE via Builder.
     * 2. Seleciona a estratégia de validação com base no horário (Strategy).
     * 3. Valida o limite — lança LimiteExcedidoException se violado.
     * 4. Atualiza o status para APROVADA.
     * 5. Persiste via PixRepository (porta de saída).
     *
     * @param cpfOrigem  CPF do titular da conta de origem.
     * @param cpfDestino CPF do titular da conta de destino.
     * @param valor      Valor da transferência em BigDecimal.
     * @return Transação processada e persistida.
     * @throws LimiteExcedidoException se o valor ultrapassar o limite da estratégia ativa.
     */
    public Transacao processarPix(String cpfOrigem,
                                  String cpfDestino,
                                  java.math.BigDecimal valor) {

        LocalDateTime agora = LocalDateTime.now();

        // 1. Monta a transação com status PENDENTE via Builder (Creational)
        Transacao transacao = Transacao.builder()
                .cpfOrigem(cpfOrigem)
                .cpfDestino(cpfDestino)
                .valor(valor)
                .status(StatusTransacao.PENDENTE)
                .criadoEm(agora)
                .build();

        // 2. Seleciona a estratégia de validação com base no horário (Strategy via SeletorDeValidador)
        ValidadorPixComPeriodo validador = seletorDeValidador.selecionar(agora);

        // 3. Valida o limite — lança exceção se violado
        if (!validador.validar(transacao)) {
            throw new LimiteExcedidoException(valor, validador.getLimite());
        }

        // 4. Atualiza status para APROVADA via Builder
        Transacao transacaoAprovada = Transacao.builder()
                .cpfOrigem(transacao.getCpfOrigem())
                .cpfDestino(transacao.getCpfDestino())
                .valor(transacao.getValor())
                .status(StatusTransacao.APROVADA)
                .criadoEm(transacao.getCriadoEm())
                .build();

        // 5. Persiste via porta de saída
        return pixRepository.salvar(transacaoAprovada);
    }

    /**
     * Caso de uso: buscar uma transação Pix pelo ID.
     *
     * @param id Identificador da transação.
     * @return Transação encontrada.
     * @throws TransacaoNaoEncontradaException se não existir transação com o ID informado.
     */
    public Transacao buscarPorId(Long id) {
        return pixRepository.buscarPorId(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));
    }

    /**
     * Caso de uso: listar todas as transações Pix registradas.
     *
     * @return Lista de todas as transações.
     */
    public List<Transacao> listarTodas() {
        return pixRepository.listarTodas();
    }

    /**
     * Caso de uso: listar transações por CPF de origem.
     *
     * @param cpfOrigem CPF do titular da conta de origem.
     * @return Lista de transações do CPF informado.
     */
    public List<Transacao> listarPorCpfOrigem(String cpfOrigem) {
        return pixRepository.listarPorCpfOrigem(cpfOrigem);
    }

    /**
     * Caso de uso: marcar uma transação como SUSPEITA após análise do Gemini.
     * Chamado pelo GeminiAdapter quando a IA sinaliza risco na transação.
     *
     * @param id       ID da transação a ser marcada.
     * @param analise  Texto da análise gerada pelo Gemini.
     * @return Transação atualizada com status SUSPEITA e análise da IA.
     */
    public Transacao marcarComoSuspeita(Long id, String analise) {
        Transacao transacao = buscarPorId(id);

        Transacao transacaoSuspeita = Transacao.builder()
                .id(transacao.getId())
                .cpfOrigem(transacao.getCpfOrigem())
                .cpfDestino(transacao.getCpfDestino())
                .valor(transacao.getValor())
                .status(StatusTransacao.SUSPEITA)
                .analiseIa(analise)
                .criadoEm(transacao.getCriadoEm())
                .build();

        return pixRepository.salvar(transacaoSuspeita);
    }

    
   
}