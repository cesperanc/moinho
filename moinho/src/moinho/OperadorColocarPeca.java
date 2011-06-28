package moinho;

import jogos.*;

public class OperadorColocarPeca extends OperadorMoinho {

    public OperadorColocarPeca(int linha, int coluna) {
	super(linha, coluna);
    }

    /**
     * Executa o operador, colocando a peça
     * 
     * @param estado do jogo
     * @param background caso o operador deva ser aplicado em segundo plano
     * @return true se o operador foi aplicado, false caso contrário
     */
    @Override
    public boolean executar(Estado estado, boolean background) throws Exception {
	if(OperadorColocarPeca.podeSerAplicado(estado, this.linha, this.coluna)){
	    // Se não estamos a correr em segundo plano, então vamos jogar com calma...
	    if(!background){
		Thread.sleep(SLEEP_TIME_BETWEEN_MOVES);
	    }
	    EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
            if(e.colocarPeca(this.linha, this.coluna)){
		e.setOperador(this);
		this.executarOperadorSeguinte(e, background);

		return true;
	    }
        }else if(!estado.terminou() && !background){
	    throw new Exception("Erro ao colocar a peça na posição: "+this.linha+", "+this.coluna+ "-"+OperadorColocarPeca.podeSerAplicado(estado, this.linha, this.coluna));
	}
	return false;
    }
    
    /**
     * Executa o operador, colocando a peça
     * 
     * @param estado do jogo
     * @param linha 
     * @param coluna 
     * @return Operador com o operador aplicado
     */
    public static Operador executar(Estado estado, int linha, int coluna) {
	if(OperadorColocarPeca.podeSerAplicado(estado, linha, coluna)){
	    OperadorColocarPeca operador = new OperadorColocarPeca(linha, coluna);
	    EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
            e._colocarPeca(linha, coluna);
            e.setOperador(operador);
	    
	    return operador;
        }
	return null;
    }

    /**
     * Verifica se a jogada é válida
     * 
     * @param estado do jogo
     * @param linha com a linha da peça
     * @param coluna com a coluna da peça
     * @return true se o operador pode ser aplicado, falso caso contrário
     */
    public static boolean podeSerAplicado(Estado estado, int linha, int coluna) {
	EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
	return (!e.terminou() && !OperadorRemoverPeca.podeSerAplicado(estado, linha, coluna) && e.isJogadaValida(linha, coluna));
    }
}
