package gui;

import db.MoinhoDB;
import db.Peso;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import javax.swing.AbstractButton;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moinho.*;

public class FrameAplicacao extends JFrame implements JogoListener, ChangeListener, TreinoListener{

    private AgenteMoinho agenteBrancas = new AgenteMoinho("XPTO cruzes");
    private AgenteMoinho agentePretas = new AgenteMoinho("XPTO bolas");
    private EstadoJogoMoinho estadoJogoMoinho = new EstadoJogoMoinho();
    private JogoMoinhoTableModel jogoMoinhoTableModel = new JogoMoinhoTableModel(estadoJogoMoinho);
    JogoDoMoinho jogoBrancas;
    JogoDoMoinho jogoPretas;
    private boolean agenteBrancoAJogar;
    private boolean agentePretasAJogar;
    private boolean agenteAJogar;
    //private JTextField profundidadeBrancas = new JTextField("3");
    //private NumericTextField profundidadeBrancas = new NumericTextField("3",3);
    private JSpinner profundidadeBrancas;
    private JTable tabelaJogo = new JTable();
    private JRadioButton humanoBrancas = new JRadioButton("Jogador humano");
    private JRadioButton xptoBrancas = new JRadioButton("XPTO");
    private String[] nomesAlgoritmos = {"Minimax", "Alfa-beta"};
    private JComboBox algoritmosBrancas = new JComboBox(nomesAlgoritmos);
    private JButton botaoNovoJogo = new JButton("Novo jogo");
    //private JTextField profundidadePretas = new JTextField("3");
    //private NumericTextField profundidadePretas = new NumericTextField("3",3);
    private JSpinner profundidadePretas;

    private JRadioButton humanoPretas = new JRadioButton("Jogador humano");
    private JRadioButton xptoPretas = new JRadioButton("XPTO");
    private JComboBox algoritmosPretas = new JComboBox(nomesAlgoritmos);
    private JLabel infoJogo = new JLabel("Bem vindo...");
    private JLabel pecasPretasPorJogar;
    private JLabel pecasBrancasPorJogar;
    private JLabel pecasPretasComidas;
    private JLabel pecasBrancasComidas;
    private javax.swing.JSpinner jogosEmSimultaneo;
    private javax.swing.JPanel buttonsContainer;
    private javax.swing.JToggleButton goTrain;
    private javax.swing.JScrollPane jsResults;
    private javax.swing.JScrollPane jsStatistics;
    private javax.swing.JTextArea jtResults;
    private javax.swing.JPanel mainTabbedPaneContent;
    private javax.swing.JPanel mainTabbedPaneContentWrapper;
    private MoinhoDbJTable tableStatistics;
    private javax.swing.JPanel trainerData;
    private javax.swing.JSplitPane trainerDataSplitter;
    
    private static String BRANCAS = "brancas";
    private static String PRETAS = "pretas";
    private HashMap<String, HashMap<String, JSpinner>> pesosFields = new HashMap<String, HashMap<String, JSpinner>>();
    JTabbedPane tabbedPanePretas = new JTabbedPane();
    JTabbedPane tabbedPaneBrancas = new JTabbedPane();
    
    private boolean treinosActivos = false;


//    private JCheckBox mostraGrelha = new JCheckBox("Mostrar grelha",true);
    private boolean restart;
    private Semaphore semaphore = new Semaphore(1);

