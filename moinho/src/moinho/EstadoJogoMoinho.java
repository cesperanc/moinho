package moinho;

import db.Peso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jogos.*;

public class EstadoJogoMoinho extends Estado {

    public static final int DIMENSAO = 7;
    public static final char VAZIO = ' ';
    public static final char V = VAZIO;
    public static final char X = 'X';
    public static final char O = 'O';
    public static final int NUMEROMAXIMOPECAS = 9;
    
    private char[][] tabuleiro;
    private char jogadorActual = X;
    
    // Guarda um registo de todas as posições de jogo válidas para este tabuleiro
    private HashMap<String, Posicao> relacoesEntrePosicoes = new HashMap<String, Posicao> ();
    
    // Guarda o número de peças máximo permitido por jogador
    private HashMap<Character, Integer> numeroPecasMaximo = new HashMap<Character, Integer>();
    
    // Flag que especifica se um dado jogador pode ou não comer peças
    private HashMap<Character, Boolean> podeComerPeca = new HashMap<Character, Boolean>();
    
    // Posição da última peça removida
    private Posicao ultimaPecaRemovida = null;
    
    /**
     * Define o estado por omissão do jogo do moinho
     * 
     */ //_TODO
    public EstadoJogoMoinho() {
        this.reiniciar();
    }
    
    /**
     * Cria um estado a partir de um tabuleiro fornecido e define a próxima peça
     * 
     * @param tabuleiro com a matrix que define o tabuleiro
     * @param jogadorActual com o caracter que representa a próxima peça
     */ //_TODO
    public EstadoJogoMoinho(char[][] tabuleiro, char proximaPeca) {
	this._init();
	
    	this.tabuleiro = new char[tabuleiro.length][tabuleiro.length];

        for (int i = 0; i < tabuleiro.length; i++) {
	    System.arraycopy(tabuleiro[i], 0, this.tabuleiro[i], 0, tabuleiro[i].length);
        }
        this.jogadorActual = proximaPeca;
    }
    
    /**
     * Cria um estado a partir de um tabuleiro fornecido e define a próxima peça
     * 
     * @param tabuleiro com a matrix que define o tabuleiro
     * @param jogadorActual com o caracter que representa a próxima peça
     * @param numeroPecasMaximoX numero de peças máximo em jogo para o jogador X
     * @param numeroPecasMaximoO numero de peças máximo em jogo para o jogador O
     */ //_TODO
    public EstadoJogoMoinho(char[][] tabuleiro, char proximaPeca, int numeroPecasMaximoX, int numeroPecasMaximoO) {
	this(tabuleiro, proximaPeca);
	
	// Define o número máximo de peças em jogo para cada jogador
	this.numeroPecasMaximo.put(X, numeroPecasMaximoX);
	this.numeroPecasMaximo.put(O, numeroPecasMaximoO);
    }
    
    /**
     * Cria um estado a partir de um tabuleiro fornecido e define a próxima peça
     * 
     * @param tabuleiro com a matrix que define o tabuleiro
     * @param jogadorActual com o caracter que representa a próxima peça
     * @param numeroPecasMaximoX numero de peças máximo em jogo para o jogador X
     * @param XpodeComerPeca flag para saber se o jogador X pode comer peças
     * @param OpodeComerPeca flag para saber se o jogador O pode comer peças
     */ //_TODO
    public EstadoJogoMoinho(char[][] tabuleiro, char proximaPeca, int numeroPecasMaximoX, int numeroPecasMaximoO, Posicao ultimaPecaRemovida, boolean XpodeComerPeca, boolean OpodeComerPeca) {
	this(tabuleiro, proximaPeca, numeroPecasMaximoX, numeroPecasMaximoO);
	
	this.podeComerPeca.put(X, XpodeComerPeca);
	this.podeComerPeca.put(O, OpodeComerPeca);
	
	this.ultimaPecaRemovida = ultimaPecaRemovida;
    }

    /**
     * Reinicia o tabuleiro
     * 
     */ //_TODO
    public final void reiniciar(){
	this._init();
	// Reinicia o tabuleiro de jogo
    	for (int i = 0; i < this.tabuleiro.length; i++) {
            for (int j = 0; j < this.tabuleiro[i].length; j++) {
                this.tabuleiro[i][j] = VAZIO;
            }
        }
	/*
	char[][] tabuleiroTmp = {
	    {X,V,V,O,V,V,V},
	    {V,X,V,O,V,O,V},
	    {V,V,X,V,O,V,V},
	    {V,X,X,V,X,V,O},
	    {V,V,X,V,O,V,V},
	    {V,X,V,V,V,O,V},
	    {X,V,V,V,V,V,V}
	};
	this.tabuleiro = tabuleiroTmp;
	
	this.numeroPecasMaximo.put(X, 9);
	this.numeroPecasMaximo.put(O, 7);
	*/
	// Define o primeiro jogador
        this.jogadorActual = X;
	
        this.fireJogoChanged();
    }
    
