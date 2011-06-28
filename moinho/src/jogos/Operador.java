package jogos;

public interface Operador {

    public boolean executar(Estado estado) throws Exception;
    
    public boolean executar(Estado estado, boolean background) throws Exception;
}
