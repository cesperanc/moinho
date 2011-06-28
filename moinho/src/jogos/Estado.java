package jogos;

public abstract class Estado {

    protected Operador operador;
    
    // Flag para gestão de empates
    protected boolean isEmpate = false;

    public abstract boolean terminou();

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }
    
    /**
     * Define se este é um estado onde o jogo está empatado
     * 
     * @param isEmpate para definir ou não o estado de empate
     */
    public void setEmpate(boolean isEmpate) {
	this.isEmpate = isEmpate;
    }
}
