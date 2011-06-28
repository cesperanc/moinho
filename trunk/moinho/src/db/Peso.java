package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import jogos.AlfaBeta;
import moinho.Treino;

/**
 *
 * @author Cláudio Esperança
 */
public class Peso {
    
    public static String FUNCAO_AVALIACAO_A1 = "a1_QuantosTresEmLinha";
    public static String FUNCAO_AVALIACAO_A2 = "a2_NumeroMovimentosPossiveis";
    public static String FUNCAO_AVALIACAO_A3 = "a3_NumeroPecasComMobilidade";
    public static String FUNCAO_AVALIACAO_A4 = "a4_NumeroPecas";
    public static String FUNCAO_AVALIACAO_A5 = "a5_QuantosPossiveisTresEmLinha";
    public static String FUNCAO_AVALIACAO_A6 = "a6_QuantasPecasEmTresEmLinha";
    public static String FUNCAO_AVALIACAO_A7 = "a7_tabuleiroVencedor";
    public static String FUNCAO_AVALIACAO_A8 = "a8_NumeroCasasVizinhasDisponiveis";
    public static String FUNCAO_AVALIACAO_A9 = "a9_QuantosPossiveisTresEmLinha";
    public static String FUNCAO_AVALIACAO_A1N = "a1_QuantosTresEmLinhaN";
    public static String FUNCAO_AVALIACAO_A2N = "a2_NumeroMovimentosPossiveisN";
    public static String FUNCAO_AVALIACAO_A3N = "a3_NumeroPecasComMobilidadeN";
    public static String FUNCAO_AVALIACAO_A4N = "a4_NumeroPecasN";
    public static String FUNCAO_AVALIACAO_A5N = "a5_QuantosPossiveisTresEmLinhaN";
    public static String FUNCAO_AVALIACAO_A6N = "a6_QuantasPecasEmTresEmLinhaN";
    public static String FUNCAO_AVALIACAO_A7N = "a7_tabuleiroVencedorN";
    public static String FUNCAO_AVALIACAO_A8N = "a8_NumeroCasasVizinhasDisponiveisN";
    public static String FUNCAO_AVALIACAO_A9N = "a9_QuantosPossiveisTresEmLinhaN";
    
    public static String PROFUNDIDADE_PESQUISA = "profundidade_pesquisa";
    public static String ALGORITMO = "algoritmo";
    public static String TOTAL_VITORIAS_COMO_J1 = "total_vitorias_como_j1";
    public static String TOTAL_VITORIAS_COMO_J2 = "total_vitorias_como_j2";
    public static String TOTAL_DERROTAS_COMO_J1 = "total_derrotas_como_j1";
    public static String TOTAL_DERROTAS_COMO_J2 = "total_derrotas_como_j2";
    public static String TOTAL_JOGOS_COMO_J1 = "total_jogos_como_j1";
    public static String TOTAL_JOGOS_COMO_J2 = "total_jogos_como_j2";
    public static String TOTAL_JOGADAS_VITORIA_COMO_J1 = "total_jogadas_vitoria_como_j1";
    public static String TOTAL_JOGADAS_VITORIA_COMO_J2 = "total_jogadas_vitoria_como_j2";
    public static String TOTAL_JOGADAS_DERROTA_COMO_J1 = "total_jogadas_derrota_como_j1";
    public static String TOTAL_JOGADAS_DERROTA_COMO_J2 = "total_jogadas_derrota_como_j2";
    public static String TOTAL_DURACAO_VITORIA_COMO_J1 = "total_duracao_vitoria_como_j1";
    public static String TOTAL_DURACAO_VITORIA_COMO_J2 = "total_duracao_vitoria_como_j2";
    public static String TOTAL_DURACAO_DERROTA_COMO_J1 = "total_duracao_derrota_como_j1";
    public static String TOTAL_DURACAO_DERROTA_COMO_J2 = "total_duracao_derrota_como_j2";
    public static String TOTAL_JOGADAS_VITORIA = "total_jogadas_vitoria";
    public static String TOTAL_JOGADAS_DERROTA = "total_jogadas_derrota";
    public static String TOTAL_DURACAO_VITORIA = "total_duracao_vitoria";
    public static String TOTAL_DURACAO_DERROTA = "total_duracao_derrota";
    public static String TOTAL_VITORIAS = "total_vitorias";
    public static String TOTAL_DERROTAS = "total_derrotas";
    public static String TOTAL_JOGOS = "total_jogos";
    public static String PERCENTAGEM_VITORIAS = "percentagem_vitorias";
    public static String PERCENTAGEM_DERROTAS = "percentagem_derrotas";
    public static String PESOS_TBL = "pesos_tbl";
    public static String PESO_ID = "pesoId";
    public static String JOGOS_TBL = "jogos_tbl";
    public static String JOGO_ID = "jogoId";
    public static String PESOS_VIEW = "pesosView";
    
    private static HashMap<String, String> funcoesAvaliacaoDesc = null;
    private static LinkedList<String> columnsOrder = null;
    private static HashMap<String, Integer> pesosFuncoesAvaliacaoDefault = null;
    private static ArrayList<String> nomesPesosFuncoesAvaliacaoDefault = null;
    
