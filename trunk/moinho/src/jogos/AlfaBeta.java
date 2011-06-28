package jogos;

import java.util.List;

public class AlfaBeta extends AlgoritmoJogos {    
    public static final String NOME = AlfaBeta.class.getName();

    @Override
    public Operador decidir(Jogo jogo) {
        valorMax(jogo.getEstadoActual(), jogo, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        return operadorSeguinte;
    }

    private double valorMax(Estado estado, Jogo jogo, double alfa, double beta, int profundidade) {
	if (jogo.testeLimite(estado, profundidade)) {
            return jogo.funcaoAvaliacao(estado);
        }

        List<Estado> sucessores = jogo.aplicaOperadores(estado);
        for (Estado e : sucessores) {
	    // Se este estado é igual a um dos estados anteriores, então analisemos o próximo estado
	    if(jogo.hasEstado(e)){
		continue;
	    }
	    
            double alfaAnterior = alfa;
            alfa = Math.max(alfa, valorMin(e, jogo, alfa, beta, profundidade + 1));

            if (operadorSeguinte == null && profundidade == 0) {
                operadorSeguinte = e.getOperador();
            }

            if (alfa != alfaAnterior && profundidade == 0) {
                operadorSeguinte = e.getOperador();
            }

            if (alfa >= beta) {
                return beta;
            }
        }
        return alfa;
    }

    private double valorMin(Estado estado, Jogo jogo, double alfa, double beta, int profundidade) {
        if (jogo.testeLimite(estado, profundidade)) {
            return jogo.funcaoAvaliacao(estado);
        }

        List<Estado> sucessores = jogo.aplicaOperadores(estado);
        for (Estado e : sucessores) {
	    // Se este estado é igual a um dos estados anteriores, então analisemos o próximo estado
	    if(jogo.hasEstado(e)){
		continue;
	    }
	    
            double betaAnterior = beta;
            beta = Math.min(beta, valorMax(e, jogo, alfa, beta, profundidade + 1));

            if (operadorSeguinte == null && profundidade == 0) {
                operadorSeguinte = e.getOperador();
            }

            if (beta != betaAnterior && profundidade == 0) {
                operadorSeguinte = e.getOperador();
            }

            if (beta <= alfa) {
                return alfa;
            }
        }
        return beta;
    }
}
