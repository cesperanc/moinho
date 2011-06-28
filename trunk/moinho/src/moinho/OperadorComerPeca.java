package moinho;

import jogos.*;

public class OperadorComerPeca extends OperadorMoinho {

    public OperadorComerPeca(int linha, int coluna) {
	super(linha, coluna);
    }

    /**
     * Executa o operador, comendo a peça do adversário
     * 
     * @param estado do jogo
     * @param background caso o operador deva ser aplicado em segundo plano
     * @return true se o operador foi aplicado, false caso contrário
     */
    @Override
    public boolean executar(Estado estado, boolean background) throws Exception {
	if(OperadorComerPeca.podeSerAplicado(estado, this.linha, this.coluna)){
	    // Se não estamos a correr em segundo plano, então vamos jogar com calma...
	    if(!background){
		Thread.sleep(SLEEP_TIME_BETWEEN_MOVES);
	    }
	    EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
            if(e.comerPeca(this.linha, this.coluna)){
		e.setOperador(this);
		this.executarOperadorSeguinte(e, background);
	    }
	    
	    return true;
        }else if(!estado.terminou() && !background){
	    throw new Exception("Erro ao comer a peça na posição: "+this.linha+", "+this.coluna+ "-"+OperadorComerPeca.podeSerAplicado(estado, this.linha, this.coluna)+"\n"+estado.toString());
	}
	return false;
    }
    
    /**
     * Executa o operador, comendo a peça
     * 
     * @param estado do jogo
     * @param linha 
     * @param coluna 
     * @return Operador com o operador aplicado
     */
    public static Operador executar(Estado estado, int linha, int coluna) {
	if(OperadorComerPeca.podeSerAplicado(estado, linha, coluna)){
	    EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
            if(e._comerPeca(linha, coluna)){
		e.setOperador(new OperadorComerPeca(linha, coluna));
		return estado.getOperador();
	    }
        }
	return null;
    }

    /**
     * Verifica se o jogo não terminou e se o jogador pode comer a peça
     * 
     * @param estado do jogo
     * @return true se o operador pode ser aplicado, falso caso contrário
     */
    public static boolean podeSerAplicado(Estado estado, int linha, int coluna) {
	EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
	return (!e.terminou() && e.jogadorPodeComerPeca(linha, coluna));
    }
}