    private HashMap<String, Integer> pesosFuncoesAvaliacao = new HashMap<String, Integer>();
    
    private int profundidade = 3;
    private String algoritmo = AlfaBeta.NOME;
    
    /**
     * Constructor
     */
    public Peso(){
	this(3);
    }
    
    /**
     * Constructor
     * @param profundidade máxima de pesquisa
     */
    public Peso(int profundidade){
	this(profundidade, AlfaBeta.NOME);
    }
    
    /**
     * Constructor
     * @param profundidade máxima de pesquisa
     * @param algoritmo utilizado
     */
    public Peso(int profundidade, String algoritmo){
	this(profundidade, algoritmo, getCoeficientesPorOmissao());
    }
    
    /**
     * Constructor
     * @param profundidade máxima de pesquisa
     * @param algoritmo utilizado
     * @param pesosAvalicao pesos da função
     */
    public Peso(int profundidade, String algoritmo, HashMap<String, Integer> pesosAvalicao){
	updateCoeficientes(pesosAvalicao);
	this.profundidade = profundidade;
	this.algoritmo = algoritmo;
    }
    
    /**
     * Define o peso para uma dada função de avaliação
     * @param funcao
     * @param peso 
     */
    public void setCoeficiente(String funcao, int peso){
	this.pesosFuncoesAvaliacao.put(funcao, peso);
    }
    
    /**
     * Obtem o peso para uma dada função de avaliação
     * @param funcao
     * @return o peso 
     */
    public int getCoeficiente(String funcao){
	return this.pesosFuncoesAvaliacao.get(funcao);
    }
    
    /**
     * Obtem os coeficientes das funções de avaliação
     * @return os coeficientes para este peso
     */
    public HashMap<String, Integer> getCoeficientes(){
	return this.pesosFuncoesAvaliacao;
    }
    
    /**
     * Com base na tabela de pesos fornecida, actualiza os pesos internamente
     * @param pesosAvalicao para cópia dos valores
     */
    public final void updateCoeficientes(HashMap<String, Integer> pesosAvalicao){
	for(String peso : pesosAvalicao.keySet()){
	    this.setCoeficiente(peso, pesosAvalicao.get(peso));
	}
    }
    
    /**
     * Obtem os coeficientes para as funções de avaliação definidas na lista
     * @return Lista com os coeficientes das funções de avaliação
     */
    public static HashMap<String, Integer> getCoeficientesPorOmissao(){
	if(pesosFuncoesAvaliacaoDefault == null){
	    pesosFuncoesAvaliacaoDefault = new HashMap<String, Integer>();
	    for(String coeficiente : getNomesCoeficientesPorOmissao()){
		pesosFuncoesAvaliacaoDefault.put(coeficiente, Treino.getAleatorio().nextInt(Treino.valorMaximoCoeficiente));
	    }
	}
	
	return pesosFuncoesAvaliacaoDefault;
    }
    
