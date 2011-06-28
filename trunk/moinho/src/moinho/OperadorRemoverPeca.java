package moinho;

import jogos.*;

public class OperadorRemoverPeca extends OperadorMoinho {

    public OperadorRemoverPeca(int linha, int coluna) {
	super(linha, coluna);
    }

    /**
     * Executa o operador, removendo a peça
     * 
     * @param estado do jogo
     * @param background caso o operador deva ser aplicado em segundo plano
     * @return true se o operador foi aplicado, false caso contrário
     */
    @Override
    public boolean executar(Estado estado, boolean background) throws Exception {
	
	if(OperadorRemoverPeca.podeSerAplicado(estado, linha, coluna)){
	    // Se não estamos a correr em segundo plano, então vamos jogar com calma...
	    if(!background){
		Thread.sleep(SLEEP_TIME_BETWEEN_MOVES);
	    }
	    EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
            if(e.removerPeca(linha, coluna)){
		e.setOperador(this);
		this.executarOperadorSeguinte(e, background);
	    
		return true;
	    }
        }else if(!estado.terminou() && !background){
	    throw new Exception("Erro ao remover a peça na posição: "+this.linha+", "+this.coluna+ "-"+OperadorRemoverPeca.podeSerAplicado(estado, this.linha, this.coluna));
	}
	return false;
    }
    
    /**
     * Executa o operador, removendo a peça
     * 
     * @param estado do jogo
     * @param linha 
     * @param coluna 
     * @return Operador com o operador aplicado
     */
    public static Operador executar(Estado estado, int linha, int coluna) {
	if(OperadorRemoverPeca.podeSerAplicado(estado, linha, coluna)){
	    OperadorMoinho operador = new OperadorRemoverPeca(linha, coluna);
	    EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
            e._removerPeca(linha, coluna);
            e.setOperador(operador);
	    
	    return operador;
        }
	return null;
    }

    /**
     * Verifica se as peças do jogador podem voar e se a peça pertence ao utilizador
     * 
     * @param estado do jogo
     * @param linha com a linha da peça
     * @param coluna com a coluna da peça
     * @return true se o operador pode ser aplicado, falso caso contrário
     */
    public static boolean podeSerAplicado(Estado estado, int linha, int coluna) {
	EstadoJogoMoinho e = (EstadoJogoMoinho) estado;
	return (!e.terminou() && !OperadorComerPeca.podeSerAplicado(estado, linha, coluna) && e.isPecaDoJogador(linha, coluna) && (e.pecasDoJogadorPodemVoar() || e.pecaPodeSerMovida(linha, coluna)));
    }
}
