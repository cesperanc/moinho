package gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class PecaJogoMoinhoCellRenderer extends JLabel implements TableCellRenderer {
	
	private static ImageIcon cornerUpLeft; 
	private static ImageIcon cornerUpRight;
	private static ImageIcon cornerDownLeft;
	private static ImageIcon cornerDownRight;
	private static ImageIcon tLeft; 
	private static ImageIcon tRight;
	private static ImageIcon tUp;
	private static ImageIcon tDown;
	private static ImageIcon cross;

	private static ImageIcon horizontal;
	private static ImageIcon vertical;
	
	private static ImageIcon cornerUpLeftWhite; 
	private static ImageIcon cornerUpRightWhite;
	private static ImageIcon cornerDownLeftWhite;
	private static ImageIcon cornerDownRightWhite;
	private static ImageIcon tLeftWhite; 
	private static ImageIcon tRightWhite;
	private static ImageIcon tUpWhite;
	private static ImageIcon tDownWhite;
	private static ImageIcon crossWhite;

	private static ImageIcon cornerUpLeftBlack; 
	private static ImageIcon cornerUpRightBlack;
	private static ImageIcon cornerDownLeftBlack;
	private static ImageIcon cornerDownRightBlack;
	private static ImageIcon tLeftBlack; 
	private static ImageIcon tRightBlack;
	private static ImageIcon tUpBlack;
	private static ImageIcon tDownBlack;
	private static ImageIcon crossBlack;


    public PecaJogoMoinhoCellRenderer() {
        setBackground(Color.WHITE);
        setOpaque(true);
        setFont(new Font("Arial", Font.BOLD, 49));
        this.setHorizontalAlignment(SwingConstants.CENTER);
        cornerUpLeft = resizeIcon("/images/cornerupleft.png","Canto superior esquerdo");
        cornerUpRight = resizeIcon("/images/cornerupright.png","Canto superior direito");
        cornerDownLeft = resizeIcon("/images/cornerdownleft.png","Canto inferior esquerdo");
        cornerDownRight = resizeIcon("/images/cornerdownright.png","Canto inferior direito");
        tLeft = resizeIcon("/images/tleft.png","T para a esquerda");
        tRight = resizeIcon("/images/tright.png","T para a direita");
        tUp = resizeIcon("/images/tup.png","T para cima");
        tDown = resizeIcon("/images/tdown.png","T para baixo");
        cross = resizeIcon("/images/cross.png","Cruz");

        horizontal = resizeIcon("/images/hor.png","Linha horizontal");
        vertical = resizeIcon("/images/ver.png","Linha vertical");
        
        cornerUpLeftWhite = resizeIcon("/images/cornerupleftwhite.png","Canto superior esquerdo com peca branca");
        cornerUpRightWhite = resizeIcon("/images/corneruprightwhite.png","Canto superior direito com peca branca");
        cornerDownLeftWhite = resizeIcon("/images/cornerdownleftwhite.png","Canto inferior esquerdo com peca branca");
        cornerDownRightWhite = resizeIcon("/images/cornerdownrightwhite.png","Canto inferior direito com peca branca");
        tLeftWhite = resizeIcon("/images/tleftwhite.png","T para esquerda com peca branca");
        tRightWhite = resizeIcon("/images/trightwhite.png","T para a direita com peca branca");
        tUpWhite = resizeIcon("/images/tupwhite.png","T para cima com peca branca");
        tDownWhite = resizeIcon("/images/tdownwhite.png","T para baixo com peca branca");
        crossWhite = resizeIcon("/images/crosswhite.png","Cruz com peca branca");

        cornerUpLeftBlack = resizeIcon("/images/cornerupleftblack.png","Canto superior esquerdo com peca preta");
        
        cornerUpRightBlack = resizeIcon("/images/corneruprightblack.png","Canto superior direito com peca preta");
        cornerDownLeftBlack = resizeIcon("/images/cornerdownleftblack.png","Canto inferior esquerdo com peca preta");
        cornerDownRightBlack = resizeIcon("/images/cornerdownrightblack.png","Canto inferior direito com peca preta");
        tLeftBlack = resizeIcon("/images/tleftblack.png","T para esquerda com peca preta");
        tRightBlack = resizeIcon("/images/trightblack.png","T para direita com peca preta");
        tUpBlack = resizeIcon("/images/tupblack.png","T para cima com peca preta");
        tDownBlack = resizeIcon("/images/tdownblack.png","T para baixo com peca preta");
        crossBlack = resizeIcon("/images/crossblack.png","Cruz com peca preta");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {       
        if(row == column) {
        	if (row<3) {
        		setIcon(getCornerUpLeftIcon(value));
        	} else if (row == 3){
        		setIcon(null);
        	} else {
        		setIcon(getCornerDownRightIcon(value));
        	}
        	
        } else if(row == 6 - column) {
        	if(column < 3){
        		setIcon(getCornerDownLeftIcon(value));
        	} else {
        		setIcon(getCornerUpRightIcon(value));
        	}
        } else if(row == 3){
        	if(column == 0 || column == 4) {
        		setIcon(getTRightIcon(value));
        	} else if(column == 1 || column == 5)  {
        		setIcon(getCrossIcon(value));
        	} else {
        		setIcon(getTLeftIcon(value));
        	}
        		
        } else if(column == 3){
        	if(row == 0 || row == 4) {
        		setIcon(getTDownIcon(value));
        	} else if(row == 1 || row == 5)  {
        		setIcon(getCrossIcon(value));
        	} else {
        		setIcon(getTUpIcon(value));
        	}
        		
        } else if((row > column && column < 2 && row > 0 && row < 6) || (column > row && column > 4 && row > 0 && row < 6)){        	
        		setIcon(vertical);       		
        } else {
        	setIcon(horizontal);
        }        
        return this;
    }
    
    private Icon getCornerUpLeftIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return cornerUpLeft;
    	else
    		return ((Character) value).charValue() == 'X' ? cornerUpLeftWhite : cornerUpLeftBlack;
    }
    
    private Icon getCornerUpRightIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return cornerUpRight;
    	else
    		return ((Character) value).charValue() == 'X' ? cornerUpRightWhite : cornerUpRightBlack;
    }
    
    private Icon getCornerDownLeftIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return cornerDownLeft;
    	else
    		return ((Character) value).charValue() == 'X' ? cornerDownLeftWhite : cornerDownLeftBlack;
    }
    
    private Icon getCornerDownRightIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return cornerDownRight;
    	else
    		return ((Character) value).charValue() == 'X' ? cornerDownRightWhite : cornerDownRightBlack;
    }
    
    private Icon getTUpIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return tUp;
    	else
    		return ((Character) value).charValue() == 'X' ? tUpWhite : tUpBlack;
    }
    
    private Icon getTDownIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return tDown;
    	else
    		return ((Character) value).charValue() == 'X' ? tDownWhite : tDownBlack;
    }
    
    private Icon getTLeftIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return tLeft;
    	else
    		return ((Character) value).charValue() == 'X' ? tLeftWhite : tLeftBlack;
    }
    
    private Icon getTRightIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ' ) 
    		return tRight;
    	else
    		return ((Character) value).charValue() == 'X' ? tRightWhite : tRightBlack;
    }
    
    private Icon getCrossIcon(Object value){
    	if (value == null || ((Character) value).charValue() == ' ') 
    		return cross;
    	else
    		return ((Character) value).charValue() == 'X' ? crossWhite : crossBlack;
    }
    /**
     *
     * @param src
     * @param title
     * @return resized ImageIcon
     */
    private ImageIcon resizeIcon(String src, String title){
        return new ImageIcon((new ImageIcon(getClass().getResource(src),title)).getImage().getScaledInstance(Propriedades.CELL_WIDTH, Propriedades.CELL_WIDTH,  java.awt.Image.SCALE_SMOOTH));
    }
    
}
