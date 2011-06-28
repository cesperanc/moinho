package jogos;

import java.util.List;

public class Minimax extends AlgoritmoJogos {
    public static final String NOME = Minimax.class.getName();

    @Override
    public Operador decidir(Jogo jogo) {
        valorMax(jogo.getEstadoActual(), jogo, 0);
        return operadorSeguinte;
    }
    /**
     * 
     * @param estado
     * @param jogo
     * @param profundidade
     * @return 
     *///_TODO
    private double valorMax(Estado estado, Jogo jogo, int profundidade) {
        //1. Se atingiu o limite de profundidade devolve a avaliacao do jogo
	if(jogo.testeLimite(estado, profundidade)){
	    return jogo.funcaoAvaliacao(estado);
	}

	//2. Aplicar todos os operadores ao estado actual
	List<Estado> estados = jogo.aplicaOperadores(estado);

	// Existe um caso limite que deve ser levado em conta neste algoritmo:
	// Mesmo em caso de derrota inultrapassavel deve ser decidida uma jogada
	// Deve ser atribuido por omissao o primeiro operador possivel na profundidade 0(zero)
	// Assim:
	// Se operadorSeguinte vazio e profundidade actual 0, guardar operador actual
	if(profundidade==0 && estados.size()>0){
	    this.operadorSeguinte = estados.get(0).getOperador();
	}

	//3. Para cada estado resultante
	double valorMax = Double.NEGATIVE_INFINITY;
	for(Estado e:estados){
	    // Se este estado é igual a um dos estados anteriores, então analisemos o próximo estado
	    if(jogo.hasEstado(e)){
		continue;
	    }
	    
	    //4. Obter o valor da jogada MINIMIZANDO no nivel seguinte, profundidade + 1
	    double valorJogada = valorMin(e, jogo, profundidade+1);

	    //5. Se melhorar a solucao conhecida guardar
	    if(valorJogada>valorMax){
		valorMax = valorJogada;

		//5.1 Se for estado de profundidade 0 guardar o operador que gerou o estado em operadorSeguinte
		if(profundidade==0){
		    this.operadorSeguinte = e.getOperador();
		}
	    }
	}

	//6. Devolver o valor da melhor solucao
	return valorMax;
    }

    /**
     * 
     * @param estado
     * @param jogo
     * @param profundidade
     * @return 
     *///_TODO
    private double valorMin(Estado estado, Jogo jogo, int profundidade) {
	//1. Se atingiu o limite de profundidade devolve a avaliacao do jogo
	if(jogo.testeLimite(estado, profundidade)){
	    return jogo.funcaoAvaliacao(estado);
	}

	//2. Aplicar todos os operadores ao estado actual
	List<Estado> estados = jogo.aplicaOperadores(estado);

	//3. Para cada estado resultante
	double valorMin = Double.POSITIVE_INFINITY;
	for(Estado e:estados){
	    // Se este estado é igual a um dos estados anteriores, então analisemos o próximo estado
	    if(jogo.hasEstado(e)){
		continue;
	    }

	    //4. Obter o valor da jogada MAXIMIZANDO no nivel seguinte, profundidade + 1
	    double valorJogada = valorMax(e, jogo, profundidade+1);

	    //5. Se melhorar a solucao conhecida guardar
	    if(valorJogada<valorMin){
		valorMin = valorJogada;

	    }
	}

	//6. Devolver o valor da melhor solucao
	return valorMin;
    }
}
