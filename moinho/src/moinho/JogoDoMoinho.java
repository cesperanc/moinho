package moinho;

import db.Peso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import jogos.*;

public class JogoDoMoinho extends Jogo {
    
    private char simboloAgente;
    private char simboloOponente;
    
    private Peso peso = null;

    /**
     * Constructor do Jogo do Moinho
     * 
     * @param estadoInicial com o estado inicial
     * @param simboloAgente com o símbolo do agente
     * @param profundidadeMaxima 
     * @param operadores com a lista de operadores a aplicar
     */ //_TODO
    public JogoDoMoinho(EstadoJogoMoinho estadoInicial, char simboloAgente, int profundidadeMaxima) {
	this(estadoInicial, simboloAgente, profundidadeMaxima, Peso.getCoeficientesPorOmissao(), AlfaBeta.NOME);
    }

    /**
     * Constructor do Jogo do Moinho
     * 
     * @param estadoInicial com o estado inicial
     * @param simboloAgente com o símbolo do agente
     * @param profundidadeMaxima 
     * @param coeficientesAvalicao 
     * @param algoritmo 
     */
    public JogoDoMoinho(EstadoJogoMoinho estadoInicial, char simboloAgente, int profundidadeMaxima, HashMap<String, Integer> coeficientesAvalicao, String algoritmo) {
        super(estadoInicial, null, profundidadeMaxima);
        this.simboloAgente = simboloAgente;
        this.simboloOponente = (simboloAgente == EstadoJogoMoinho.X) ? EstadoJogoMoinho.O : EstadoJogoMoinho.X;
	this.peso = new Peso(profundidadeMaxima, algoritmo, coeficientesAvalicao);
    }
    
    /**
     * Aplica (se possível) os diferentes operadores ao estado sucessor criado a partir do estado actual
     * 
     * @param estado a aplicar os operadores
     * @return lista de sucessores do estado actual
     */ //_TODO
    @Override
    public List<Estado> aplicaOperadores(Estado estado) {
    	List<Estado> listaSucessores = new LinkedList<Estado>();
	
        EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
	ArrayList<Posicao> posicoes = (ArrayList<Posicao>) e.getPosicoes();
	for (Posicao posicao : posicoes) {
	    int linha = posicao.getLinha();
	    int coluna = posicao.getColuna();
	    
	    if(this.aplicarOperadorComerPeca(listaSucessores, e, linha, coluna)==null){
		// Se conseguimos remover a peça, então vamos ver para onde a podemos mover
		if(this.aplicarOperadorRemoverPeca(listaSucessores, e, linha, coluna)==null){
		    // Se não conseguimos comer ou mover uma peça, então vamos ver se a podemos colocar
		    this.aplicarOperadorColocarPeca(listaSucessores, e, linha, coluna);
		}
	    }
	}
        return listaSucessores;
    }
    
    private OperadorComerPeca aplicarOperadorComerPeca(List<Estado> listaSucessores, EstadoJogoMoinho e, int linha, int coluna ){
	EstadoJogoMoinho sucessor = (EstadoJogoMoinho) e.clone();
	
	OperadorComerPeca operadorComerPeca = (OperadorComerPeca) OperadorComerPeca.executar(sucessor, linha, coluna);
	
	if(operadorComerPeca!=null){
	    // Se conseguimos comer a peça, então adicionamos o sucessor à lista de sucessores
	    this.addSucessor(listaSucessores, sucessor);
	}
	return operadorComerPeca;
    }
    
    private OperadorRemoverPeca aplicarOperadorRemoverPeca(List<Estado> listaSucessores, EstadoJogoMoinho e, int linha, int coluna ){
	EstadoJogoMoinho sucessor = (EstadoJogoMoinho) e.clone();
	
	OperadorRemoverPeca operadorRemoverPeca = (OperadorRemoverPeca) OperadorRemoverPeca.executar(sucessor, linha, coluna);
	if(operadorRemoverPeca!=null){
	    if(listaSucessores==null || this.addSucessor(listaSucessores, sucessor)){

		ArrayList<Posicao> posicoesAcessiveis = (ArrayList<Posicao>) sucessor.getPosicoesParaOndePodeMover(linha, coluna);
		for (Posicao posicaoAcessivel : posicoesAcessiveis) {
		    // Se conseguimos colocar uma peça, então é um sucessor válido
		    OperadorColocarPeca operadorColocarPeca = this.aplicarOperadorColocarPeca(null, sucessor, posicaoAcessivel.getLinha(), posicaoAcessivel.getColuna());
		    if(operadorColocarPeca!=null){
			operadorRemoverPeca.setOperadorSeguinte(operadorColocarPeca);
		    }
		}
	    }
	}
	return operadorRemoverPeca;
    }
    
