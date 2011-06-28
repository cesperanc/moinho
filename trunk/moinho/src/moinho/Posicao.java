package moinho;

import java.util.ArrayList;

/**
 * Gere as informações de uma posição de um tabuleiro de jogo
 * 
 * @author Cláudio Esperança
 */
public final class Posicao {
    private int _coluna = 0;
    private int _linha = 0;
    private char _peca = EstadoJogoMoinho.VAZIO;
    private ArrayList<ArrayList<Posicao>> _posicoesVizinhas = new ArrayList<ArrayList<Posicao>>();
    
    /**
     * Constructor da posição
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     */
    public Posicao(int linha, int coluna){
        this.setLinha(linha);
        this.setColuna(coluna);
    }
    
    /**
     * Contructor da posição com especificação da peça
     * 
     * @param linha com a linha da posição
     * @param coluna com a coluna da posição
     * @param peca colocada na posição
     */
    public Posicao(int linha, int coluna, char peca){
	this(linha, coluna);
    }
    
    /**
     * @return linha da posição
     */
    public int getLinha(){
        return this._linha;
    }

    /**
     * Define a linha da posição
     * 
     * @param linha 
     */
    public void setLinha(int linha){
        this._linha = linha;
    }
    
    /**
     * @return coluna da posição
     */
    public int getColuna(){
        return this._coluna;
    }
    
    /**
     * Define a coluna da posição
     * 
     * @param coluna 
     */
    public void setColuna(int coluna){
        this._coluna = coluna;
    }

    /**
     * @return peça da posição
     */
    public char getPeca(){
        return this._peca;
    }
    
    /**
     * Define a peça da posição
     * 
     * @param peca 
     */
    public void setPeca(char peca){
        this._peca = peca;
    }
    
    /**
     * Metodo para comparação de posição
     * 
     * @param o com o objecto do tipo Posicao a comparar com a instância actual
     * @return true se os objectos são iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Posicao){
            Posicao obj = (Posicao) o;
	    return (obj.getLinha()==this.getLinha() && obj.getColuna()==this.getColuna());
        }
        return false;
    }

    /**
     * Gera o código hash da posição
     * 
     * @return o código hash
     */
    @Override
    public int hashCode() {
	return toString().hashCode();
    }
    
    @Override
    public String toString(){
	return this._linha+","+this._coluna;
    }

    /**
     * Devolve as posições vizinhas
     * 
     * @return lista com as posições vizinhas
     */
    public ArrayList<ArrayList<Posicao>> getPosicoesVizinhas() {
	return _posicoesVizinhas;
    }

    /**
     * Adiciona uma posição à vizinhança da posição actual
     * 
     * @param posicao com a posição a adicionar
     */
    public void addPosicaoVizinha(Posicao posicao) {
	// Se a posição vizinha é igual à posição actual, não adicionar como vizinha
	if(this.equals(posicao)){
	    return;
	}
	int linha = -1;
	
	// Se a linha ou a coluna da posição existe em alguma linha de registo de vizinhos, então adicionar a posição a essa linha
	for (int a = 0; a<this.getPosicoesVizinhas().size(); a++){
	    ArrayList<Posicao> linhaDeVizinhos = this.getPosicoesVizinhas().get(a);
	    for (Posicao posicaoVizinha : linhaDeVizinhos) {
		// Se a posicao a adicionar é igual à posição vizinha, então é porque esta já existe como vizinha
		if(posicaoVizinha.equals(posicao)){
		    return; 
		}
		// Se posição pertence a esta linha de registo de vizinhos, adicionar a esta linha
		if(posicaoVizinha.getLinha()==posicao.getLinha() || posicaoVizinha.getColuna()==posicao.getColuna()){
		    linha = a;
		    // Não paramos o ciclo aqui, pois o vizinho pode existir nos próximos vizinhos em pesquisa pelo ciclo
		}
	    }
	}
	
	// Se ainda não existe um linha de registo para esta linha ou coluna
	if(linha<0){
	    // Criar uma nova linha de registo de vizinhos
	    ArrayList<Posicao> novaLinhaDeVizinhos = new ArrayList<Posicao>();
	    novaLinhaDeVizinhos.add(posicao);
	    // Adicionar esta linha de vizinhos aos vizinhos
	    this._posicoesVizinhas.add(novaLinhaDeVizinhos);
	}else{
	    // Adicionar a posição à linha respectiva existente
	    this._posicoesVizinhas.get(linha).add(posicao);
	}
	// Adicionar a posição actual na lista de vizinhos do vizinho
	posicao.addPosicaoVizinha(this);
    }
    
    /**
     * De acordo com os vizinhos conhecidos para a posição, verifica se uma dada posição é vizinha da posição actual
     * 
     * @param posicao a reconhecer
     * @return true se a posição é um vizinho, false caso contrário
     */
    public boolean isVizinho(Posicao posicao){
	// Se a posição vizinha é igual à posição actual, então não é vizinho
	if(this.equals(posicao)){
	    return false;
	}
	
	for (ArrayList<Posicao> vizinhos : this.getPosicoesVizinhas()) {
	    for (Posicao vizinho : vizinhos) {
		if(vizinho.equals(posicao)){
		    return true;
		}
	    }
	}
	return false;
    }
    
    /**
     * Verifica se a posição especificada é um visinho próximo da posição actual
     * @param posicao
     * @return 
     */
    public boolean isVizinhoProximo(Posicao posicao){
	if(this.isVizinho(posicao)){
	    
	    if(this.getLinha()==posicao.getLinha() && this.getColuna()!=posicao.getColuna()){
		Posicao vizinhoProximo = posicao;
		for (ArrayList<Posicao> vizinhos : this.getPosicoesVizinhas()) {
		    for (Posicao vizinho : vizinhos) {
			if(this.getLinha()!=vizinho.getLinha()){
			    break;
			}
			if(!vizinho.equals(posicao) && Math.abs(this.getColuna()-vizinhoProximo.getColuna())>Math.abs(this.getColuna()-vizinho.getColuna())){
			    vizinhoProximo = vizinho;
			}
		    }
		}
		return vizinhoProximo.equals(posicao);
	    } else if(this.getLinha()!=posicao.getLinha() && this.getColuna()==posicao.getColuna()){
		Posicao vizinhoProximo = posicao;
		for (ArrayList<Posicao> vizinhos : this.getPosicoesVizinhas()) {
		    for (Posicao vizinho : vizinhos) {
			if(this.getColuna()!=vizinho.getColuna()){
			    break;
			}
			if(!vizinho.equals(posicao) && Math.abs(this.getLinha()-vizinhoProximo.getLinha())>Math.abs(this.getLinha()-vizinho.getLinha())){
			    vizinhoProximo = vizinho;
			}
		    }
		}
		return vizinhoProximo.equals(posicao);
	    }
	}
	return false;
    }
}
