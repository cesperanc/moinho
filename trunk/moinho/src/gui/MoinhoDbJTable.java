package gui;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;

/**
 * @author Cláudio Esperança
 */
public class MoinhoDbJTable extends JTable implements TableModelListener {
    
    public MoinhoDbJTable(){
	super(new MoinhoDbTableModel());
	this._init();
    }
    
    private void _init(){
	this.getModel().addTableModelListener(this);
	this.setAutoCreateRowSorter(true);
    }

}
