package gui;

import db.MoinhoDB;
import db.MoinhoDbEvent;
import db.MoinhoDbListener;
import db.Peso;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * @author Cláudio Esperança
 */
public class MoinhoDbTableModel extends AbstractTableModel implements MoinhoDbListener {
    
    private ArrayList<ArrayList<Object>> estatisticas=null;
    private ArrayList<String> nomesColunas=null;
    
    public MoinhoDbTableModel(){
	super();
	this._init();
    }
    
    private void _init(){
	MoinhoDB.addListener(this);
	this.estatisticas = MoinhoDB.getEstatisticas();
	this.nomesColunas = MoinhoDB.getEstatisticasColumnNames();
    }

    @Override
    public int getColumnCount() {
	if(this.nomesColunas!=null){
	    return this.nomesColunas.size();
	}
	return 0;
    }

    @Override
    public int getRowCount() {
        if(this.estatisticas!=null){
	    return this.estatisticas.size();
	}
	return 0;
    }
    
    private String _getColumnName(int col) {
        return this.nomesColunas.get(col);
    }

    @Override
    public String getColumnName(int col) {
        return Peso.getFuncaoCoeficienteDesc(this._getColumnName(col));
    }

    @Override
    public Object getValueAt(int row, int col) {
        return this.estatisticas.get(row).get(col);
    }

    @Override
    public Class getColumnClass(int c) {
        return (getValueAt(0, c)!=null)?getValueAt(0, c).getClass():String.class;
    }

    /*
     * 
     */
    @Override
    public boolean isCellEditable(int row, int col) {
	return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     *//*
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }*/

    @Override
    public void dbUpdated(MoinhoDbEvent e) {
	this.estatisticas = e.getEstatisticas();
	this.fireTableDataChanged();
    }
}
