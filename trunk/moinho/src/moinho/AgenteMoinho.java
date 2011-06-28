package moinho;

import jogos.*;
public class AgenteMoinho extends Agente {
    public AgenteMoinho(String nome) {
	super(nome);
    }

    @Override
    public void jogar() throws Exception {
	this.jogar(false);
    }
    
    /**
     * Tenta executar uma jogada, com a possibilidade de especificar se o jogo deve ocorrer em segundo plano ou não
     * @param background
     * @throws Exception 
     */
    public void jogar(boolean background) throws Exception {
	// Não vamos continuar a tentar jogar se o jogo já terminou (estamos a utilizar threads, o que podem tentar continuar a jogar por ainda não ter chegado a informação de que o jogo terminou)
	if(jogo.getEstadoActual().terminou()){
	    return;
	}
	// Guardamos o estado antes da jogada e, caso não seja possível por repetição de estado, definir o jogo como empatado
	if(!jogo.addEstado()){
	    EstadoJogoMoinho estadoMoinho = (EstadoJogoMoinho) jogo.getEstadoActual();
	    estadoMoinho.setEmpate(true);
	    estadoMoinho.fireJogoChanged();
	    return;
	}
	Operador operador = algoritmo.decidir(jogo);
	operador.executar(jogo.getEstadoActual(), background);
    }

    /**
     * Define o algoritmo para o agente (e para o jogo)
     */
    @Override
    public void usarMinimax() {
	super.usarMinimax();
        this.setAlgoritmo();
    }

     /**
     * Define o algoritmo para o agente (e para o jogo)
     */
    @Override
    public void usarAlfabeta() {
        super.usarAlfabeta();
        this.setAlgoritmo();
    }

    /**
     * Define o algoritmo do peso associado ao jogo
     */
    public void setAlgoritmo(String algoritmo) {
        if(this.getJogo()!=null){
	    this.getJogo().getPeso().setAlgoritmo(algoritmo);
	}
    }

    /**
     * Define o algoritmo do peso associado ao jogo
     */
    public void setAlgoritmo() {
        this.setAlgoritmo(this.nomeAlgoritmo);
    }

    /**
     * Getter para o jogo
     */
    public JogoDoMoinho getJogo() {
        return (JogoDoMoinho) this.jogo;
    }
}
