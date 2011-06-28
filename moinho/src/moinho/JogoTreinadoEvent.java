package moinho;

public class JogoTreinadoEvent extends JogoEvent {
    private AgenteMoinho vencedor;
    private AgenteMoinho jogadorBrancas;
    private AgenteMoinho jogadorPretas;
    private long totalJogadas = 0;
    private long duracao = 0;

    public JogoTreinadoEvent(EstadoJogoMoinho source, AgenteMoinho jogadorBrancas, AgenteMoinho jogadorPretas, AgenteMoinho vencedor, long totalJogadas, long duracao) {
        super(source);
	this.vencedor = vencedor;
	this.jogadorBrancas = jogadorBrancas;
	this.jogadorPretas = jogadorPretas;
	this.totalJogadas = totalJogadas;
	this.duracao = duracao;
    }

    /**
     * @return the vencedor
     */
    public AgenteMoinho getVencedor() {
	return this.vencedor;
    }

    /**
     * @return the vencido
     */
    public AgenteMoinho getVencido() {
	if(this.getVencedor()!=null && this.jogadorBrancas!=null){
	    return (this.getVencedor().getNome().equals(this.jogadorBrancas.getNome())?this.jogadorBrancas:this.jogadorPretas);
	}
	return null;
    }

    /**
     * @return the jogadorBrancas
     */
    public AgenteMoinho getJogadorBrancas() {
	return this.jogadorBrancas;
    }

    /**
     * @return the jogadorPretas
     */
    public AgenteMoinho getJogadorPretas() {
	return this.jogadorPretas;
    }

    /**
     * @return the totalJogadas
     */
    public long getTotalJogadas() {
	return this.totalJogadas;
    }

    /**
     * @return the duracao
     */
    public long getDuracao() {
	return this.duracao;
    }
    
}
