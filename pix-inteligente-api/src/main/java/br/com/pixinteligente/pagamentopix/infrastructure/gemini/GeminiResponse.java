package br.com.pixinteligente.pagamentopix.infrastructure.gemini;

/**
 * DTO que representa a resposta da API do Gemini.
 *
 * Usa Record do Java 21 — imutável, sem boilerplate,
 * ideal para objetos de transferência de dados.
 *
 * Quando a API Key estiver disponível, este Record
 * será preenchido com o JSON real retornado pelo Gemini.
 *
 * @author Golbery Santos
 */
public record GeminiResponse(

        /**
         * Texto da análise gerada pelo Gemini sobre a transação.
         */
        String analise,

        /**
         * Indica se o Gemini considerou a transação suspeita.
         * true  → transação será marcada como SUSPEITA no sistema.
         * false → transação permanece com o status atual.
         */
        boolean suspeita
) {}
