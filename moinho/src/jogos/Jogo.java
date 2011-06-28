package jogos;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public abstract class Jogo {

    protected Estado estadoActual;
    protected LinkedList<Operador> listaOperadores;
    protected int profundidadeMaxima;
    
    // Para fazer cache dos estados já jogados, para evitar ciclos
    protected HashMap<String, Integer> cacheEstados=null;

    protected Jogo(Estado estadoInicial, LinkedList<Operador> listaOperadores, int profundidadeMaxima) {
        this.estadoActual = estadoInicial;
        this.listaOperadores = listaOperadores;
        this.profundidadeMaxima = profundidadeMaxima;
	
	this.cacheEstados = new HashMap<String, Integer>();
    }

    public boolean terminou(Estado estado) {
        return estado.terminou();
    }

    public boolean testeLimite(Estado estado, int profundidade) {
        return (terminou(estado) || profundidade == profundidadeMaxima);
    }

    public abstract List<Estado> aplicaOperadores(Estado estado);

    public abstract double utilidade(Estado estado);

    public abstract double funcaoAvaliacao(Estado estado);

    public Estado getEstadoActual() {
        return estadoActual;
    }
    
    /**
     * Adiciona um estado à lista de estados recentes
     * @param estado
     * @return 
     */
    public boolean addEstado(Estado estado){
	if(!this.hasEstado(estado)){
	    this.cleanEstadosAntigos(estado);
	    this.cacheEstados.put(estado.toString(), estado.hashCode());
	    return true;
	}
	return false;
    }
    
    /**
     * Adiciona o estado actual à lista de estados recentes
     * @return 
     */
    public boolean addEstado(){
	return this.addEstado(this.getEstadoActual());
    }
    
    public void cleanEstadosAntigos(Estado estado){}
    
    /**
     * Verifica se o jogo já passou por este estado
     * @param estado
     * @return 
     */
    public boolean hasEstado(Estado estado){
	return this.cacheEstados.containsKey(estado.toString());
    }
    
    /**
     * @return profundidadeMaxima de pesquisa dos algoritmos para este jogo
     */
    public int getProfundidadeMaxima(){
	return this.profundidadeMaxima;
    }
}
