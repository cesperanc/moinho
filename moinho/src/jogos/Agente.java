package jogos;

public class Agente {

    protected String nome;
    protected Minimax minimax;
    protected AlfaBeta alfabeta;
    protected AlgoritmoJogos algoritmo;
    protected String nomeAlgoritmo;
    protected Jogo jogo;

    public Agente(String nome) {
        this.nome = nome;
        minimax = new Minimax();
        alfabeta = new AlfaBeta();
        algoritmo = alfabeta;
	nomeAlgoritmo = AlfaBeta.NOME;
    }

    public void jogar() throws Exception {
	Operador operador = algoritmo.decidir(jogo);
	operador.executar(jogo.getEstadoActual(), false);
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public void usarMinimax() {
        algoritmo = minimax;
	nomeAlgoritmo = Minimax.NOME;
    }

    public void usarAlfabeta() {
        algoritmo = alfabeta;
	nomeAlgoritmo = AlfaBeta.NOME;
    }

    public String getNome(){
        return nome;
    }
    
    /**
     * Devolve o nome do algoritmo que este agente está a utilizar
     * @return 
     */
    public String getAlgoritmo(){
	return this.nomeAlgoritmo;
    }
}