    /**
     * Obtem os nomes dos coeficientes para as funções de avaliação definidas na lista
     * @return Lista com os nomes das funções de avaliação
     */
    public static ArrayList<String> getNomesCoeficientesPorOmissao(){
	if(nomesPesosFuncoesAvaliacaoDefault == null){
	    nomesPesosFuncoesAvaliacaoDefault = new ArrayList<String>();
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A1);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A2);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A3);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A4);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A5);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A6);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A7);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A8);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A9);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A1N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A2N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A3N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A4N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A5N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A6N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A7N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A8N);
	    nomesPesosFuncoesAvaliacaoDefault.add(FUNCAO_AVALIACAO_A9N);
	}
	
	return nomesPesosFuncoesAvaliacaoDefault;
    }
    
    /**
     * Obtém todas as descrições do parametros peso
     * 
     * @return  com as descrições
     */
    public static HashMap<String, String> getFuncaoCoeficienteDescs(){
	if(funcoesAvaliacaoDesc == null){
	    funcoesAvaliacaoDesc = new HashMap<String, String>();
	    columnsOrder = new LinkedList<String>();
	    
	    addColumn(PESO_ID, "ID");
	    addColumn(FUNCAO_AVALIACAO_A1, "A1 - Coeficiente Número de Três em Linha");
	    addColumn(FUNCAO_AVALIACAO_A2, "A2 - Coeficiente Número de Movimentos Possíveis");
	    addColumn(FUNCAO_AVALIACAO_A3, "A3 - Coeficiente Número de Peças Com Mobilidade");
	    addColumn(FUNCAO_AVALIACAO_A4, "A4 - Coeficiente Número de Peças");
	    addColumn(FUNCAO_AVALIACAO_A5, "A5 - Coeficiente Número de Quantos Três em Linha Possíveis");
	    addColumn(FUNCAO_AVALIACAO_A6, "A6 - Coeficiente para Quantas Peças em Três em Linha");
	    addColumn(FUNCAO_AVALIACAO_A7, "A7 - Coeficiente para Tabuleiro Vencedor");
	    addColumn(FUNCAO_AVALIACAO_A8, "A8 - Coeficiente para Casas Vizinhas disponivéis");
	    addColumn(FUNCAO_AVALIACAO_A9, "A9 - Coeficiente para Verificar possibilidade de Três em Linha");
	    addColumn(FUNCAO_AVALIACAO_A1N, "A1N - Coeficiente Número de Três em Linha - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A2N, "A2N - Coeficiente Número de Movimentos Possíveis - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A3N, "A3N - Coeficiente Número de Peças Com Mobilidade - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A4N, "A4N - Coeficiente Número de Peças - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A5N, "A5N - Coeficiente Número de Quantos Três em Linha Possíveis - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A6N, "A6N - Coeficiente para Quantas Peças em Três em Linha - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A7N, "A7N - Coeficiente para Tabuleiro Vencedor - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A8N, "A8N - Coeficiente para Casas Vizinhas disponivéis - Negativo");
	    addColumn(FUNCAO_AVALIACAO_A9N, "A9N - Coeficiente para Verificar possibilidade de Três em Linha - Negativo");
	    
	    addColumn(PROFUNDIDADE_PESQUISA, "Profundidade máxima de pesquisa");
	    addColumn(ALGORITMO, "Algoritmo");
	    addColumn(TOTAL_VITORIAS_COMO_J1, "Total de vitórias como jogador brancas");
	    addColumn(TOTAL_VITORIAS_COMO_J2, "Total de vitórias como jogador pretas");
	    addColumn(TOTAL_DERROTAS_COMO_J1, "Total de derrotas como jogador brancas");
	    addColumn(TOTAL_DERROTAS_COMO_J2, "Total de derrotas como jogador pretas");
	    addColumn(TOTAL_JOGOS_COMO_J1, "Total de jogos como jogador brancas");
	    addColumn(TOTAL_JOGOS_COMO_J2, "Total de jogos como jogador pretas");
	    addColumn(TOTAL_JOGADAS_VITORIA_COMO_J1, "Total de jogadas até à vitória como jogador brancas");
	    addColumn(TOTAL_JOGADAS_VITORIA_COMO_J2, "Total de jogadas até à vitória como jogador pretas");
	    addColumn(TOTAL_JOGADAS_DERROTA_COMO_J1, "Total de jogadas até à derrota como jogador brancas");
	    addColumn(TOTAL_JOGADAS_DERROTA_COMO_J2, "Total de jogadas até à derrota como jogador pretas");
	    addColumn(TOTAL_DURACAO_VITORIA_COMO_J1, "Tempo decorrido até à vitória como jogador brancas");
	    addColumn(TOTAL_DURACAO_VITORIA_COMO_J2, "Tempo decorrido até à vitória como jogador pretas");
	    addColumn(TOTAL_DURACAO_DERROTA_COMO_J1, "Tempo decorrido até à derrota como jogador brancas");
	    addColumn(TOTAL_DURACAO_DERROTA_COMO_J2, "Tempo decorrido até à derrota como jogador pretas");
	    addColumn(TOTAL_JOGADAS_VITORIA, "Total de jogadas até à vitória");
	    addColumn(TOTAL_JOGADAS_DERROTA, "Total de jogadas até à derrota");
	    addColumn(TOTAL_DURACAO_VITORIA, "Tempo decorrido até à vitória");
	    addColumn(TOTAL_DURACAO_DERROTA, "Tempo decorrido até à derrota");
	    addColumn(TOTAL_VITORIAS, "Total de vitórias");
	    addColumn(TOTAL_DERROTAS, "Total de derrotas");
	    addColumn(TOTAL_JOGOS, "Total de jogos");
	    addColumn(PERCENTAGEM_VITORIAS, "Percentagem de vitórias");
	    addColumn(PERCENTAGEM_DERROTAS, "Percentagem de derrotas");
	}
	return funcoesAvaliacaoDesc;
    }
    
    private static void addColumn(String key, String label){
	funcoesAvaliacaoDesc.put(key, label);
	columnsOrder.addLast(key);
    }
    
    public static LinkedList<String> getColumns(){
	if(columnsOrder == null){
	    getFuncaoCoeficienteDescs();
	}
	return columnsOrder;
    }
    
    /**
     * Obtem a descrição da função peso associada
     * 
     * @param funcaoId com o identificador da função
     * @return String com a descrição
     */
    public static String getFuncaoCoeficienteDesc(String funcaoId){
	if(funcoesAvaliacaoDesc == null){
	    getFuncaoCoeficienteDescs();
	}
	return funcoesAvaliacaoDesc.containsKey(funcaoId)?funcoesAvaliacaoDesc.get(funcaoId):funcaoId;
    }

    /**
     * @return the profundidade
     */
    public int getProfundidade() {
	return this.profundidade;
    }

    /**
     * @param profundidade the profundidade to set
     */
    public void setProfundidade(int profundidade) {
	this.profundidade = profundidade;
    }

    /**
     * @return the algoritmo
     */
    public String getAlgoritmo() {
	return this.algoritmo;
    }

    /**
     * @param algoritmo the algoritmo to set
     */
    public void setAlgoritmo(String algoritmo) {
	this.algoritmo = algoritmo;
    }
}
