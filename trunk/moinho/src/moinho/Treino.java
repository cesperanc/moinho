/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moinho;

import db.MoinhoDB;
import db.Peso;
import gui.FrameAplicacao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.swing.SwingWorker;
import jogos.AlfaBeta;
import jogos.Minimax;

/**
 *
 * @author cesperanc
 */
public class Treino implements JogoTreinadoListener {
    private FrameAplicacao frame = null;
    // Número mínimo de individuos na base de dados
    private final int tamanhoMinimoPopulacao = 30;
    // Valor máximo que um coeficiente de uma função de avaliação pode ter assumir
    public static final int valorMaximoCoeficiente = 100;
    // profundidade máxima de pesquisa
    private final int profundidadeMaximaPesquisa = 2;
    
    // gerador de números aleatórios
    private static Random aleatorio = null;
    
    private static int totalTreinosEmExecucao=0;
    private final static Object _syncObject = new Object();
    
    
    public Treino(FrameAplicacao frame){
	super();
	this.frame = frame;
	this._init();
    }
    
    private void _init(){
	this.addListener(this.frame);
	
	synchronized(_syncObject) {
	    this.gerarPopulacaoInicial();
	    
	    Peso jogador1 = this.getIndividuoAleatoriatoriamente();
	    Peso jogador2 = this.getIndividuoAleatoriatoriamente();
	
	    if(jogador1!=null && jogador2!=null){
		this.frame.updateStatus("\nA iniciar um novo jogo entre o jogador com o ID "+MoinhoDB.getPeso(jogador1) +" e "+MoinhoDB.getPeso(jogador2)+"...");
		totalTreinosEmExecucao++;
		final TreinadorMoinho treinador = new TreinadorMoinho(jogador1.getAlgoritmo(), jogador1.getCoeficientes(), jogador1.getProfundidade(), jogador2.getAlgoritmo(), jogador2.getCoeficientes(), jogador2.getProfundidade());

		treinador.addListener(this);
		SwingWorker worker = new SwingWorker<Void, Void>() {

		    @Override
		    public Void doInBackground() {
			try {
			    treinador.start();
			} catch (Exception e) {
			    e.printStackTrace();
			}
			return null;
		    }
		};
		worker.execute();
	    }
	}
	
    }
    
    /**
     * @return o individuo com menos jogos, mais vitórias e menos derrotas que não esteja já em jogo
     */
    public static Peso getMelhorIndividuo(){
	return getMelhorIndividuo(0);
    }
    
    /**
     * @return o individuo com menos jogos, mais vitórias e menos derrotas mais perto da posição pretendida
     */
    private static synchronized Peso getMelhorIndividuo(int posicao){
	if(MoinhoDB.getEstatisticasList().isEmpty()){
	    return null;
	}
	if(MoinhoDB.getEstatisticasList().size()<=0){
	    return null;
	}
	if(posicao>MoinhoDB.getEstatisticasList().size()-1){
	    posicao = MoinhoDB.getEstatisticasList().size()-1;
	}
	if(posicao<0){
	    posicao = 0;
	}
	return MoinhoDB.getPeso((Integer) (MoinhoDB.getEstatisticasList().get(posicao).get(Peso.PESO_ID)));
    }
    
    /**
     * @return um inviduo escolhido de forma aleatória da população
     */
    private synchronized Peso getIndividuoAleatoriatoriamente(){
	ArrayList<HashMap<String, Object>> estatisticas = MoinhoDB.getEstatisticasList();
	if(!estatisticas.isEmpty()){
	    return MoinhoDB.getPeso((Integer) (estatisticas.get(getAleatorio().nextInt(estatisticas.size()-1)).get(Peso.PESO_ID)));
	}
	return null;
    }
    
    /**
     * Com base em dois individuos, faz a troca de material genético entre os individuos, gerando dois novos individuos com possibilidade de mutações
     */
    private synchronized void recombinarEmutar(Peso pai, Peso mae){
	for(String coeficiente : Peso.getCoeficientesPorOmissao().keySet()){
	    if(getAleatorio().nextDouble()<0.5){ // a probabilidade haver troca de genes é de 50%
		int gene = pai.getCoeficiente(coeficiente);
		pai.setCoeficiente(coeficiente, this.mutacao(mae.getCoeficiente(coeficiente)));
		mae.setCoeficiente(coeficiente, this.mutacao(gene));
	    }
	}
	
	ArrayList<Peso> pesos = new ArrayList<Peso>();
	pesos.add(pai);
	pesos.add(mae);
	
	MoinhoDB.insertPesos(pesos);
    }
    
    /**
     * Aplica uma mutação em 1 em cada 1000 individuos, com uma alteração de -10% a 10% do valor do gene
     * @param gene a mutar
     * @return gene mutado
     */
    private int mutacao(int gene){
	return (int) Math.round(gene+(((getAleatorio().nextDouble()<0.001)?1:0)*(getAleatorio().nextBoolean()?1:-1)*gene*(getAleatorio().nextDouble()/10)));
    }
    