    private void _init(){
	
	this.tabuleiro = new char[DIMENSAO][DIMENSAO];
	
	// Tabela das posições que representam as possibilidades de três peças em linha
	Posicao[][] posicoesVitoria = {
	    { new Posicao(0, 0), new Posicao(0, 3), new Posicao(0, 6) },
	    { new Posicao(1, 1), new Posicao(1, 3), new Posicao(1, 5) },
	    { new Posicao(2, 2), new Posicao(2, 3), new Posicao(2, 4) },
	    { new Posicao(3, 0), new Posicao(3, 1), new Posicao(3, 2) },
	    { new Posicao(3, 4), new Posicao(3, 5), new Posicao(3, 6) },
	    { new Posicao(4, 2), new Posicao(4, 3), new Posicao(4, 4) }, 
	    { new Posicao(5, 1), new Posicao(5, 3), new Posicao(5, 5) },
	    { new Posicao(6, 0), new Posicao(6, 3), new Posicao(6, 6) },
	    { new Posicao(0, 0), new Posicao(3, 0), new Posicao(6, 0) },
	    { new Posicao(1, 1), new Posicao(3, 1), new Posicao(5, 1) },
	    { new Posicao(2, 2), new Posicao(3, 2), new Posicao(4, 2) },
	    { new Posicao(0, 3), new Posicao(1, 3), new Posicao(2, 3) },
	    { new Posicao(4, 3), new Posicao(5, 3), new Posicao(6, 3) },
	    { new Posicao(2, 4), new Posicao(3, 4), new Posicao(4, 4) },
	    { new Posicao(1, 5), new Posicao(3, 5), new Posicao(5, 5) },
	    { new Posicao(0, 6), new Posicao(3, 6), new Posicao(6, 6) }
	};
	
	// Cria um grafo de relações entre as posições do tabuleiro baseado nas posições possíveis no mesmo
	for (int i = 0; i < posicoesVitoria.length; i++) {
            for (int j = 0; j < posicoesVitoria[i].length; j++) {
		if(!this.relacoesEntrePosicoes.containsKey(posicoesVitoria[i][j].toString())){
		    // Se a chave não existe, esta posição ainda não existe, por isso deve ser importada
		    this.relacoesEntrePosicoes.put(posicoesVitoria[i][j].toString(), posicoesVitoria[i][j]);
		}else{
		    // Se a chave existe, então importar a referência da posição existente e substituir na matriz de posições original
		    posicoesVitoria[i][j] = this.relacoesEntrePosicoes.get(posicoesVitoria[i][j].toString());
		}
		// Inserir as relações em cada uma das posições
		int k=j-1;
		while(k>=0){
		    posicoesVitoria[i][j].addPosicaoVizinha(posicoesVitoria[i][k]);
		    k--;
		}
            }
        }
	
	// Define o número máximo de peças em jogo para cada jogador
	this.numeroPecasMaximo.put(X, NUMEROMAXIMOPECAS);
	this.numeroPecasMaximo.put(O, NUMEROMAXIMOPECAS);
	
	// Inicialmente nenhum jogador pode comer peças
	this.podeComerPeca.put(X, false);
	this.podeComerPeca.put(O, false);
	
	// Remove a referência à última peça removida
	this.ultimaPecaRemovida = null;
	
	this.isEmpate = false;
	
    }

    /**
     * Verifica se a linha e coluna existem no tabuleiro, se a respectiva posição está disponível e a posição existe na matriz de posições de vitória
     * 
     * @param linha com a linha da posição a validar
     * @param coluna com a coluna da posição a validar
     * @return boolean true se a jogada é válida, falso caso contrário
     */ //_TODO
    public boolean isJogadaValida(int linha, int coluna) {
        return (
	    !this.podeComerPeca(this.jogadorActual) &&
	    this.isPosicaoLivre(linha, coluna) && 
	    this.getNumeroDePecasJogadorActual()<this.getNumeroMaximoDePecasJogadorActual() && 
	    (!this.isMover() || this.pecaEmCachePodeSerMovida(linha, coluna, this.jogadorActual))
	);
    }
    
    /**
     * Obtem o número máximo de peças que o jogador pode ter em jogo
     * 
     * @param char com o caracter do jogador
     * @return int com o número de peças do jogador
     */
    public int getNumeroMaximoDePecasJogador(char jogador){
        return this.numeroPecasMaximo.get(jogador);
    }
    
    /**
     * Obtem o número máximo de peças que o jogador actual pode ter em jogo
     * 
     * @return int com o número de peças do jogador actual
     */
    public int getNumeroMaximoDePecasJogadorActual(){
        return this.getNumeroMaximoDePecasJogador(this.jogadorActual);
    }
    
    /**
     * Obtem o número total de peças para o jogador
     * 
     * @param char com o caracter do jogador
     * @return int com o número de peças do jogador
     */
    public int getNumeroDePecasJogador(char jogador){
	// Quantas peças é que o jogador já tem em jogo?
	int nPecas = 0;
	for (int i = 0; i < this.tabuleiro.length; i++) {
            for (int j = 0; j < this.tabuleiro[i].length; j++) {
                if(this.tabuleiro[i][j] == jogador){
		    nPecas++;
		}
            }
        }
        return nPecas;
    }
    
    /**
     * Obtem o número total de peças para o jogador actual
     * 
     * @return int com o número de peças do jogador actual
     */
    public int getNumeroDePecasJogadorActual(){
        return this.getNumeroDePecasJogador(this.jogadorActual);
    }
    
    /**
     * Obtem o número total de peças para o jogador adversário
     * 
     * @return int com o número de peças do adversário
     */
    public int getNumeroDePecasJogadorAdversario(){
        return this.getNumeroDePecasJogador((this.jogadorActual==X)?O:X);
    }
    
    /**
     * Verifica se a posição é válida na matriz de jogo
     * 
     * @param linha com a linha da posição a validar
     * @param coluna com a coluna da posição a validar
     * @return boolean true se a posição é válida, falso caso contrário
     */
    public boolean isPosicaoValida(int linha, int coluna){
	if(linha < 0 || coluna < 0 || linha >= tabuleiro.length || coluna >= tabuleiro[0].length){
	    return false;
	}
        return this.relacoesEntrePosicoes.containsKey((new Posicao(linha, coluna)).toString());
    }
    
    /**
     * Verifica se a posição é válida e está livre na matriz de jogo
     * 
     * @param linha com a linha da posição a validar
     * @param coluna com a coluna da posição a validar
     * @return boolean true se a posição é válida, falso caso contrário
     */
    public boolean isPosicaoLivre(int linha, int coluna){
	return (this.isPosicaoValida(linha, coluna) && tabuleiro[linha][coluna] == VAZIO);
    }

