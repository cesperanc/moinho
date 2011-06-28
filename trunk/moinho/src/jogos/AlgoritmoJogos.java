package jogos;

public abstract class AlgoritmoJogos {

    protected Operador operadorSeguinte = null;

    public abstract Operador decidir(Jogo jogo);
}