    /**
     * Gera uma população inicial de individuos na base de dados
     * @return true se foram gerados individuos, false caso contrário
     */
    public synchronized boolean gerarPopulacaoInicial(){
	boolean result = false;
	int numeroDeInviduosNaBd = MoinhoDB.getEstatisticas().size();
	if(numeroDeInviduosNaBd>=tamanhoMinimoPopulacao){
	    return true;
	}
	HashMap<String, Integer> coeficientes = Peso.getCoeficientesPorOmissao();
	ArrayList<Peso> pesos = new ArrayList<Peso> ();
	// Enquanto não tivermos o número mínimo de elementos para a população
	while(numeroDeInviduosNaBd++<tamanhoMinimoPopulacao){
	    // definir valores aleatórios para cada um dos coeficientes dos individuos da população (-valorMaximoCoeficiente<=coeficiente<=valorMaximoCoeficiente)
	    for(String coeficiente : coeficientes.keySet()){
		coeficientes.put(coeficiente, getAleatorio().nextInt(valorMaximoCoeficiente)/**(getRandom().nextBoolean()?1:-1)*/);
	    }
	    // Introduzir os individuos na base de dados, para ambos os algoritmos
	    pesos.add(new Peso(profundidadeMaximaPesquisa, AlfaBeta.NOME, coeficientes));
	    pesos.add(new Peso(profundidadeMaximaPesquisa, Minimax.NOME, coeficientes));
	    // Foram gerados individuos
	    result = true;
	}
	if(result){
	    MoinhoDB.insertPesos(pesos);
	}
	return result;
    }

    /**
     * Se o jogo terminou, vamos registar os resultados
     * @param e 
     */
    @Override
    public void jogoFinished(JogoTreinadoEvent e) {
	AgenteMoinho jogador=null;
	if(MoinhoDB.checkDb()){
	    
	    int jogador1Id = MoinhoDB.insertPeso(e.getJogadorBrancas().getJogo().getPeso());
	    int jogador2Id = MoinhoDB.insertPeso(e.getJogadorPretas().getJogo().getPeso());
	    
	    jogador = e.getVencedor();
	    int vencedorId = (jogador!=null)?MoinhoDB.insertPeso(jogador.getJogo().getPeso()):0;
	    
	    jogador = e.getVencido();
	    int vencidoId = (jogador!=null)?MoinhoDB.insertPeso(jogador.getJogo().getPeso()):0;
	    
	    if(MoinhoDB.insertJogo(jogador1Id, jogador2Id, vencedorId, vencidoId, e.getTotalJogadas(), e.getDuracao())<0){
		System.err.println("Estatística não inserida");
	    }
	    
	    StringBuilder sbResult = new StringBuilder("\n");
	    if(e.getVencedor()!=null){
		
		// Em 30% das vitórias, queremos que o vencedor possa recombinar com o melhor individuo
		if(getAleatorio().nextDouble()<0.3){
		    this.recombinarEmutar(e.getVencedor().getJogo().getPeso(), getMelhorIndividuo());
		}
		
		sbResult.append("O jogador com o ID ").append(vencedorId);
		sbResult.append(" venceu o jogador com o ID ").append(vencidoId);
	    }else{
		sbResult.append("O jogo foi um empate");
		sbResult.append(" entre o jogador com o ID ").append(jogador1Id);
		sbResult.append(" e o jogador com o ID ").append(jogador2Id);
		
		// Em 1% dos empates, escolhemos um dos jogadores ao acaso para recombinar com o melhor individuo do jogo
		if(getAleatorio().nextDouble()<0.01){
		    this.recombinarEmutar((getAleatorio().nextBoolean()?e.getJogadorBrancas():e.getJogadorPretas()).getJogo().getPeso(), getMelhorIndividuo());
		}
	    }
	    this.frame.updateStatus(sbResult.toString());
	    
	}else{
	    System.err.println("A base de dados está com problemas");
	}
	
	
	
	synchronized(_syncObject) {
	    totalTreinosEmExecucao--;
	}
	this.fireTreinoFinished();
    }
    
    /**
     * Prepara um String com a informação de um jogo em execução
     * @param jogo
     * @return 
     */
    private String getPesosString(JogoDoMoinho jogo){
	HashMap<String, Integer> pesos = Peso.getCoeficientesPorOmissao();
	StringBuilder sbResult = new StringBuilder();
	for(String peso : pesos.keySet()){
	    sbResult.append('{').append(peso).append("=").append(jogo.getPeso().getCoeficiente(peso)).append("}, ");
	}
	return sbResult.toString();
    }
    
    /**
     * Obter o gerador de números aleatórios
     * @return 
     */
    public static Random getAleatorio(){
	if(aleatorio==null)
	    aleatorio = new Random(123);
	return aleatorio;
    }
    
    

    //Listeners
    private transient ArrayList<TreinoListener> listeners = new ArrayList<TreinoListener>();

    public synchronized void removeListener(TreinoListener l) {
        if (listeners != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public synchronized void addListener(TreinoListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void fireTreinoFinished() {
        for (TreinoListener listener : listeners) {
            listener.treinoFinished();
        }
    }
}