    public FrameAplicacao() {
        try {
	    this.pesosFields.put(PRETAS, new HashMap<String, JSpinner>());
	    this.pesosFields.put(BRANCAS, new HashMap<String, JSpinner>());
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Jogo do Moinho");
	this.setIconImage(new ImageIcon(getClass().getResource("/images/black.png"),"peca branca").getImage());
	
        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());
	
	JTabbedPane mainTabbedPane = new JTabbedPane();
	contentPane.add(mainTabbedPane);

        JPanel painelConfiguracao = new JPanel();
        painelConfiguracao.setLayout(new BoxLayout(painelConfiguracao, BoxLayout.Y_AXIS));


	Peso melhorJogadorConhecido = Treino.getMelhorIndividuo();
	melhorJogadorConhecido = (melhorJogadorConhecido==null)?new Peso():melhorJogadorConhecido;

        /// Inicio das brancas
        JPanel panel1Brancas = new JPanel();
        panel1Brancas.setLayout(new GridLayout(4, 1) );

        ButtonGroup grupoBrancas = new ButtonGroup();
        grupoBrancas.add(humanoBrancas);
        grupoBrancas.add(xptoBrancas);

        JPanel painelRadioButtons = new JPanel(new FlowLayout());
        painelRadioButtons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Brancas (joga primeiro)"),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        panel1Brancas.add(humanoBrancas);
        panel1Brancas.add(xptoBrancas);
        
        humanoBrancas.setSelected(true);
        painelConfiguracao.add(painelRadioButtons);

        JPanel painelAlgoritmo = new JPanel(new FlowLayout());
        painelAlgoritmo.add(new JLabel("Algoritmo do XPTO: "));
        painelAlgoritmo.add(algoritmosBrancas);
        if(jogos.Minimax.NOME.equalsIgnoreCase(melhorJogadorConhecido.getAlgoritmo().toString())){
            algoritmosBrancas.setSelectedIndex(0);
        }else{
            algoritmosBrancas.setSelectedIndex(1);
        }
        panel1Brancas.add(painelAlgoritmo);

        JPanel painelProfundidade = new JPanel(new FlowLayout());
        painelProfundidade.add(new JLabel("Prof. máx. de pesquisa: "));
        profundidadeBrancas = new JSpinner(new SpinnerNumberModel(melhorJogadorConhecido.getProfundidade(), 0, 1000, 1));
        painelProfundidade.add(profundidadeBrancas);
        panel1Brancas.add(painelProfundidade);

        tabbedPaneBrancas.add( "Bases", panel1Brancas);

        /// Inicio das pretas
        JPanel panel1Pretas = new JPanel();
        panel1Pretas.setLayout(new GridLayout(4, 1) );


        ButtonGroup grupoPretas = new ButtonGroup();
        grupoPretas.add(humanoPretas);
        grupoPretas.add(xptoPretas);
            JPanel painelRadioButtons2 = new JPanel(new FlowLayout());
        painelRadioButtons2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Pretas"),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

            panel1Pretas.add(humanoPretas);
            panel1Pretas.add(xptoPretas);
        humanoPretas.setSelected(true);
            
        painelConfiguracao.add(painelRadioButtons2);
        grupoPretas.setSelected(xptoPretas.getModel(), true);

        JPanel painelAlgoritmo2 = new JPanel(new FlowLayout());
        painelAlgoritmo2.add(new JLabel("Algoritmo do XPTO: "));
        painelAlgoritmo2.add(algoritmosPretas);
        if(jogos.Minimax.NOME.equalsIgnoreCase(melhorJogadorConhecido.getAlgoritmo().toString())){
        algoritmosPretas.setSelectedIndex(0);
        }else{
            algoritmosPretas.setSelectedIndex(1);
        }

        panel1Pretas.add(painelAlgoritmo2);

        JPanel painelProfundidade2 = new JPanel(new FlowLayout());
        painelProfundidade2.add(new JLabel("Prof. máx. de pesquisa: "));
        //profundidadePretas.setColumns(4);
        profundidadePretas = new JSpinner(new SpinnerNumberModel(melhorJogadorConhecido.getProfundidade(), 0, 1000, 1));
        painelProfundidade2.add(profundidadePretas);
        panel1Pretas.add(painelProfundidade2);


	tabbedPanePretas.addTab( "Base", panel1Pretas );

	JPanel panel2Pretas = new JPanel();
        panel2Pretas.setLayout(new GridLayout(6, 4) );
	JPanel panel2Brancas = new JPanel();
        panel2Brancas.setLayout(new GridLayout(6, 4) );
	
	int counter = 1;
        String negative = "";
	HashMap<String, Integer> pesos = Peso.getCoeficientesPorOmissao();
	for(String peso : Peso.getNomesCoeficientesPorOmissao()){
	    // Pretas
	    JPanel subPanel = new JPanel();

            if(counter>9){
                counter = 1;
                negative = "N";
            }
             JLabel pLabel = new JLabel("A"+counter+negative+": ");
            pLabel.setToolTipText(Peso.getFuncaoCoeficienteDesc(peso));
	    subPanel.add(pLabel);
            
            JSpinner tf = new JSpinner(new SpinnerNumberModel(melhorJogadorConhecido.getCoeficiente(peso),  -Treino.valorMaximoCoeficiente, Treino.valorMaximoCoeficiente, 1));
	    subPanel.add(tf);
	    panel2Pretas.add(subPanel);
	    
	    pesosFields.get(PRETAS).put(peso, tf);
	    
	    // Brancas
	    subPanel = new JPanel();
	    JLabel aLabel = new JLabel("A"+counter+negative+": ");
	    subPanel.add(aLabel);
	    aLabel.setToolTipText(Peso.getFuncaoCoeficienteDesc(peso));
	    tf = new JSpinner(new SpinnerNumberModel(melhorJogadorConhecido.getCoeficiente(peso),  -Treino.valorMaximoCoeficiente, Treino.valorMaximoCoeficiente, 1));
	    subPanel.add(tf);
	    aLabel.setLabelFor(tf);
	    panel2Brancas.add(subPanel);
	    
	    pesosFields.get(BRANCAS).put(peso, tf);
	    
	    
	    counter++;
	}

	tabbedPanePretas.addTab( "Configuração da IA", panel2Pretas );
        tabbedPaneBrancas.addTab( "Configuração da IA", panel2Brancas );
	
        painelRadioButtons.add(tabbedPaneBrancas);
	painelRadioButtons2.add(tabbedPanePretas);

        botaoNovoJogo.addActionListener(new BotaoNovoJogo_actionAdapter(this));
        painelConfiguracao.add(botaoNovoJogo);

        JPanel painelGlobal = new JPanel(new BorderLayout());

            JPanel painelMeio = new JPanel(new FlowLayout());
            painelMeio.add(painelConfiguracao);
            painelMeio.add(tabelaJogo);

            JPanel painelEstadoJogo = new JPanel();
            painelEstadoJogo.setLayout(new BoxLayout(painelEstadoJogo, BoxLayout.Y_AXIS));

            JPanel painelRadioButtons3 = new JPanel(new GridLayout(2, 1));
            painelRadioButtons3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Peças por jogar"),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

            
            pecasBrancasPorJogar = new JLabel(Integer.toString(EstadoJogoMoinho.NUMEROMAXIMOPECAS),new ImageIcon(getClass().getResource("/images/white.png"),"peca branca"), JLabel.CENTER);
            pecasPretasPorJogar = new JLabel(Integer.toString(EstadoJogoMoinho.NUMEROMAXIMOPECAS) ,new ImageIcon(getClass().getResource("/images/black.png"),"peca preta"),JLabel.CENTER);

            painelRadioButtons3.add(pecasBrancasPorJogar);
            painelRadioButtons3.add(pecasPretasPorJogar);

            painelEstadoJogo.add(painelRadioButtons3);

            JPanel painelRadioButtons4 = new JPanel(new GridLayout(2, 1));
            painelRadioButtons4.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Peças comidas"),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

            pecasBrancasComidas = new JLabel("0",new ImageIcon(getClass().getResource("/images/white.png"),"peca branca"), JLabel.CENTER);
            pecasPretasComidas = new JLabel("0" ,new ImageIcon(getClass().getResource("/images/black.png"),"peca preta"),JLabel.CENTER);
            painelRadioButtons4.add(pecasBrancasComidas);
            painelRadioButtons4.add(pecasPretasComidas);

            
            painelEstadoJogo.add(painelRadioButtons4);
            painelMeio.add(painelEstadoJogo);
        


        painelGlobal.add(painelMeio,BorderLayout.PAGE_START);
        
        JPanel painelFundo = new JPanel(new FlowLayout(java.awt.FlowLayout.CENTER));
        painelFundo.add(infoJogo);
        painelGlobal.add(painelFundo);

        
        mainTabbedPane.addTab("Modo de Jogo", painelGlobal);

        estadoJogoMoinho.addJogoListener(this);

        tabelaJogo.addMouseListener(new TabelaJogo_mouseAdapter(this));
        configurarTabela(tabelaJogo);
	
	
	// Modo de treino
	
	//mainTabbedPane = new javax.swing.JTabbedPane();
        mainTabbedPaneContentWrapper = new javax.swing.JPanel();
        mainTabbedPaneContent = new javax.swing.JPanel();
        trainerData = new javax.swing.JPanel();
        trainerDataSplitter = new javax.swing.JSplitPane();
        jsStatistics = new javax.swing.JScrollPane();
        tableStatistics = new MoinhoDbJTable();
	tableStatistics.getModel().addTableModelListener(tableStatistics);
	tableStatistics.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jsResults = new javax.swing.JScrollPane();
        jtResults = new javax.swing.JTextArea();
        buttonsContainer = new javax.swing.JPanel();
        goTrain = new javax.swing.JToggleButton();
        jogosEmSimultaneo = new javax.swing.JSpinner( new SpinnerNumberModel(5, 1, 256, 1));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        //mainTabbedPaneContent.setLayout(new javax.swing.BoxLayout(mainTabbedPaneContent, javax.swing.BoxLayout.Y_AXIS));
        javax.swing.GroupLayout mainTabbedPaneContentLayout = new javax.swing.GroupLayout(mainTabbedPaneContent);
        mainTabbedPaneContent.setLayout(mainTabbedPaneContentLayout);
        mainTabbedPaneContentLayout.setHorizontalGroup(
            mainTabbedPaneContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainTabbedPaneContentLayout.createSequentialGroup()
                .addComponent(buttonsContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(trainerData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainTabbedPaneContentLayout.setVerticalGroup(
           mainTabbedPaneContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainTabbedPaneContentLayout.createSequentialGroup()
                .addComponent(trainerData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        trainerDataSplitter.setDividerLocation(200);
        trainerDataSplitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jsStatistics.setBorder(javax.swing.BorderFactory.createTitledBorder("Treinos Anteriores"));

        jsStatistics.setViewportView(tableStatistics);

        trainerDataSplitter.setLeftComponent(jsStatistics);

        jsResults.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        jtResults.setColumns(20);
        jtResults.setRows(5);
	jtResults.setLineWrap(true);
	jtResults.setWrapStyleWord(true);
        jsResults.setViewportView(jtResults);

        trainerDataSplitter.setRightComponent(jsResults);

        javax.swing.GroupLayout trainerDataLayout = new javax.swing.GroupLayout(trainerData);
        trainerData.setLayout(trainerDataLayout);
        trainerDataLayout.setHorizontalGroup(
            trainerDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 525, Short.MAX_VALUE)
            .addGroup(trainerDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(trainerDataSplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE))
        );
        trainerDataLayout.setVerticalGroup(
            trainerDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
            .addGroup(trainerDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(trainerDataSplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE))
        );

        //mainTabbedPaneContent.add(trainerData);
        //buttonsContainer.setPreferredSize(new java.awt.Dimension(527, 50));

        goTrain.setText("Iniciar treino");
	goTrain.addActionListener(new java.awt.event.ActionListener() {
	    @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goTrainActionPerformed(evt);
            }
        });
	
	buttonsContainer.add(new JLabel("Número de jogos em simultâneo"));
        buttonsContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));


        buttonsContainer.add(jogosEmSimultaneo);
        buttonsContainer.add(goTrain);

        //mainTabbedPaneContent.add(jogosEmSimultaneo);
        //mainTabbedPaneContent.add(buttonsContainer);

        javax.swing.GroupLayout mainTabbedPaneContentWrapperLayout = new javax.swing.GroupLayout(mainTabbedPaneContentWrapper);
        mainTabbedPaneContentWrapper.setLayout(mainTabbedPaneContentWrapperLayout);
        mainTabbedPaneContentWrapperLayout.setHorizontalGroup(
            mainTabbedPaneContentWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainTabbedPaneContentWrapperLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPaneContent, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainTabbedPaneContentWrapperLayout.setVerticalGroup(
            mainTabbedPaneContentWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainTabbedPaneContentWrapperLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPaneContent, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabbedPane.addTab("Modo de Treino", mainTabbedPaneContentWrapper);
	
	// Preparar o jogo
	
	xptoPretas.addChangeListener(this);
	xptoBrancas.addChangeListener(this);
	this.stateChanged(null);

        pack();
    }

    private void configurarTabela(JTable table){
        tabelaJogo.setModel(jogoMoinhoTableModel);
        table.setDefaultRenderer(Object.class, new PecaJogoMoinhoCellRenderer());
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(Propriedades.CELL_WIDTH);
        }
        table.setRowHeight(Propriedades.CELL_HEIGHT);
        table.setBorder(BorderFactory.createLineBorder(Color.black));
        table.setIntercellSpacing(new Dimension(0,0));

        table.setShowGrid(true);
    }

    public void jButtonNovoJogo_actionPerformed(ActionEvent e) {
        restart = true;
        while (agenteAJogar){} //espera que termine uma jogada
        estadoJogoMoinho.reiniciar();
        restart = false;
	boolean human = true;
	
        if (xptoBrancas.isSelected()) {
            //jogoBrancas = new JogoDoMoinho(estadoJogoMoinho, EstadoJogoMoinho.X, Integer.parseInt(profundidadeBrancas.getText()));
            jogoBrancas = new JogoDoMoinho(estadoJogoMoinho, EstadoJogoMoinho.X, (Integer)profundidadeBrancas.getValue());
	    this.updatePesos(jogoBrancas, BRANCAS);
	    
            agenteBrancas.setJogo(jogoBrancas);
            if (algoritmosBrancas.getSelectedIndex() == 0) {
                agenteBrancas.usarMinimax();
            } else {
                agenteBrancas.usarAlfabeta();
            }
	    // Se este jogador vai jogar, então vamos guardar informações sobre ele na base de dados
	    MoinhoDB.insertPeso(jogoBrancas.getPeso());

            agenteBrancoAJogar = true;
            agentePretasAJogar = false;
            jogadasDoAgenteBranco();
	    human = false;
        }

        if (xptoPretas.isSelected()) {
            //jogoPretas = new JogoDoMoinho(estadoJogoMoinho, EstadoJogoMoinho.O, Integer.parseInt(profundidadePretas.getText()));
            jogoPretas = new JogoDoMoinho(estadoJogoMoinho, EstadoJogoMoinho.O, (Integer)profundidadePretas.getValue());
	    this.updatePesos(jogoPretas, PRETAS);
	    
            agentePretas.setJogo(jogoPretas);
            if (algoritmosPretas.getSelectedIndex() == 0) {
                agentePretas.usarMinimax();
            } else {
                agentePretas.usarAlfabeta();
            }
	    // Se este jogador vai jogar, então vamos guardar informações sobre ele na base de dados
	    MoinhoDB.insertPeso(jogoPretas.getPeso());

            agenteBrancoAJogar = true;
            agentePretasAJogar = false;
            jogadasDoAgentePretas();
	    human = false;
        }
	
	// Activar a possibilidade de dois humanos jogarem...
	if(human){
	    //jogoBrancas = new JogoDoMoinho(estadoJogoMoinho, EstadoJogoMoinho.O, Integer.parseInt(profundidadePretas.getText()));
            jogoBrancas = new JogoDoMoinho(estadoJogoMoinho, EstadoJogoMoinho.O, (Integer)profundidadePretas.getValue());
            agenteBrancoAJogar = true;
            agentePretasAJogar = false;
	}
    }

    void tabelaJogo_mouseClicked(MouseEvent e) {
        if (jogoBrancas != null || jogoPretas != null) {
            try {
                int linha = tabelaJogo.rowAtPoint(e.getPoint());
                int coluna = tabelaJogo.columnAtPoint(e.getPoint());
		semaphore.acquire();
		if (!agenteAJogar && !estadoJogoMoinho.terminou()){
		    
		    if (estadoJogoMoinho.jogadorPodeComerPeca(linha, coluna)) {
			    
			// Se o jogador pode comer uma peça do adversário
			if(estadoJogoMoinho.comerPeca(linha, coluna)){
			    // Se o jogador actual comeu uma peça dar a hipótese de jogo ao computador
			    agenteBrancoAJogar = xptoBrancas.isSelected();
			    agentePretasAJogar = xptoPretas.isSelected();
			}
		    }else if (estadoJogoMoinho.isPecaDoJogador(linha, coluna) && (estadoJogoMoinho.pecasDoJogadorPodemVoar() || estadoJogoMoinho.pecaPodeSerMovida(linha, coluna))) {
			// Se as peças do jogador podem voar e se a peça clicada pertence ao utilizador
			estadoJogoMoinho.removerPeca(linha, coluna);
			
		    }else if (estadoJogoMoinho.isJogadaValida(linha, coluna)) {
			// Se a jogada for válida, colocar a peça
			estadoJogoMoinho.colocarPeca(linha, coluna);
			
			// Se o jogador actual não fez três em linha, dar a hipótese de jogo ao computador
			if(!estadoJogoMoinho.jogadorTemTresEmLinha(linha, coluna)){
			    agenteBrancoAJogar = xptoBrancas.isSelected();
			    agentePretasAJogar = xptoPretas.isSelected();
			    
			}
		    }
		}
		semaphore.release();
            } catch (Exception ex) {
		ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void jogoChanged(JogoEvent pe) {
	if(pe != null && pe.getEstado()!=null){
            pecasPretasPorJogar.setText(Integer.toString(pe.getEstado().getNumeroMaximoDePecasJogador(EstadoJogoMoinho.O)-pe.getEstado().getNumeroDePecasJogador(EstadoJogoMoinho.O)));
            pecasBrancasPorJogar.setText(Integer.toString(pe.getEstado().getNumeroMaximoDePecasJogador(EstadoJogoMoinho.X)-pe.getEstado().getNumeroDePecasJogador(EstadoJogoMoinho.X)));
	
            pecasPretasComidas.setText(Integer.toString(EstadoJogoMoinho.NUMEROMAXIMOPECAS-pe.getEstado().getNumeroMaximoDePecasJogador(EstadoJogoMoinho.O)));
            pecasBrancasComidas.setText(Integer.toString(EstadoJogoMoinho.NUMEROMAXIMOPECAS-pe.getEstado().getNumeroMaximoDePecasJogador(EstadoJogoMoinho.X)));
	    
        }
        if (estadoJogoMoinho.terminou()) {
            if (estadoJogoMoinho.isVencedor(EstadoJogoMoinho.X) && xptoBrancas.isSelected()) {
                JOptionPane.showMessageDialog(this, "Ganhou o " + agenteBrancas.getNome() + "!!", "Resultado do Jogo", JOptionPane.INFORMATION_MESSAGE);
            } else if (estadoJogoMoinho.isVencedor(EstadoJogoMoinho.O) && xptoPretas.isSelected()) {
                JOptionPane.showMessageDialog(this, "Ganhou o " + agentePretas.getNome() + "!!", "Resultado do Jogo", JOptionPane.INFORMATION_MESSAGE);
            } else if (estadoJogoMoinho.isVencedor(EstadoJogoMoinho.X) || estadoJogoMoinho.isVencedor(EstadoJogoMoinho.O)) {
                JOptionPane.showMessageDialog(this, "Você ganhou. Parabéns!", "Resultado do Jogo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Empataram!", "Resultado do Jogo", JOptionPane.INFORMATION_MESSAGE);
            }
	    
	    botaoNovoJogo.setEnabled(true);
        }else{
            if(pe.getEstado().getJogador() == EstadoJogoMoinho.X){
                //jogam as peças Brancas
                infoJogo.setText("São as BRANCAS a jogar!");
                if(pe.getEstado().jogadorPodeComerPeca()){
                    infoJogo.setText("São as BRANCAS a jogar! Têm de comer uma peça preta");
                }else if(pe.getEstado().jogadorPodeColocarPecas(EstadoJogoMoinho.X) ){
                    infoJogo.setText("São as BRANCAS a jogar! Pode colocar peças");
                }else if(pe.getEstado().pecasDoJogadorPodemVoar()){
                    infoJogo.setText("São as BRANCAS a jogar! As peças podem voar");
		}
            }else if(pe.getEstado().getJogador() == EstadoJogoMoinho.O){
                //jogam as Pretas
                infoJogo.setText("São as PRETAS a jogar!");
                if(pe.getEstado().jogadorPodeComerPeca()){
                    infoJogo.setText("São as PRETAS a jogar! Têm de comer uma peça branca");
                }else if(pe.getEstado().jogadorPodeColocarPecas(EstadoJogoMoinho.O) ){
                    infoJogo.setText("São as PRETAS a jogar! Pode colocar peças");
                }else if(pe.getEstado().pecasDoJogadorPodemVoar()){
                    infoJogo.setText("São as PRETAS a jogar! As peças podem voar");
		}
	    }
	}
    }

    private void jogadasDoAgenteBranco() {
        SwingWorker worker = new SwingWorker<Void, Void>() {

	    @Override
            public Void doInBackground() {
                try {
                    while (!estadoJogoMoinho.terminou() && !restart) {
                        semaphore.acquire();
                        if (agenteBrancoAJogar) {
			    botaoNovoJogo.setEnabled(false);
                            agenteAJogar = true;
                            agenteBrancas.jogar();
                            agenteAJogar = false;
                            agenteBrancoAJogar = false;
                            agentePretasAJogar = xptoPretas.isSelected();
			    botaoNovoJogo.setEnabled(true);
                        }
                        semaphore.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        worker.execute();
    }

    private void jogadasDoAgentePretas() {
        SwingWorker worker = new SwingWorker<Void, Void>() {

	    @Override
            public Void doInBackground() {
                try {
                    while (!estadoJogoMoinho.terminou() && !restart) {
                        semaphore.acquire();
                        if (agentePretasAJogar) {
			    botaoNovoJogo.setEnabled(false);
                            agenteAJogar = true;
                            agentePretas.jogar();
                            agenteAJogar = false;
                            agentePretasAJogar = false;
                            agenteBrancoAJogar = xptoBrancas.isSelected();
			    botaoNovoJogo.setEnabled(true);
                        }
                        semaphore.release();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        worker.execute();
    }

    /**
     * Executado sempre que existem alterações nos radios do jogador XPTO
     * @param e 
     */
    @Override
    public void stateChanged(ChangeEvent e) {
	try{
	    // Pretas
	    algoritmosPretas.setEnabled(xptoPretas.isSelected());
	    profundidadePretas.setEnabled(xptoPretas.isSelected());
	    tabbedPanePretas.setEnabledAt(1, xptoPretas.isSelected());
	    
	    // Brancas
	    algoritmosBrancas.setEnabled(xptoBrancas.isSelected());
	    profundidadeBrancas.setEnabled(xptoBrancas.isSelected());
	    tabbedPaneBrancas.setEnabledAt(1, xptoBrancas.isSelected());
	}catch(Exception ex){
	    ex.printStackTrace();
	}
    }
    
    /**
     * Define os coeficiente de cada componente na função de avaliação
     * @param jogo com o jogo onde os pesos devem ser definidos
     * @param jogador com a string do jogador com os componentes de onde os dados devem ser obtidos
     */
    public void updatePesos(JogoDoMoinho jogo, String jogador){
	HashMap<String, JSpinner> coeficientes = this.pesosFields.get(jogador);
	Peso peso = jogo.getPeso();
	if(peso!=null){
	    for(String coeficiente : coeficientes.keySet()){
		jogo.getPeso().setCoeficiente(coeficiente, (Integer)coeficientes.get(coeficiente).getValue());
	    }
	}else{
	    System.err.println("O peso deve ser definido antes dos seus coeficientes");
	}
    }
    
    /**
     * Adiciona informações de estado à consola no modo de treino
     * @param status com a String a adicionar
     */
    public synchronized void updateStatus(String status){
	this.jtResults.append(status);
	this.jtResults.setCaretPosition(this.jtResults.getText().length());
    }
    
    /**
     * Inicia uma nova thread para executar um jogo em segundo plano
     * @param evt 
     */
    private void goTrainActionPerformed(java.awt.event.ActionEvent evt) {
	AbstractButton abstractButton = (AbstractButton) evt.getSource();
	if(abstractButton.getModel().isSelected()){
	    this.treinosActivos = true;
	    abstractButton.setText("Parar treino");
	    int jogosConcorrentes = (Integer)(this.jogosEmSimultaneo.getValue());
	    for(int a=0; a<jogosConcorrentes; a++){
		this.treinoFinished();
	    }
	}else{
	    this.treinosActivos = false;
	    abstractButton.setText("Iniciar treino");
	}
	this.jogosEmSimultaneo.setEnabled(!this.treinosActivos);
    }
    
    /**
     * @return true se o mode de treino está activo
     */
    public boolean isTreinoActivo(){
	return this.treinosActivos;
    }

    @Override
    public void treinoFinished() {
	if(this.treinosActivos){
	    new Treino(this);
	}
    }
}

class BotaoNovoJogo_actionAdapter implements ActionListener {

    private FrameAplicacao adaptee;

    BotaoNovoJogo_actionAdapter(FrameAplicacao adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonNovoJogo_actionPerformed(e);
    }
}

class TabelaJogo_mouseAdapter extends java.awt.event.MouseAdapter {

    private FrameAplicacao adaptee;

    TabelaJogo_mouseAdapter(FrameAplicacao adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        adaptee.tabelaJogo_mouseClicked(e);
    }
}

 class NumericTextField extends JTextField {

    public NumericTextField (String _initialStr, int _col) {
        super (_initialStr, _col) ;

        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped (KeyEvent e){
                char c = e.getKeyChar() ;

                if (! ((c==KeyEvent.VK_BACK_SPACE) || (c==KeyEvent.VK_DELETE) ||  (c== KeyEvent.VK_ENTER)  || (c == KeyEvent.VK_TAB)  ||  (Character.isDigit(c)))) {
                    e.consume() ;
                }
            }
        });
    }
    
   
}
