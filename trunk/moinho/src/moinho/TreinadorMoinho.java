package moinho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;
import jogos.Minimax;

public class TreinadorMoinho implements JogoListener {

    private AgenteMoinho agenteBrancas = new AgenteMoinho("XPTO cruzes");
    private AgenteMoinho agentePretas = new AgenteMoinho("XPTO bolas");
    JogoDoMoinho jogoBrancas;
    JogoDoMoinho jogoPretas;
    private EstadoJogoMoinho estadoJogoMoinho = new EstadoJogoMoinho();
    private boolean agenteBrancoAJogar=true;
    private boolean agentePretasAJogar=false;
    private boolean agenteAJogar=false;
    private long inicioExecucao = 0;
    private long jogadasBranco = 0;
    private long jogadasPreto = 0;
    
    private boolean restart;
    private Semaphore semaphore = new Semaphore(1);

    public TreinadorMoinho(String algoritmoBrancas, HashMap<String, Integer> pesosAvalicaoBrancas, int profundidadeMaximaBrancas, String algoritmoPretas, HashMap<String, Integer> pesosAvalicaoPretas, int profundidadeMaximaPretas) {
        try {
	    this.jogoBrancas = new JogoDoMoinho(this.estadoJogoMoinho, EstadoJogoMoinho.X, profundidadeMaximaBrancas, pesosAvalicaoBrancas, algoritmoBrancas);
	    this.agenteBrancas.setJogo(this.jogoBrancas);
	    if (algoritmoBrancas.equals(Minimax.NOME)) {
		this.agenteBrancas.usarMinimax();
	    } else {
		this.agenteBrancas.usarAlfabeta();
	    }
	    
	    this.jogoPretas = new JogoDoMoinho(this.estadoJogoMoinho, EstadoJogoMoinho.X, profundidadeMaximaPretas, pesosAvalicaoPretas, algoritmoPretas);
	    this.agentePretas.setJogo(this.jogoPretas);
	    if (algoritmoPretas.equals(Minimax.NOME)) {
		this.agentePretas.usarMinimax();
	    } else {
		this.agentePretas.usarAlfabeta();
	    }
	    
            this._init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _init(){
        this.estadoJogoMoinho.addJogoListener(this);
	
	this.restart = true;
        while (agenteAJogar){} //espera que termine uma jogada
        this.estadoJogoMoinho.reiniciar();
        this.restart = false;
	// Inicio do jogo
	this.inicioExecucao = System.currentTimeMillis();
	
	jogadasDoAgenteBranco();
	jogadasDoAgentePretas();
        
    }
    
    public void start(){
	jogadasDoAgenteBranco();
	jogadasDoAgentePretas();
    }

    private void jogadasDoAgenteBranco() {
	SwingWorker worker = new SwingWorker<Void, Void>() {

	    @Override
            public Void doInBackground() {
                try {
                    while (!estadoJogoMoinho.terminou() && !restart) {
                        semaphore.acquire();
                        if (agenteBrancoAJogar) {
                            agenteAJogar = true;
                            agenteBrancas.jogar(true);
                            agenteAJogar = false;
                            agenteBrancoAJogar = false;
                            agentePretasAJogar = true;
			    jogadasBranco++;
                        }
                        semaphore.release();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
		    System.err.println("Ocorreu um erro na jogada das brancas "+e.getMessage());
                }
                return null;
            }
        };

        worker.execute();
    }

    private void jogadasDoAgentePretas() {
        SwingWorker worker = new SwingWorker<Void, Void>() {

	    @Override
            public Void doInBackground() {
                try {
                    while (!estadoJogoMoinho.terminou() && !restart) {
                        semaphore.acquire();
                        if (agentePretasAJogar) {
                            agenteAJogar = true;
                            agentePretas.jogar(true);
                            agenteAJogar = false;
                            agentePretasAJogar = false;
                            agenteBrancoAJogar = true;
			    jogadasPreto++;
                        }
                        semaphore.release();
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
		    System.err.println("Ocorreu um erro na jogada das brancas "+e.getMessage());
                }
                return null;
            }
        };

        worker.execute();
    }

    @Override
    public void jogoChanged(JogoEvent pe) {
        if (estadoJogoMoinho.terminou()) {
	    long fimExecucao = System.currentTimeMillis();
            if (estadoJogoMoinho.isVencedor(EstadoJogoMoinho.X)) {
		this.fireJogoFinished(new JogoTreinadoEvent(this.estadoJogoMoinho, this.agenteBrancas, this.agentePretas, this.agenteBrancas, this.jogadasBranco+this.jogadasPreto, fimExecucao-this.inicioExecucao));
            } else if (estadoJogoMoinho.isVencedor(EstadoJogoMoinho.O)) {
		this.fireJogoFinished(new JogoTreinadoEvent(this.estadoJogoMoinho, this.agenteBrancas, this.agentePretas, this.agentePretas, this.jogadasBranco+this.jogadasPreto, fimExecucao-this.inicioExecucao));
            } else {
                this.fireJogoFinished(new JogoTreinadoEvent(this.estadoJogoMoinho, this.agenteBrancas, this.agentePretas, null, this.jogadasBranco+this.jogadasPreto, System.currentTimeMillis()-this.inicioExecucao));
            }
        }
    }

    //Listeners
    private transient ArrayList<JogoTreinadoListener> listeners = new ArrayList<JogoTreinadoListener>();

    public synchronized void removeListener(JogoTreinadoListener l) {
        if (listeners != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public synchronized void addListener(JogoTreinadoListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void fireJogoFinished(JogoTreinadoEvent pe) {
        for (JogoTreinadoListener listener : listeners) {
            listener.jogoFinished(pe);
        }
    }
}
