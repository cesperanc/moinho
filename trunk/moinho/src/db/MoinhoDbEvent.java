package db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.EventObject;

public class MoinhoDbEvent extends EventObject {
    private ArrayList<ArrayList<Object>> estatisticas=null;

    public MoinhoDbEvent(Connection source, ArrayList<ArrayList<Object>> estatisticas) {
        super(source);
	this.estatisticas = estatisticas;
    }

    /**
     * @return the estatisticas
     */
    public ArrayList<ArrayList<Object>> getEstatisticas() {
	return this.estatisticas;
    }
}