    /**
     * Coloca a peça no tabuleiro, se possível (não notificando listenners
     * 
     * @param linha com a linha da posição a colocar a peça
     * @param coluna com a coluna da posição a colocar a peça
     * @return boolean true se a peça foi colocada, false caso contrário
     */ //_TODO
    protected boolean _colocarPeca(int linha, int coluna) {
	// Se a jogada é válida
    	if (this.isJogadaValida(linha, coluna)) {
	    this.tabuleiro[linha][coluna] = this.jogadorActual;
	    // Se o jogar não obteve três em linha nesta jogada, então passa a vez ao adversário
	    if(!this.jogadorTemTresEmLinha(linha, coluna) && (!this.isMover() || (this.isMover() && !this.ultimaPecaRemovida.equals(this.getPosicao(linha, coluna))))){
		this.trocarJogadorActual();
	    }else{
		this.jogadorTresEmLinha(linha, coluna);
	    }
	    this.ultimaPecaRemovida = null;
	    
	    return true;
	}
        return false;
    }

    /**
     * Coloca a peça no tabuleiro, se possível
     * 
     * @param linha com a linha da posição a colocar a peça
     * @param coluna com a coluna da posição a colocar a peça
     * @return boolean true se a peça foi colocada, false caso contrário
     */ //_TODO
    public boolean colocarPeca(int linha, int coluna) {
	// Se a jogada é válida
    	if (this._colocarPeca(linha, coluna)) {
	    fireJogoChanged(new JogoEvent(this));
	    return true;
	}
        return false;
    }
    
    /**
     * Remove a peça do jogador actual na posição do tabuleiro
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça foi removida, false caso contrário
     */
    protected boolean _removerPeca(int linha, int coluna) {
    	if (this.isPecaDoJogador(linha, coluna) && !this.isMover()) {
	    this.tabuleiro[linha][coluna] = VAZIO;
	    
	    this.ultimaPecaRemovida = this.getPosicao(linha, coluna);

	    return true;
	}
        return false;
    }
    
    /**
     * Remove a peça do jogador actual na posição do tabuleiro
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça foi removida, false caso contrário
     */
    public boolean removerPeca(int linha, int coluna) {
    	if (this._removerPeca(linha, coluna)) {
	    fireJogoChanged(new JogoEvent(this));

	    return true;
	}
        return false;
    }
    
    /**
     * Remove a peça do jogador actual na posição do tabuleiro (não notificando listenners)
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça foi removida, false caso contrário
     */
    protected boolean _comerPeca(int linha, int coluna) {
    	if (this.jogadorPodeComerPeca(linha, coluna)) {
	    // Limpamos a peça
	    this.tabuleiro[linha][coluna] = VAZIO;
	    
	    // Diminuímos o número de peças máximo do utilizador
	    this.numeroPecasMaximo.put(this.getAdversario(), this.numeroPecasMaximo.get(this.getAdversario())-1);
	    
	    // se comeu uma peça, este jogador já não pode comer mais nesta jogada
	    this.podeComerPeca.put(this.jogadorActual, false);
	    
	    // passa a vez ao jogador seguinte
	    this.trocarJogadorActual();

	    return true;
	}
        return false;
    }
    
    /**
     * Remove a peça do jogador actual na posição do tabuleiro
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça foi removida, false caso contrário
     */
    public boolean comerPeca(int linha, int coluna) {
    	if (this._comerPeca(linha, coluna)) {
	    
	    fireJogoChanged(new JogoEvent(this));

	    return true;
	}
        return false;
    }
    
    /**
     * Obtém o caracter do jogador adversário
     * 
     * @return char com o caracter do adversário
     */
    public char getAdversario() {
    	return this.getAdversario(this.jogadorActual);
    }
    
    /**
     * Obtém o caracter do jogador actual
     * 
     * @return char com o caracter do actual
     */
    public char getJogador() {
    	return this.jogadorActual;
    }
    
    /**
     * Obtém o caracter do jogador adversário
     * 
     * @param jogador com o simbolo do jogador para o qual se pretende obter o adversário
     * @return char com o caracter do adversário
     */
    public char getAdversario(char jogador) {
    	return (jogador==X?O:X);
    }
    
    /**
     * Verifica se a peça na posição especificada é do adversário
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça pertence ao adversário, false caso contrário
     */
    public boolean isPecaDoAdversario(int linha, int coluna) {
    	return this.isPecaDe(this.getAdversario(), linha, coluna);
    }
    
    /**
     * Verifica se a peça na posição especificada é do jogador
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça pertence ao jogador, false caso contrário
     */
    public boolean isPecaDoJogador(int linha, int coluna) {
    	return this.isPecaDe(this.jogadorActual, linha, coluna);
    }
    
    /**
     * Verifica se a peça na posição especificada é do jogador
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return boolean true se a peça pertence ao jogador, false caso contrário
     */
    public boolean isPecaDe(char jogador, int linha, int coluna) {
    	return (isPosicaoValida(linha, coluna) && this.tabuleiro[linha][coluna] == jogador);
    }
    
    /**
     * Troca o jogador em jogo
     * 
     * @return o novo jogador
     */
    public char trocarJogadorActual(){
	this.jogadorActual = (this.jogadorActual == X )? O : X;
	return this.jogadorActual;
    }
    
    /**
     * Verifica se o jogo está no estado de movimento
     * 
     * @return o novo jogador
     */
    public boolean isMover(){
	return (this.ultimaPecaRemovida!=null);
    }
    
    /**
     * Verifica se as peças do jogador podem voar no tabuleiro
     * 
     * @return true se sim, false caso contrário
     */
    public boolean pecasPodemVoar(char jogador){
	return (this.numeroPecasMaximo.get(jogador)==3);
    }
    
