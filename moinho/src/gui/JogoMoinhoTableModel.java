package gui;

import javax.swing.table.AbstractTableModel;
import moinho.*;

public class JogoMoinhoTableModel extends AbstractTableModel implements JogoListener{

    private EstadoJogoMoinho estadoJogo;

    public JogoMoinhoTableModel(EstadoJogoMoinho estadoJogo) {
        if(estadoJogo == null)
            throw new NullPointerException("Estado nao pode ser null");
        this.estadoJogo = estadoJogo;
        this.estadoJogo.addJogoListener(this);
    }

    @Override
    public int getColumnCount() {
        return EstadoJogoMoinho.DIMENSAO;
    }

    @Override
    public int getRowCount() {
        return EstadoJogoMoinho.DIMENSAO;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return new Character(estadoJogo.getValorPeca(row, col));
    }

    @Override
    public void jogoChanged(JogoEvent pe){
        fireTableDataChanged();
    }

    public void setJogo(EstadoJogoMoinho estadoJogo){
        if(estadoJogo == null)
          throw new NullPointerException("Puzzle nao pode ser null");
        this.estadoJogo.removeJogoListener(this);
        this.estadoJogo = estadoJogo;
        estadoJogo.addJogoListener(this);
        fireTableDataChanged();
    }
}
