package moinho;

import jogos.*;

public abstract class OperadorMoinho implements Operador {
    private Operador operadorSeguinte = null;
    protected int linha = 0;
    protected int coluna = 0;
    
    public static int SLEEP_TIME_BETWEEN_MOVES = 500;

    public OperadorMoinho(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }
    
    /**
     * Define o operador que deve ser executado no passo seguinte
     * @param operador 
     */
    public void setOperadorSeguinte(Operador operador){
	this.operadorSeguinte = operador;
    }
    
    /**
     * Obtêm o operador seguinte a ser aplicado
     */
    public Operador getOperadorSeguinte(){
	return this.operadorSeguinte;
    }
    
    /**
     * Executa o operador seguinte, se este estiver definido
     * @param estado com o estado do jogo actual
     * @param background caso o operador deva ser aplicado em segundo plano
     */
    protected void executarOperadorSeguinte(Estado estado, boolean background) throws Exception{
	if(this.operadorSeguinte!=null){
	    this.operadorSeguinte.executar(estado, background);
	}
    }
    
    /**
     * Executa o operador em segundo plano
     * @param estado do jogo
     * @return true se o operador foi aplicado, false caso contrário
     */
    @Override
    public boolean executar(Estado estado) throws Exception {
	return this.executar(estado, true);
    }
    
    public int getLinha(){
	return this.linha;
    }
    
    public int getColuna(){
	return this.coluna;
    }
    
}