    /**
     * Verifica se as peças do jogador podem voar no tabuleiro
     * 
     * @return true se sim, false caso contrário
     */
    public boolean pecasDoJogadorPodemVoar(){
	return this.pecasPodemVoar(this.jogadorActual);
    }
    
    /**
     * Verifica se as peças do adversário podem voar no tabuleiro
     * 
     * @return true se sim, false caso contrário
     */
    public boolean pecasDoAdversarioPodemVoar(){
	return this.pecasPodemVoar(this.getAdversario());
    }
    
    /**
     * Verifica se o simbolo é um vencedor
     * 
     * @param jogador com simbolo do jogador
     * @return true se o jogador é um vencedor, false caso contrário
     */ //_TODO
    public boolean isVencedor(char jogador) {
	// Se o jogador adversario só puder jogar com menos do que 3 peças, então o jogo correu bem...
	if(this.getNumeroMaximoDePecasJogador(this.getAdversario(jogador))<3 || (!this.isEmpate(jogador) && !this.jogadorPodeColocarPecas(this.getAdversario()) && this.numeroDePecasComMobilidade(this.getAdversario())<=0)){
	    return true;
	}
    	return false;
    }
    
    /**
     * Verifica se foi verificado um empate entre o jogador e o respectivo adversário
     * 
     * @param jogador actual
     * @return true se foi encontrado um empate, falso caso contrário
     */
    public boolean isEmpate(char jogador) {
	// Se o jogador adversario só puder jogar com menos do que 3 peças, então o jogo correu bem...
	if(this.isEmpate || (!this.jogadorPodeColocarPecas(jogador) && !this.jogadorPodeColocarPecas(this.getAdversario()) && this.numeroDePecasComMobilidade(jogador)<=0 && this.numeroDePecasComMobilidade(this.getAdversario())<=0)){
	    return true;
	}
    	return false;
    }
    
    /**
     * Verifica se o jogador tem três peças em linha numa dada posição
     * 
     * @param jogador com o caracter do jogador
     * @param linha com a linha da nova posição
     * @param coluna com a coluna da nova posição
     * @return true se o jogador tem peças em linha, false caso contrário
     */
    public boolean jogadorTemTresEmLinha(char jogador, int linha, int coluna){
	if(this.isPecaDe(jogador, linha, coluna)){
	    // Verificação dos 3 em linha
	    Posicao posicao = this.getPosicao(linha, coluna);
	    for (ArrayList<Posicao> linhaDeVizinhos : posicao.getPosicoesVizinhas()) {
		int casasPreenchidas = ((this.tabuleiro[posicao.getLinha()][posicao.getColuna()]==jogador)?1:0);
		for (Posicao posicaoVizinha : linhaDeVizinhos) {
		    if(this.tabuleiro[posicaoVizinha.getLinha()][posicaoVizinha.getColuna()]==jogador){
			if(++casasPreenchidas>=3){
			    return true;
			}
		    }else{
			break;
		    }
		}
	    }
	}
    	return false;
    }
    
    /**
     * Verifica se o jogador actual tem três peças em linha numa dada posição
     * 
     * @param linha com a linha da nova posição
     * @param coluna com a coluna da nova posição
     * @return true se o jogador tem peças em linha, false caso contrário
     */
    public boolean jogadorTemTresEmLinha(int linha, int coluna){
	return this.jogadorTemTresEmLinha(this.jogadorActual, linha, coluna);
    }
    
    /**
     * Verifica se o adversário tem três peças em linha numa dada posição
     * 
     * @param linha com a linha da nova posição
     * @param coluna com a coluna da nova posição
     * @return true se o adversário tem peças em linha, false caso contrário
     */
    public boolean adversarioTemTresEmLinha(int linha, int coluna){
	return this.jogadorTemTresEmLinha(this.getAdversario(), linha, coluna);
    }
    
    /**
     * Verifica se o jogador tem três peças em linha numa dada posição
     * 
     * @param jogador com o caracter do jogador
     * @param linha com a linha da nova posição
     * @param coluna com a coluna da nova posição
     * @return true se o jogador tem peças em linha, false caso contrário
     */
    public boolean jogadorTresEmLinha(char jogador, int linha, int coluna){
	if(this.jogadorTemTresEmLinha(jogador, linha, coluna)){
	    this.podeComerPeca.put(jogador, true);
	    return true;
	}
    	return false;
    }
    
    /**
     * Verifica se o jogador tem três peças em linha numa dada posição
     * 
     * @param linha com a linha da nova posição
     * @param coluna com a coluna da nova posição
     * @return true se o jogador tem peças em linha, false caso contrário
     */
    public boolean jogadorTresEmLinha(int linha, int coluna){
	return (this.jogadorTresEmLinha(this.jogadorActual, linha, coluna));
    }
    
    /**
     * Verifica se o adversario tem três peças em linha numa dada posição
     * 
     * @param linha com a linha da nova posição
     * @param coluna com a coluna da nova posição
     * @return true se o jogador tem peças em linha, false caso contrário
     */
    public boolean adversarioTresEmLinha(int linha, int coluna){
	return (this.jogadorTresEmLinha(this.getAdversario(), linha, coluna));
    }
    
    /**
     * Verifica se um dado jogador pode comer uma peça do adversário
     * 
     * @param jogador com o jogador a verificar
     * @return true se o jogador pode comer uma peça, false caso contrário
     */
    public boolean podeComerPeca(char jogador){
	return this.podeComerPeca.get(jogador);
    }
    
    /**
     * Verifica se o jogador pode comer uma peça do adversário
     * 
     * @return true se o jogador pode comer uma peça, false caso contrário
     */
    public boolean jogadorPodeComerPeca(){
	return this.podeComerPeca.get(this.jogadorActual);
    }
    
