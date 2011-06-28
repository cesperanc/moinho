package moinho;

import java.util.*;

public class JogoEvent extends EventObject {
    private EstadoJogoMoinho src;

    public JogoEvent(EstadoJogoMoinho source) {
        super(source);
	this.src = source;
    }
    
    public EstadoJogoMoinho getEstado(){
	return this.src;
    }
}