    private OperadorColocarPeca aplicarOperadorColocarPeca(List<Estado> listaSucessores, EstadoJogoMoinho e, int linha, int coluna ){
	EstadoJogoMoinho sucessor = (EstadoJogoMoinho) e.clone();
	
	OperadorColocarPeca operadorColocarPeca = (OperadorColocarPeca) OperadorColocarPeca.executar(sucessor, linha, coluna);
	if(operadorColocarPeca!=null){
	    if(listaSucessores==null || this.addSucessor(listaSucessores, sucessor)){

		// Se temos três em linha, então
		if(sucessor.jogadorTemTresEmLinha(linha, coluna)){
		    ArrayList<Posicao> posicoesTabuleiro = (ArrayList<Posicao>) sucessor.getPosicoes();
		    for (Posicao posicaoTabuleiro : posicoesTabuleiro) {
			if(sucessor.jogadorPodeComerPeca(posicaoTabuleiro.getLinha(), posicaoTabuleiro.getColuna())){
			    // Se conseguimos comer uma peça, definir operador comer peca como sendo o operador seguinte
			    OperadorComerPeca operadorComerPeca = this.aplicarOperadorComerPeca(null, sucessor, posicaoTabuleiro.getLinha(), posicaoTabuleiro.getColuna());
			    if(operadorComerPeca!=null){
				operadorColocarPeca.setOperadorSeguinte(operadorComerPeca);
			    }
			}
		    }
		}
	    }
	}
	return operadorColocarPeca;
    }

    /**
     * Função utilidade que verifica se estado actual é de vencedor, vencido ou empate
     * 
     * @param estado
     * @return 
     */ //_TODO
    @Override
    public double utilidade(Estado estado) {
    	
    	if (((EstadoJogoMoinho) estado).isVencedor(simboloAgente)) {
            return Double.POSITIVE_INFINITY;
        } else if (((EstadoJogoMoinho) estado).isVencedor(simboloOponente)) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return 0;
        }
    }

    /**
     * Função de avaliação do estado que verifica se estado actual é de vencedor, vencido, empate ou outro valor intermédio
     * 
     * @param estado
     * @return 
     */ //_TODO
    @Override
    public double funcaoAvaliacao(Estado estado) {
    	if (terminou(estado)) {
            return utilidade(estado);
        }
        return ((EstadoJogoMoinho) estado).avaliacao(simboloAgente, this);
    }
    
    /**
     * Adiciona um estado à lista de estados recentes
     * @param estado
     * @return 
     */
    @Override
    public boolean addEstado(Estado estado){
	EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
	// Não vamos guardar nenhum estado quando as peças ainda estão a ser colocadas no tabuleiro
	if(e.jogadorPodeColocarPecas(simboloAgente)){
	    return true;
	}
	return super.addEstado(estado);
    }
    
    /**
     * Limpa os estados antigos da cache de estados do jogo actual
     * @param estado 
     */
    @Override
    public void cleanEstadosAntigos(Estado estado){
	int iSpaces = estado.toString().replaceAll("[^"+EstadoJogoMoinho.VAZIO+"]", "").length();
	
	ArrayList<String> estados = new ArrayList<String>(this.cacheEstados.keySet());
	for(String sEstado : estados){
	    // se a diferença entre o número de espaços de um dos estados anteriores e o actual for superior a 2, remover o estado antigo
	    if(Math.abs(iSpaces-sEstado.toString().replaceAll("[^"+EstadoJogoMoinho.VAZIO+"]", "").length())>=2){
		this.cacheEstados.remove(sEstado);
	    }
	}
    }
    
    /**
     * Obtem o tamanho da cache para os estados anteriores
     * 
     * @return 
     */
    public int getCacheSize(){
	return this.cacheEstados.size();
    }
    
    /**
     * Adiciona um novo sucessor à lista de sucessores, caso o estado não exista
     * @param listaSucessores
     * @param sucessor
     * @return 
     */
    public boolean addSucessor(List<Estado> listaSucessores, Estado sucessor){
	if(listaSucessores!=null && !listaSucessores.contains(sucessor)){
	    return listaSucessores.add(sucessor);
	}
	return false;
    }
    /**
     * Define o peso
     * @param peso 
     */
    public void setPeso(Peso peso){
	this.peso = peso;
    }
    
    /**
     * Obtem o peso
     * @return peso 
     */
    public Peso getPeso(){
	return this.peso;
    }
}