    /**
     * Verifica se o jogador pode comer uma peça do adversário
     * 
     * @param linha com a linha da posição da peça a ser comida
     * @param coluna com a coluna da posição da peça a ser comida
     * @return true se o jogador pode comer uma peça, false caso contrário
     */
    public boolean jogadorPodeComerPeca(int linha, int coluna){
	return (
	    this.podeComerPeca.get(this.jogadorActual) && 
	    this.isPecaDoAdversario(linha, coluna) && 
	    (
		!this.jogadorTemTresEmLinha(this.getAdversario(), linha, coluna) || 
		(this.getNumeroDePecasJogadorAdversario()==this.a6_QuantasPecasEmTresEmLinha(this.getAdversario()))
	    )
	);
    }
    
    /**
     * Verifica se o adversário pode comer uma peça do jogador
     * 
     * @return true se o adversário pode comer uma peça, false caso contrário
     */
    public boolean adversarioPodeComerPeca(){
	return this.podeComerPeca.get(this.getAdversario());
    }
    
    /**
     * Verifica se um dado jogador pode ou não colocar peças no tabuleiro
     * 
     * @return true se sim, false caso contrário
     */
    public boolean jogadorPodeColocarPecas(char jogador){
	return this.getNumeroDePecasJogador(jogador)<this.getNumeroMaximoDePecasJogador(jogador);
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param linhaOrigem
     * @param colunaOrigem 
     * @param linhaDestino
     * @param colunaDestino 
     * @return true se a peça pode ser movida, false caso contrário
     */
    public boolean pecaPodeSerMovida(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino, char jogador){
	return this.pecaPodeSerMovida(this.getPosicao(linhaOrigem, colunaOrigem), this.getPosicao(linhaDestino, colunaDestino), jogador);
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param origem com a posição origem
     * @param destino com a posição destino
     * @return true se a peça pode ser movida, false caso contrário
     */
    public boolean pecaPodeSerMovida(Posicao origem, Posicao destino, char jogador){
	return (this.pecasPodemVoar(jogador) || (/*!this.isMover() && !this.pecasDoJogadorPodemVoar() && */origem.isVizinhoProximo(destino)));
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param destino com a posição destino
     * @return true se a peça pode ser movida, false caso contrário
     */
    public boolean pecaEmCachePodeSerMovida(Posicao destino, char jogador){
	return (this.pecasPodemVoar(jogador) || (this.isMover() && this.pecaPodeSerMovida(this.ultimaPecaRemovida, destino, jogador)));
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param linhaDestino
     * @param colunaDestino 
     * @return true se a peça pode ser movida, false caso contrário
     */
    public boolean pecaEmCachePodeSerMovida(int linhaDestino, int colunaDestino, char jogador){
	return this.pecaEmCachePodeSerMovida(this.getPosicao(linhaDestino, colunaDestino), jogador);
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return true se a peça pode ser movida, false caso contrário
     */
    public boolean pecaPodeSerMovida(int linha, int coluna){
	return this.pecaPodeSerMovida(linha, coluna, this.jogadorActual);
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return true se a peça pode ser movida, false caso contrário
     */
    public boolean pecaPodeSerMovida(int linha, int coluna, char jogador){
	return (this.pecaPodeSerMovidaParaQuantasPosicoes(linha, coluna, jogador)>0);
    }
    
    /**
     * Verifica se a peça de uma dada posição pode ser movida
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return true se a peça pode ser movida, false caso contrário
     */
    public int pecaPodeSerMovidaParaQuantasPosicoes(int linha, int coluna, char jogador){
	int posicoesLivres = 0;
	if(this.getNumeroDePecasJogador(jogador) == this.getNumeroMaximoDePecasJogador(jogador) && this.isPosicaoValida(linha, coluna) && this.isPecaDe(jogador, linha, coluna)){
	    for (ArrayList<Posicao> linhaDeVizinhos : this.getPosicao(linha, coluna).getPosicoesVizinhas()) {
		for (Posicao posicaoVizinha : linhaDeVizinhos) {
		    if(this.isPosicaoLivre(posicaoVizinha.getLinha(), posicaoVizinha.getColuna()) && this.pecaPodeSerMovida(this.getPosicao(linha, coluna), posicaoVizinha, jogador)){
			posicoesLivres++;
		    }
		}
	    }
	}
	return posicoesLivres;
    }
    
    /**
     * Relativamente à posição actual, quais as posições para uma peça nesta posição se pode mover
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return lista com as posições acessíveis a partir desta posição
     */
    public List<Posicao> getPosicoesParaOndePodeMover(int linha, int coluna){
	return this.getPosicoesParaOndePodeMover(linha, coluna, this.jogadorActual);
    }
    
    /**
     * Relativamente à posição actual, quais as posições para uma peça nesta posição se pode mover
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return lista com as posições acessíveis a partir desta posição
     */
    public List<Posicao> getPosicoesParaOndePodeMover(int linha, int coluna, char jogador){
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>();
	if(this.pecasDoJogadorPodemVoar()){
	    ArrayList<Posicao> posicoesTabuleiro = (ArrayList<Posicao>) this.getPosicoes();
	    for(Posicao posicao : posicoesTabuleiro){
		if(!posicao.equals(new Posicao(linha, coluna)) && this.isPosicaoLivre(posicao.getLinha(), posicao.getColuna())){
		    posicoes.add(posicao);
		}
	    }
	}else{
	    if(this.isPosicaoValida(linha, coluna)){
		for (ArrayList<Posicao> linhaDeVizinhos : this.getPosicao(linha, coluna).getPosicoesVizinhas()) {
		    for (Posicao posicaoVizinha : linhaDeVizinhos) {
			if(this.isPosicaoLivre(posicaoVizinha.getLinha(), posicaoVizinha.getColuna()) && this.pecaPodeSerMovida(this.getPosicao(linha, coluna), posicaoVizinha, jogador)){
			    posicoes.add(posicaoVizinha);
			}
		    }
		}
	    }
	}
	return posicoes;
    }
    
    /**
     * Função de avaliação de um estado para um determinado jogador
     * 
     * @param jogador com o simbolo do jogador
     * @return classificação numérica do estado
     */ //_TODO
    public double avaliacao(char jogador, JogoDoMoinho jogo) {
        double pontuacao = 0;
	
        //Coeficientes Positivos
        pontuacao += this.a1_QuantosTresEmLinha(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A1);
	pontuacao += this.a2_NumeroMovimentosPossiveis(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A2);
	pontuacao += this.a3_NumeroPecasComMobilidade(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A3);
	pontuacao += this.a4_NumeroPecas(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A4);
	pontuacao += this.a5_QuantosPossiveisTresEmLinha(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A5);
        pontuacao += this.a5_QuantosPossiveisTresEmLinha(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A5);
	pontuacao += this.a6_QuantasPecasEmTresEmLinha(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A6);
        pontuacao += this.a7_tabuleiroVencedor(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A7);
	pontuacao += this.a8_NumeroCasasVizinhasDisponiveis(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A8);
	pontuacao += this.a9_QuantosPossiveisTresEmLinha(jogador)*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A9);
        //pontuacao += this.a8_NumeroCasasVizinhasDisponiveis(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A8);
        //pontuacao += this.a9_QuantosPossiveisTresEmLinha(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A9);

        //Coeficientes Negativos
        pontuacao -= this.a1_QuantosTresEmLinha(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A1N);
	pontuacao -= this.a2_NumeroMovimentosPossiveis(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A2N);
	pontuacao -= this.a3_NumeroPecasComMobilidade(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A3N);
	pontuacao -= this.a4_NumeroPecas(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A4N);
	pontuacao -= this.a5_QuantosPossiveisTresEmLinha(this.getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A5N);
	pontuacao -= this.a6_QuantasPecasEmTresEmLinha(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A6N);
        pontuacao -= this.a7_tabuleiroVencedor(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A7N);
        pontuacao -= this.a8_NumeroCasasVizinhasDisponiveis(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A8N);
        pontuacao -= this.a9_QuantosPossiveisTresEmLinha(getAdversario(jogador))*jogo.getPeso().getCoeficiente(Peso.FUNCAO_AVALIACAO_A9N);

        return pontuacao;
    }
    
    /**
     * Contabilização do número de linhas de peças do jogador
     * 
     * @param jogador
     * @return 
     */
    private int a1_QuantosTresEmLinha(char jogador){
	int total = 0;
	
	// Todas as posições válidas no tabuleiro
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	
	// Para evitar múltiplas contabilizações da mesma linha
	HashMap<Integer, Boolean> posicoesContabilizadas = new HashMap<Integer, Boolean>();
	
	for (Posicao posicao : posicoes) {
	    for (ArrayList<Posicao> linhaDeVizinhos : posicao.getPosicoesVizinhas()) {
		// Chave do elemento para contabilização das posições
		int chave=0;
		int casasPreenchidas = 0;
		if(this.tabuleiro[posicao.getLinha()][posicao.getColuna()]==jogador){
		    casasPreenchidas = 1;
		    chave+=posicao.hashCode();
		}
		
		for (Posicao posicaoVizinha : linhaDeVizinhos) {
		    if(this.tabuleiro[posicaoVizinha.getLinha()][posicaoVizinha.getColuna()]==jogador){
			chave+=posicaoVizinha.hashCode();
			if(++casasPreenchidas>=3 && !posicoesContabilizadas.containsKey(chave)){
			    total++;
			    posicoesContabilizadas.put(chave, Boolean.TRUE);
			}
		    }else{
			break;
		    }
		}
	    }
	}
	return total;
    }
    
    /**
     * Contabilização do número de movimentos de peças possíveis com as do jogador
     * 
     * @param jogador
     * @return 
     */
    private int a2_NumeroMovimentosPossiveis(char jogador){
	int totalMovimentosDisponiveisJogador = 0;
	
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	for (Posicao posicao : posicoes) {
	    int linha = posicao.getLinha();
	    int coluna = posicao.getColuna();
	    int numeroMovimentos = this.pecaPodeSerMovidaParaQuantasPosicoes(linha, coluna, jogador);
	    if(numeroMovimentos>0){
		totalMovimentosDisponiveisJogador+=numeroMovimentos;
	    }
	}
	return totalMovimentosDisponiveisJogador;
    }
    
    /**
     * Contabilização do número de peças com mobilidade do jogador
     * 
     * @param jogador
     * @return 
     */
    private int a3_NumeroPecasComMobilidade(char jogador){
	int totalDePecasComMobilidadeParaJogador = 0;
	
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	for (Posicao posicao : posicoes) {
	    int linha = posicao.getLinha();
	    int coluna = posicao.getColuna();
	    
	    if(this.pecaPodeSerMovidaParaQuantasPosicoes(linha, coluna, jogador)>0){
		totalDePecasComMobilidadeParaJogador++;
	    }
	}
	return totalDePecasComMobilidadeParaJogador;
    }
    
    /**
     * Contabilização do número de peças do jogador
     * 
     * @param jogador
     * @return 
     */
    private int a4_NumeroPecas(char jogador){
	int totalDePecasDoJogador = 0;
	
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	for (Posicao posicao : posicoes) {
	    int linha = posicao.getLinha();
	    int coluna = posicao.getColuna();
	    
	    // Contabilizar o número de peças do jogador
	    if(this.isPecaDoJogador(linha, coluna)){
		totalDePecasDoJogador++;
	    }
	}
	return totalDePecasDoJogador;
    }
    
    /**
     * Contabilização do número de possibilidade de três em linha
     * 
     * @param jogador
     * @return 
     */
    private int a5_QuantosPossiveisTresEmLinha(char jogador){
	int total = 0;
	if(this.isColocarPecasMode(jogador)){
	    return total;
        }
	
	// Todas as posições válidas no tabuleiro
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	
	// Para evitar múltiplas contabilizações da mesma linha
	HashMap<Integer, Boolean> posicoesContabilizadas = new HashMap<Integer, Boolean>();
	
	for (Posicao posicao : posicoes) {
	    for (ArrayList<Posicao> linhaDeVizinhos : posicao.getPosicoesVizinhas()) {
		// Chave do elemento para contabilização das posições
		int chave=0;
		int casasPreenchidas = 0;
		Posicao casaVazia = null;
		
		// Contabilizar a posição actual
		if(this.tabuleiro[posicao.getLinha()][posicao.getColuna()]==jogador){
		    casasPreenchidas = 1;
		    chave+=posicao.hashCode();
		}else if(this.tabuleiro[posicao.getLinha()][posicao.getColuna()]==VAZIO){
		    casaVazia = posicao;
		    chave+=casaVazia.hashCode();
		}
		
		// Para cada uns dos vizinhos desta posição, verificar se contem uma peça do jogador ou vazio
		for (Posicao posicaoVizinha : linhaDeVizinhos) {
		    if(this.tabuleiro[posicaoVizinha.getLinha()][posicaoVizinha.getColuna()]==jogador){
			chave+=posicaoVizinha.hashCode();
			casasPreenchidas++;
		    }else if(casaVazia==null && this.tabuleiro[posicaoVizinha.getLinha()][posicaoVizinha.getColuna()]==VAZIO){
			casaVazia = posicaoVizinha;
			chave+=casaVazia.hashCode();
		    }else{
			break;
		    }
		}
		
		// Se temos duas casas preenchidas, uma terceira vazia e estas posições ainda não foram contabilizadas
		if(casasPreenchidas==2 && casaVazia!=null && !posicoesContabilizadas.containsKey(chave)){
		    ArrayList<Posicao> posicoesAcessiveis = (ArrayList<Posicao>) this.getPosicoesParaOndePodeMover(casaVazia.getLinha(), casaVazia.getColuna(), jogador);
                    for (Posicao posicaoAcessivel : posicoesAcessiveis) {
			if(this.tabuleiro[posicaoAcessivel.getLinha()][posicaoAcessivel.getColuna()]==jogador){
                            total++; // O jogador pode fazer 3 em linha
			}else if(this.tabuleiro[posicaoAcessivel.getLinha()][posicaoAcessivel.getColuna()]==this.getAdversario(jogador)){
			    total--; // O adversário pode bloquear a jogada
			}
		    }
		    // Registar contagem
		    if(!posicoesContabilizadas.containsKey(chave)){
			posicoesContabilizadas.put(chave, Boolean.TRUE);
		    }
		}
	    }
	}
	return total;
    }
    
    /**
     * Contabilização do número de peças que fazem parte do conjunto de três em linha
     * 
     * @param jogador
     * @return 
     */
    private int a6_QuantasPecasEmTresEmLinha(char jogador){
	int total = 0;
	
	// Todas as posições válidas no tabuleiro
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	
	for (Posicao posicao : posicoes) {
	    if(this.jogadorTemTresEmLinha(jogador, posicao.getLinha(), posicao.getColuna())){
		total++;
	    }
	}
	return total;
    }
    
    /**
     * Verificação da presença de um tabuleiro vencedor
     * 
     * @param jogador
     * @return 
     */
    private int a7_tabuleiroVencedor(char jogador){
	if(this.isVencedor(jogador)){
	    return 1;
	}
	return 0;
    }
    
    /**
     * Contabilização do número de movimentos de peças possíveis com as do jogador
     * 
     * @param jogador
     * @return 
     */
    private int a8_NumeroCasasVizinhasDisponiveis(char jogador){
	int totalCasaVizinhasDisponiveisJogador = 0;

        if(!this.isColocarPecasMode(jogador)){
	    return totalCasaVizinhasDisponiveisJogador;
	}
	
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	for (Posicao posicao : posicoes) {
	    int linha = posicao.getLinha();
	    int coluna = posicao.getColuna();
	    int posicoesLivres = 0;
	    if(this.isPosicaoValida(linha, coluna) && this.isPecaDe(jogador, linha, coluna)){
		for (ArrayList<Posicao> linhaDeVizinhos : this.getPosicao(linha, coluna).getPosicoesVizinhas()) {
		    for (Posicao posicaoVizinha : linhaDeVizinhos) {
			if(this.isPosicaoLivre(posicaoVizinha.getLinha(), posicaoVizinha.getColuna()) && posicaoVizinha.isVizinhoProximo(posicao)){
                            posicoesLivres++;
			}
		    }
		}
	    }
	    if(posicoesLivres>0){
		totalCasaVizinhasDisponiveisJogador+=posicoesLivres;
	    }
	}
        return totalCasaVizinhasDisponiveisJogador;
    }

    /**
     * Contabilização do número de possibilidade de três em linha
     *
     * @param jogador
     * @return
     */
    private int a9_QuantosPossiveisTresEmLinha(char jogador){
	int total = 0;

	if(!this.isColocarPecasMode(jogador)){
	    return total;
	}

	// Todas as posições válidas no tabuleiro
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());

	// Para evitar múltiplas contabilizações da mesma linha
	HashMap<Integer, Boolean> posicoesContabilizadas = new HashMap<Integer, Boolean>();

	for (Posicao posicao : posicoes) {
	    for (ArrayList<Posicao> linhaDeVizinhos : posicao.getPosicoesVizinhas()) {
		// Chave do elemento para contabilização das posições
		int chave=0;
		int casasPreenchidas = 0;
		Posicao casaVazia = null;

		// Contabilizar a posição actual
		if(this.tabuleiro[posicao.getLinha()][posicao.getColuna()]==jogador){
		    casasPreenchidas = 1;
		    chave+=posicao.hashCode();
		}else if(this.tabuleiro[posicao.getLinha()][posicao.getColuna()]==VAZIO){
		    casaVazia = posicao;
		    chave+=casaVazia.hashCode();
		}

		// Para cada uns dos vizinhos desta posição, verificar se contem uma peça do jogador ou vazio
		    for (Posicao posicaoVizinha : linhaDeVizinhos) {
		    if(this.tabuleiro[posicaoVizinha.getLinha()][posicaoVizinha.getColuna()]==jogador){
			chave+=posicaoVizinha.hashCode();
			casasPreenchidas++;
		    }else if(casaVazia==null && this.tabuleiro[posicaoVizinha.getLinha()][posicaoVizinha.getColuna()]==VAZIO){
			casaVazia = posicaoVizinha;
			chave+=casaVazia.hashCode();
		    }else{
			break;
                            }
			}
		// Se temos duas casas preenchidas, uma terceira vazia e estas posições ainda não foram contabilizadas
		if(casasPreenchidas==2 && casaVazia!=null && !posicoesContabilizadas.containsKey(chave)){
		    total++; // O jogador pode fazer 3 em linha
		    // Registar contagem
		    if(!posicoesContabilizadas.containsKey(chave)){
			posicoesContabilizadas.put(chave, Boolean.TRUE);
		    }
		}
	    }
	    }
	return total;
	}

    /**
     * Calcula o número total de peças com mobilidade para o jogador
     * 
     * @param jogador
     * @return 
     */
    private int numeroDePecasComMobilidade(char jogador){
	int total = 0;
	ArrayList<Posicao> posicoes = new ArrayList<Posicao>(this.getPosicoes());
	for (Posicao posicao : posicoes) {
	    if(this.pecaPodeSerMovida(posicao.getLinha(), posicao.getColuna(), jogador)){
		total++;
	    }
	}
	return total;
    }
    
    /**
     * Verifica se o jogo conclui por vitório ou empate dos jogadores
     * 
     * @return true alguém ganhou ou empatou, false caso contrário
     */ //_TODO
    @Override
    public boolean terminou() {
    	if (this.isVencedor(X) || this.isVencedor(O) || this.isEmpate(this.jogadorActual)) {
            return true;
        }
	
        return false;
    }
    
    /**
     * Imprime o tabuleiro para a consola
     * 
     * @return String com uma representação textual do tabuleiro de jogo
     */ //_TODO
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
	
	buffer.append("\n -");
	for (int j = 0; j < this.tabuleiro[0].length-1; j++) {
	    buffer.append("--");
	}
        for (int i = 0; i < this.tabuleiro.length; i++) {
            buffer.append("\n|");
	    
            for (int j = 0; j < this.tabuleiro[i].length; j++) {
		if(this.isPosicaoValida(i, j))
		    buffer.append(this.tabuleiro[i][j]);
		else
		    buffer.append(' ');
		
		if(j < this.tabuleiro[i].length-1)
		    buffer.append(' ');
            }
	    buffer.append("|");
        }
	buffer.append("\n -");
	for (int j = 0; j < this.tabuleiro[0].length-1; j++) {
	    buffer.append("--");
	}
        return buffer.toString();
    }

    @Override
    public Object clone() {
        return new EstadoJogoMoinho(tabuleiro, jogadorActual, this.numeroPecasMaximo.get(X), this.numeroPecasMaximo.get(O), this.ultimaPecaRemovida, this.podeComerPeca.get(X), this.podeComerPeca.get(O));
    }

    public int getDimensao() {
        return tabuleiro.length;
    }

    public char getValorPeca(int linha, int coluna){
        return tabuleiro[linha][coluna];
    }
    
    /**
     * Obter uma posição a partir das posições válidas
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @return Posição
     */
    public Posicao getPosicao(int linha, int coluna){
	if(this.isPosicaoValida(linha, coluna)){
	    return this.relacoesEntrePosicoes.get((new Posicao(linha, coluna)).toString());
	}
    	return null;
    }
    
    /**
     * Verifica se o jogo está no modo de colocação de peças
     * @param jogador
     * @return
     */
    public boolean isColocarPecasMode(char jogador){
	return (this.jogadorPodeColocarPecas(jogador) && this.ultimaPecaRemovida==null);
    }

    /**
     * Verifica se o jogo está no modo de voo de peças
     * @param jogador
     * @return
     */
    public boolean isVoarMode(char jogador){
	return this.pecasPodemVoar(jogador);
    }

    /**
     * Verifica se o jogo está no modo de movimento de peças
     * @param jogador
     * @return
     */
    public boolean isMoverMode(char jogador){
	return (!this.isColocarPecasMode(jogador) && !this.isVoarMode(jogador));
    }

    /**
     * Devolve a lista de posições válidas do tabuleiro de jogo
     * 
     * @return coleção com a lista de posições
     */
    public List<Posicao> getPosicoes(){
	return new ArrayList<Posicao>(this.relacoesEntrePosicoes.values());
    }

    //Listeners
    private transient ArrayList<JogoListener> listeners = new ArrayList<JogoListener>();

    public synchronized void removeJogoListener(JogoListener l) {
        if (listeners != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public synchronized void addJogoListener(JogoListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void fireJogoChanged(JogoEvent pe) {
        for (JogoListener listener : listeners) {
            listener.jogoChanged(pe);
        }
    }
    
    /**
     * Notifica os listener de uma alteração no estado
     */
    public void fireJogoChanged() {
        this.fireJogoChanged(new JogoEvent(this));
    }
}
