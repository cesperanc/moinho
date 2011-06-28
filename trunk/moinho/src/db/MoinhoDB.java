package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Classe para gestão da persistência dos dados estatísticos obtidos
 * @author Cláudio Esperança
 */
public class MoinhoDB extends Object {
    // A nossa instância única
    private static MoinhoDB _singleton = null;
    // O mecanismo de segurança para evitar a criação de mais do que uma instância no ambiente multi-thread
    private final static Object _syncObject = new Object();
    
    private Connection connection=null;
    
    private static boolean databaseOk = false;
    private static ArrayList<ArrayList<Object>> statsCacheArray = null;
    private static ArrayList<HashMap<String, Object>> stats = null;
    private static ArrayList<String> statsColumnsNamesCache = null;
    
    /**
     * O nosso construtor é privado, pois instâncias desta classe apenas podem ser instânciadas internamente
     */
    private MoinhoDB(){
	super();
	try {
	    Class.forName("org.sqlite.JDBC");
	} catch (ClassNotFoundException e) {
	    //e.printStackTrace();
	    System.err.println("Não foi possível carregar a classe JDBC para o SQLite para gestão da persistência de dados");
	}
    }
    
    /**
     * @return _singleton com a instância única desta classe
     */
    public static MoinhoDB getInstance(){
	if (_singleton != null) {
	    return _singleton;
	}
	synchronized(_syncObject) {
	    if (_singleton == null) {
		_singleton = new MoinhoDB();
	    }
	}
	return _singleton;
    }
    
    /**
     * @return a database connection instance
     */
    public synchronized static Connection getConnection(){
	MoinhoDB instance = getInstance();
	try {
	    if(instance.connection==null || instance.connection.isClosed()){
		instance.connection = DriverManager.getConnection("jdbc:sqlite:"+MoinhoDbProperties.DB_FILE);
	    }
	} catch(SQLException e) {
	    System.err.println(e.getMessage());
	}
	
	return instance.connection;
    }
    
    /**
     * Fecha a ligação à base de dados
     */
    public synchronized static void closeConnection(){
	closeConnection(getInstance().connection);
    }
    
    /**
     * Fecha a ligação especificada à base de dados
     */
    public synchronized static void closeConnection(Connection connection){
	try { 
	    if(connection!=null && !connection.isClosed()){
		connection.close();
	    }
	} 
	catch( Exception e ) { 
	    System.err.println("Problema ao fechar a ligação à base de dados "+e.getMessage()); 
	}
    }
    
    /**
     * Verifica a base de dados, criando as tabelas auxiliares caso estas não existam
     */
    public synchronized static boolean checkDb(){
	if(databaseOk){
	    return true;
	}
	Connection connection = null;
	try {
	    // Criar a ligação à base de dados
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);

	    // Criar a tabela para os pesos
	    StringBuilder sb = new StringBuilder();
	    StringBuilder sbIndex = new StringBuilder();
	    sb.append("CREATE TABLE IF NOT EXISTS ").append(Peso.PESOS_TBL).append(" ( ").append(Peso.PESO_ID)
		.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		for (String peso : Peso.getCoeficientesPorOmissao().keySet()) {
		    sb.append(peso).append(" NUMERIC DEFAULT ( 0 ), ");
		    sbIndex.append(peso).append(", ");
		}
		sb.append(Peso.PROFUNDIDADE_PESQUISA).append(" NUMERIC DEFAULT ( 3 ), ")
		.append(Peso.ALGORITMO).append("  VARCHAR( 250 )  NOT NULL, ")
		.append("CONSTRAINT 'unique_peso' UNIQUE ( ").append(sbIndex.toString()).append(Peso.PROFUNDIDADE_PESQUISA).append(", ").append(Peso.ALGORITMO).append(" ) ON CONFLICT REPLACE ")
	    .append(");");
	    statement.executeUpdate(sb.toString());
	    
	    // Criar a tabela para o registo dos jogos efectuados
	    statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+Peso.JOGOS_TBL+" (  "+
		    Peso.JOGO_ID+"        INTEGER PRIMARY KEY AUTOINCREMENT, "+
    		    "jogador1Id    INTEGER NOT NULL "+
			"REFERENCES "+Peso.PESOS_TBL+" ( "+Peso.PESO_ID+" ) ON DELETE CASCADE "+
							"ON UPDATE CASCADE, "+
    		    "jogador2Id    INTEGER NOT NULL "+
			"REFERENCES "+Peso.PESOS_TBL+" ( "+Peso.PESO_ID+" ) ON DELETE CASCADE "+
							"ON UPDATE CASCADE, "+
    		    "vencedorId    INTEGER DEFAULT ( 0 ) REFERENCES "+Peso.PESOS_TBL+" ( "+Peso.PESO_ID+" ) ON DELETE CASCADE "+
							"ON UPDATE CASCADE, "+
    		    "vencidoId     INTEGER DEFAULT ( 0 ) REFERENCES "+Peso.PESOS_TBL+" ( "+Peso.PESO_ID+" ) ON DELETE CASCADE "+
							"ON UPDATE CASCADE, "+
    		    "numeroJogadas NUMERIC DEFAULT ( 0 ), "+
    		    "duracaoJogo   NUMERIC DEFAULT ( 0 ), "+
    		    "UNIQUE ( jogador1Id, jogador2Id, vencedorId, vencidoId, numeroJogadas, duracaoJogo )  ON CONFLICT REPLACE  "+
	    ");");
	    
	    // Trigger para inserção automática do campo vencido na tabela de jogos
	    statement.executeUpdate("CREATE TRIGGER IF NOT EXISTS insertVencido "+
		    "AFTER INSERT ON "+Peso.JOGOS_TBL+" "+
		    "WHEN (new.vencidoId IS NULL OR new.vencidoId=0 OR new.vencedorId = new.vencidoId) AND (new.vencedorId IS NOT NULL AND new.vencedorId<>0) "+
		    "BEGIN "+
			"UPDATE "+Peso.JOGOS_TBL+" "+
			    "SET vencidoId = (  "+
				"SELECT COALESCE( (  "+
				    "SELECT jogador2Id "+
				    "FROM "+Peso.JOGOS_TBL+" "+
				    "WHERE "+Peso.JOGO_ID+" = new."+Peso.JOGO_ID+"  "+
					" AND vencedorId = jogador1Id  "+
				") , (  "+
				    "SELECT jogador1Id "+
				    "FROM "+Peso.JOGOS_TBL+" "+
				    "WHERE "+Peso.JOGO_ID+" = new."+Peso.JOGO_ID+"  "+
					"AND vencedorId = jogador2Id  "+
				")  "+
			    ")) "+
			"WHERE "+Peso.JOGO_ID+" = new."+Peso.JOGO_ID+"; "+
		    "END;"+
	    ";");
	    
	    // Trigger para actualização automática do campo vencido na tabela de jogos
	    statement.executeUpdate("CREATE TRIGGER IF NOT EXISTS updateVencido "+
		    "AFTER UPDATE ON "+Peso.JOGOS_TBL+" "+
		    "WHEN (new.vencidoId IS NULL OR new.vencidoId=0 OR new.vencedorId = new.vencidoId) AND (new.vencedorId IS NOT NULL AND new.vencedorId<>0) "+
		    "BEGIN "+
			"UPDATE "+Peso.JOGOS_TBL+" "+
			    "SET vencidoId = (  "+
				"SELECT COALESCE( (  "+
				    "SELECT jogador2Id "+
				    "FROM "+Peso.JOGOS_TBL+" "+
				    "WHERE "+Peso.JOGO_ID+" = new."+Peso.JOGO_ID+" AND vencedorId = jogador1Id  "+
				") , (  "+
				"SELECT jogador1Id "+
				"FROM "+Peso.JOGOS_TBL+" "+
				"WHERE "+Peso.JOGO_ID+" = new."+Peso.JOGO_ID+" AND vencedorId = jogador2Id  "+
			    ")"+
			") ) "+
			"WHERE "+Peso.JOGO_ID+" = new."+Peso.JOGO_ID+"; "+
		    "END; "+
	    ";");
	    
	    // View para obtenção de dados estatísticos sobre os jogos registados
	    statement.executeUpdate("CREATE VIEW IF NOT EXISTS "+Peso.PESOS_VIEW+" AS "+
		"SELECT p.*, "+
		    "COALESCE(j1_1.total, 0) AS "+Peso.TOTAL_VITORIAS_COMO_J1+", "+
		    "COALESCE(j1_2.total, 0) AS "+Peso.TOTAL_VITORIAS_COMO_J2+", "+

		    "COALESCE(j2_1.total, 0) AS "+Peso.TOTAL_DERROTAS_COMO_J1+", "+
		    "COALESCE(j2_2.total, 0) AS "+Peso.TOTAL_DERROTAS_COMO_J2+", "+

		    "COALESCE(j3_1.total, 0) AS "+Peso.TOTAL_JOGOS_COMO_J1+", "+
		    "COALESCE(j3_2.total, 0) AS "+Peso.TOTAL_JOGOS_COMO_J2+", "+

		    "COALESCE(j4_1.total, 0) AS "+Peso.TOTAL_JOGADAS_VITORIA_COMO_J1+", "+
		    "COALESCE(j4_2.total, 0) AS "+Peso.TOTAL_JOGADAS_VITORIA_COMO_J2+", "+

		    "COALESCE(j5_1.total, 0) AS "+Peso.TOTAL_JOGADAS_DERROTA_COMO_J1+", "+
		    "COALESCE(j5_2.total, 0) AS "+Peso.TOTAL_JOGADAS_DERROTA_COMO_J2+", "+

		    "COALESCE(j6_1.total, 0) AS "+Peso.TOTAL_DURACAO_VITORIA_COMO_J1+", "+
		    "COALESCE(j6_2.total, 0) AS "+Peso.TOTAL_DURACAO_VITORIA_COMO_J2+", "+

		    "COALESCE(j7_1.total, 0) AS "+Peso.TOTAL_DURACAO_DERROTA_COMO_J1+", "+
		    "COALESCE(j7_2.total, 0) AS "+Peso.TOTAL_DURACAO_DERROTA_COMO_J2+", "+

		    "(COALESCE(j4_1.total, 0)+COALESCE(j4_2.total, 0)) AS "+Peso.TOTAL_JOGADAS_VITORIA+", "+
		    "(COALESCE(j5_1.total, 0)+COALESCE(j5_2.total, 0)) AS "+Peso.TOTAL_JOGADAS_DERROTA+", "+
		    "(COALESCE(j6_1.total, 0)+COALESCE(j6_2.total, 0)) AS "+Peso.TOTAL_DURACAO_VITORIA+", "+
		    "(COALESCE(j7_1.total, 0)+COALESCE(j7_2.total, 0)) AS "+Peso.TOTAL_DURACAO_DERROTA+", "+
		    "(COALESCE(j1_1.total, 0)+COALESCE(j1_2.total, 0)) AS "+Peso.TOTAL_VITORIAS+", "+
		    "(COALESCE(j2_1.total, 0)+COALESCE(j2_2.total, 0)) AS "+Peso.TOTAL_DERROTAS+", "+
		    "(COALESCE(j3_1.total, 0)+COALESCE(j3_2.total, 0)) AS "+Peso.TOTAL_JOGOS+", "+
		    "ROUND(COALESCE(CAST((COALESCE(j1_1.total, 0)+COALESCE(j1_2.total, 0)) AS REAL)/CAST((COALESCE(j3_1.total, 0)+COALESCE(j3_2.total, 0)) AS REAL), 0)*100) AS "+Peso.PERCENTAGEM_VITORIAS+", "+
		    "ROUND(COALESCE(CAST((COALESCE(j2_1.total, 0)+COALESCE(j2_2.total, 0)) AS REAL)/CAST((COALESCE(j3_1.total, 0)+COALESCE(j3_2.total, 0)) AS REAL), 0)*100) AS "+Peso.PERCENTAGEM_DERROTAS+" "+
		    
		"FROM "+Peso.PESOS_TBL+" AS p "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, vencedorId, COUNT( * ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador1Id = vencedorId "+
			"GROUP BY vencedorId, jogador1Id  "+
		    ") AS j1_1 ON j1_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, vencedorId, COUNT( * ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador2Id = vencedorId "+
			"GROUP BY vencedorId, jogador2Id  "+
		    ") AS j1_2 ON j1_2.jogador2Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, vencidoId, COUNT( * ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador1Id = vencidoId "+
			"GROUP BY vencidoId, jogador1Id  "+
		    ") AS j2_1 ON j2_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, vencidoId, COUNT( * ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador2Id = vencidoId "+
			"GROUP BY vencidoId, jogador2Id  "+
		    ") AS j2_2 ON j2_2.jogador2Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, COUNT( * ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"GROUP BY jogador1Id  "+
		    ") AS j3_1 ON j3_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, COUNT( * ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"GROUP BY jogador2Id  "+
		    ") AS j3_2 ON j3_2.jogador2Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, SUM( numeroJogadas ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador1Id = vencedorId "+
			"GROUP BY jogador1Id  "+
		    ") AS j4_1 ON j4_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, SUM( numeroJogadas ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador2Id = vencedorId "+
			"GROUP BY jogador2Id  "+
		    ") AS j4_2  ON j4_2.jogador2Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, SUM( numeroJogadas ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador1Id = vencidoId "+
			"GROUP BY jogador1Id  "+
		    ") AS j5_1 ON j5_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, SUM( numeroJogadas ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador2Id = vencidoId "+
			"GROUP BY jogador2Id  "+
		    ") AS j5_2 ON j5_2.jogador2Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, SUM( duracaoJogo ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador1Id = vencedorId "+
			"GROUP BY jogador1Id  "+
		    ") AS j6_1 ON j6_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, SUM( duracaoJogo ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador2Id = vencedorId "+
			"GROUP BY jogador2Id  "+
		    ") AS j6_2 ON j6_2.jogador2Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador1Id, SUM( duracaoJogo ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador1Id = vencidoId "+
			"GROUP BY jogador1Id  "+
		    ") AS j7_1 ON j7_1.jogador1Id = p."+Peso.PESO_ID+" "+
		    "LEFT JOIN  (  "+
			"SELECT jogador2Id, SUM( duracaoJogo ) AS total "+
			"FROM "+Peso.JOGOS_TBL+" "+
			"WHERE jogador2Id = vencidoId "+
			"GROUP BY jogador2Id  "+
		    ")  AS j7_2 ON j7_2.jogador2Id = p."+Peso.PESO_ID+" "+
		"GROUP BY p."+Peso.PESO_ID+" "+
		//"ORDER BY "+Peso.TOTAL_JOGOS+" ASC, "+Peso.PERCENTAGEM_VITORIAS+" DESC, "+Peso.PERCENTAGEM_DERROTAS+" ASC; "+
	    ";");
	    databaseOk = true;
	    
	    fireDbUpdated();
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return databaseOk;
    }
    
    /**
     * Insere um novo peso na base de dados
     * @param peso com o peso a inserir
     * @return int maior do que zero com o id do novo peso, false caso contrário
     */
    private synchronized static int _insertPeso(Peso peso, boolean fireDbUpdated){
	int result = -1;
	// Verificar o estado da base de dados
	if(!checkDb()){
	    return result;
	}
	// Se o peso já existe, então ignoramos a inserção
	result = getPeso(peso);
	if(result>=0){
	    return result;
	}
	Connection connection = null;
	try {
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    StringBuilder sb = new StringBuilder();
	    StringBuilder sbValues = new StringBuilder();
	    sb.append("INSERT INTO [").append(Peso.PESOS_TBL).append("] ( ");
		for (String coeficiente : Peso.getCoeficientesPorOmissao().keySet()) {
		    sb.append(" [").append(coeficiente).append("], ");
		    sbValues.append(peso.getCoeficiente(coeficiente)).append(", ");
		}
		sb.append("[").append(Peso.PROFUNDIDADE_PESQUISA).append("], [").append(Peso.ALGORITMO).append("]) VALUES ( ")
		.append(sbValues.toString()).append(peso.getProfundidade()).append(", '").append(peso.getAlgoritmo()).append("' );");
		
	    statement.executeUpdate(sb.toString());
	    
	    ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS id;");
	    while(rs.next()) {
		result = rs.getInt("id");
	    }
	    if(fireDbUpdated){
		fireDbUpdated();
	    }
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return result;
    }
    
    /**
     * Insere um novo peso na base de dados
     * @param peso com o peso a inserir
     * @return int maior do que zero com o id do novo peso, false caso contrário
     */
    public static int insertPeso(Peso peso){
	return _insertPeso(peso, true);
    }
    
    /**
     * Insere novos pesos na base de dados
     * @param pesos com a lista de pesos a inserir
     */
    public synchronized static void insertPesos(ArrayList<Peso> pesos){
	// Verificar o estado da base de dados
	if(!checkDb()){
	    return;
	}
	// Se o peso já existe, então ignoramos a inserção
	Connection connection = null;
	try {
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    StringBuilder sbTransaction = new StringBuilder();
	    sbTransaction.append("BEGIN TRANSACTION; ");
	    for(Peso peso : pesos){
		StringBuilder sbValues = new StringBuilder();
		sbTransaction.append("INSERT INTO [").append(Peso.PESOS_TBL).append("] ( ");
		    for (String coeficiente : Peso.getCoeficientesPorOmissao().keySet()) {
			sbTransaction.append(" [").append(coeficiente).append("], ");
			sbValues.append(peso.getCoeficiente(coeficiente)).append(", ");
		    }
		    sbTransaction.append("[").append(Peso.PROFUNDIDADE_PESQUISA).append("], [").append(Peso.ALGORITMO).append("]) VALUES ( ")
		    .append(sbValues.toString()).append(peso.getProfundidade()).append(", '").append(peso.getAlgoritmo()).append("' ); ");
	    }
	    sbTransaction.append("COMMIT; ");
	    statement.executeUpdate(sbTransaction.toString());
	    
	    fireDbUpdated();
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
    }
    
    /**
     * Obtem um peso com base num identificador existente na base de dados
     * @param id com o identificador do peso
     * @return 
     */
    public synchronized static Peso getPeso(int id){
	Peso peso = null;
	if(!checkDb()){
	    return peso;
	}
	Connection connection = null;
	try {
	    Peso pesoT = new Peso();
	    // cria uma ligação à base de dados
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    ResultSet rs = statement.executeQuery("SELECT * FROM ["+Peso.PESOS_TBL+"] WHERE "+Peso.PESO_ID+" = "+id+"; ");
	    while(rs.next()) {
		for (String coeficiente : Peso.getCoeficientesPorOmissao().keySet()) {
		    pesoT.setCoeficiente(coeficiente, rs.getInt(coeficiente));
		}
		pesoT.setProfundidade(rs.getInt(Peso.PROFUNDIDADE_PESQUISA));
		pesoT.setAlgoritmo(rs.getString(Peso.ALGORITMO));
	    }
	    peso = pesoT;
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return peso;
    }
    
    /**
     * Obtem um peso existente na base de dados com base nos valores dos coeficientes, profundidade e algoritmo
     * @param peso com o peso a obter
     * @return int maior do que zero com o id do novo peso, false caso contrário
     */
    public synchronized static int getPeso(Peso peso){
	int id = -1;
	if(!checkDb()){
	    return id;
	}
	Connection connection = null;
	try {
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("SELECT ").append(Peso.PESO_ID).append(" FROM [").append(Peso.PESOS_TBL).append("] WHERE ");
		for (String coeficiente : Peso.getCoeficientesPorOmissao().keySet()) {
		    sb.append(coeficiente).append("=").append(peso.getCoeficiente(coeficiente)).append(" AND ");
		}
		sb.append(Peso.PROFUNDIDADE_PESQUISA).append("=").append(peso.getProfundidade()).append(" AND ").append(Peso.ALGORITMO).append("='").append(peso.getAlgoritmo()).append("'; ");
	    ResultSet rs = statement.executeQuery(sb.toString());
	    while(rs.next()) {
		id = rs.getInt(Peso.PESO_ID);
	    }
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return id;
    }
    
    /**
     * Verifica se um dado peso já existe na base de dados
     * @param peso com o peso a verificar
     * @return true se o peso existe, false caso contrário ou em caso de erro
     */
    public static boolean pesoExists(Peso peso){
	if(!checkDb()){
	    return false;
	}
	return (getPeso(peso)>=0);
    }
    
    /**
     * Verifica se um dado peso já existe na base de dados
     * @param id com o peso a verificar
     * @return true se o peso existe, false caso contrário ou em caso de erro
     */
    public static boolean pesoExists(int id){
	if(!checkDb()){
	    return false;
	}
	return (getPeso(id)!=null);
    }
    
    /**
     * Insere um novo jogo na base de dados
     * @param jogador1
     * @param jogador2
     * @param vencedor
     * @param vencido
     * @param numeroJogadas
     * @param duracaoJogo
     * @return int maior do que zero com o id do novo jogo, false caso contrário
     */
    public synchronized static int insertJogo(int jogador1, int jogador2, int vencedor, int vencido, long numeroJogadas, long duracaoJogo){
	int result = -1;
	// Verificar o estado da base de dados
	if(!checkDb()){
	    return result;
	}
	// Se o jogo já existe, então ignoramos a inserção
	result = getJogo(jogador1, jogador2, vencedor, vencido);
	if(result>=0){
	    return result;
	}
	
	if(!pesoExists(jogador1) || !pesoExists(jogador2) || (vencedor!=0 && vencedor!=jogador1 && vencedor!=jogador2 ) || (vencedor!=0 && vencido!=jogador1 && vencido!=jogador2 )){
	    System.err.println("Existem jogadores inexistentes");
	    return result;
	}
	
	Connection connection = null;
	try {
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("INSERT INTO [").append(Peso.JOGOS_TBL).append("] ( ")
		.append("[jogador1Id], [jogador2Id], [vencedorId], [vencidoId], [numeroJogadas], [duracaoJogo]) VALUES ( ")
		.append(jogador1).append(", ").append(jogador2).append(", ").append(vencedor).append(", ").append(vencido).append(", ").append(numeroJogadas).append(", ").append(duracaoJogo)
		.append("); ");
		
	    statement.executeUpdate(sb.toString());
	    
	    ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS id;");
	    while(rs.next()) {
		result = rs.getInt("id");
	    }
	    fireDbUpdated();
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return result;
    }
    
    /**
     * Permite obter um identificador do jogo, caso este já exista
     * 
     * @param jogador1
     * @param jogador2
     * @param vencedor
     * @param vencido
     * @return  int maior do que zero com o id do novo jogo, false caso contrário
     */
    public synchronized static int getJogo(int jogador1, int jogador2, int vencedor, int vencido){
	int id = -1;
	if(!checkDb()){
	    return id;
	}
	Connection connection = null;
	try {
	    // cria uma ligação à base de dados
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("SELECT ").append(Peso.JOGO_ID).append(" FROM [").append(Peso.JOGOS_TBL).append("] WHERE jogador1Id=").append(jogador1)
		.append(" AND jogador1Id=").append(jogador1)
		.append(" AND jogador2Id=").append(jogador2)
		.append(" AND vencedorId=").append(vencedor)
		.append(" AND vencidoId=").append(vencido);
	    ResultSet rs = statement.executeQuery(sb.toString());
	    while(rs.next()) {
		id = rs.getInt(Peso.JOGO_ID);
	    }
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return id;
    }
    
    /**
     * Obtém o nome das colunas para a tabela de estatísticas
     * @return 
     */
    public synchronized static ArrayList<String> getEstatisticasColumnNames(){
	if(statsColumnsNamesCache==null){
	    getEstatisticas(true);
	}
	return statsColumnsNamesCache;
    }
    
    /**
     * Obtém as estatísticas da base de dados
     * @return 
     */
    public static ArrayList<ArrayList<Object>> getEstatisticas(){
	return getEstatisticas(false);
    }
    
    /**
     * Obtém as estatísticas da base de dados
     * @return 
     */
    public synchronized static ArrayList<ArrayList<Object>> getEstatisticas(boolean refreshCache){
	ArrayList<HashMap<String, String>> orderBy = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> field = new HashMap<String, String>();
	field.put(Peso.TOTAL_VITORIAS, "DESC");
	orderBy.add(field);
	
	field = new HashMap<String, String>();
	field.put(Peso.TOTAL_DERROTAS, "ASC");
	orderBy.add(field);
	
	return getEstatisticas(refreshCache, orderBy);
    }
    
    /**
     * Obtém as estatísticas da base de dados
     * @return 
     */
    public synchronized static ArrayList<ArrayList<Object>> getEstatisticas(boolean refreshCache, ArrayList<HashMap<String, String>> orderBy){
	if(!refreshCache && statsCacheArray!=null && statsCacheArray.size()>0){
	    return statsCacheArray;
	}
	statsCacheArray = null;
	if(!checkDb()){
	    return statsCacheArray;
	}
//	statsCacheArray = new ArrayList<ArrayList<Object>>();
//	for(HashMap<String, Object> row : getEstatisticasList(refreshCache)){
//	    // Implementado na hashmap para que as colunas apareçam ordenadas (bad hashmap, bad!)
////	    if(statsColumnsNamesCache==null){
////		statsColumnsNamesCache = new ArrayList<String>(row.keySet());
////	    }
//	    statsCacheArray.add(new ArrayList<Object>(row.values()));
//	}
	// Sim, não é bonito, mas a alternativa é as colunas aparecerem com uma ordem estranha na JTable
	Connection connection = null;
	try {
	    // cria uma ligação à base de dados
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    //ResultSet rs = statement.executeQuery("SELECT * FROM ["+Peso.PESOS_VIEW+"]; ");
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("SELECT ");
	    Iterator<String> it = Peso.getColumns().iterator();
	    while(it.hasNext()){
		String key = it.next();
		sb.append(key);
		if(it.hasNext()){
		    sb.append(", ");
		}
	    }
	    sb.append(" FROM [").append(Peso.PESOS_VIEW).append("] ").append(orderBy(orderBy)).append("; ");
		
	    ResultSet rs = statement.executeQuery(sb.toString());
	    
	    ResultSetMetaData rsmd = rs.getMetaData();
	    int totalColumns = rsmd.getColumnCount();
	    
	    // Preencher os nomes das colunas
	    if(statsColumnsNamesCache==null){
		statsColumnsNamesCache = new ArrayList<String>(totalColumns);
		for(int a=1; a<=totalColumns; a++){
		    statsColumnsNamesCache.add(rsmd.getColumnLabel(a));
		}
	    }
	    statsCacheArray = new ArrayList<ArrayList<Object>>();
	    while(rs.next()) {
		ArrayList<Object> row = new ArrayList<Object>(totalColumns);
		for(int a=1; a<=totalColumns; a++){
		    row.add(rs.getObject(a));
		}
		statsCacheArray.add(row);
	    }
	    
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return statsCacheArray;
    }
    
    /**
     * Obtém as estatísticas da base de dados para uma hashmap
     * @return 
     */
    public static ArrayList<HashMap<String, Object>> getEstatisticasList(){
	return getEstatisticasList(false);
    }
    
    /**
     * Verifica se os campos de orderBy existem e são válidos e devolve a expressão sql respectiva
     * @param orderBy
     * @return 
     */
    private static String orderBy(ArrayList<HashMap<String, String>> orderBy){
	// Verificar se os campos de orderBy existem e são válidos
	HashMap<String, String> dbFields = Peso.getFuncaoCoeficienteDescs();
	StringBuilder sb = new StringBuilder();
	if(!orderBy.isEmpty()){
	    sb.append(" ORDER BY");

	    Iterator<HashMap<String, String>> it = orderBy.iterator();
	    while(it.hasNext()){
		HashMap<String, String> orderByField = it.next();
		for(String orderByFieldName : orderByField.keySet()){
		    if(!dbFields.containsKey(orderByFieldName) || (!orderByField.get(orderByFieldName).toUpperCase().equals("ASC") && !orderByField.get(orderByFieldName).toUpperCase().equals("DESC"))){
			return "";
		    }
		    sb.append(" ").append(orderByFieldName).append(" ").append(orderByField.get(orderByFieldName));
		    if(it.hasNext()){
			sb.append(", ");
		    }
		}
	    }
	}
	return sb.toString();
    }
    
    /**
     * Obtém as estatísticas da base de dados para uma hashmap
     * @return 
     */
    public synchronized static ArrayList<HashMap<String, Object>> getEstatisticasList(boolean refreshCache){
	ArrayList<HashMap<String, String>> orderBy = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> field = new HashMap<String, String>();
	field.put(Peso.TOTAL_VITORIAS, "DESC");
	orderBy.add(field);
	
	field = new HashMap<String, String>();
	field.put(Peso.TOTAL_DERROTAS, "ASC");
	orderBy.add(field);
	
	return getEstatisticasList(refreshCache, orderBy);
    }
    
    /**
     * Obtém as estatísticas da base de dados para uma hashmap
     * @return 
     */
    public synchronized static ArrayList<HashMap<String, Object>> getEstatisticasList(boolean refreshCache, ArrayList<HashMap<String, String>> orderBy){
	if(!refreshCache && stats!=null && stats.size()>0){
	    return stats;
	}
	stats = null;
	if(!checkDb()){
	    return stats;
	}
	Connection connection = null;
	try {
	    ArrayList<HashMap<String, Object>> statsTmp = new ArrayList<HashMap<String, Object>>();
	    // cria uma ligação à base de dados
	    connection = getConnection();
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(MoinhoDbProperties.TIMEOUT);
	    
	    //ResultSet rs = statement.executeQuery("SELECT * FROM [pesosView]; ");
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("SELECT ");
	    Iterator<String> it = Peso.getColumns().iterator();
	    while(it.hasNext()){
		String key = it.next();
		sb.append(key);
		if(it.hasNext()){
		    sb.append(", ");
		}
	    }
	    sb.append(" FROM [").append(Peso.PESOS_VIEW).append("] ").append(orderBy(orderBy)).append("; ");
		
	    ResultSet rs = statement.executeQuery(sb.toString());
	    
	    ResultSetMetaData rsmd = rs.getMetaData();
	    int totalColumns = rsmd.getColumnCount();
	    if(statsColumnsNamesCache==null){
		statsColumnsNamesCache = new ArrayList<String>(totalColumns);
	    }
	    
	    HashMap<String, Object> stat;
	    
	    while(rs.next()) {
		stat = new HashMap<String, Object>();
		for(int a=1; a<=totalColumns; a++){
		    if(statsColumnsNamesCache.size()<totalColumns){
			statsColumnsNamesCache.add(rsmd.getColumnLabel(a));
		    }
		    stat.put(rsmd.getColumnLabel(a), rs.getObject(a));
		}
		statsTmp.add(stat);
	    }
	    stats = statsTmp;
	} catch(SQLException e) {
	    //e.printStackTrace();
	    System.err.println(e.getMessage());
	} finally {
	    closeConnection(connection);
	}
	return stats;
    }
    
    /**
     * Instâncias desta classe não podem ser replicadas
     * 
     * @return null
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException();
    }
    
    /**
     * Quando esta instância for destruída, fechar eventuais ligações à base de dados que ainda se encontrem abertas
     * @throws Throwable 
     */
    @Override
    public void finalize() throws Throwable{ 
	closeConnection();
	super.finalize();
    }
    
    

    //Listeners
    private static transient ArrayList<MoinhoDbListener> listeners = new ArrayList<MoinhoDbListener>();

    // Remove um ouvinte
    public static synchronized void removeListener(MoinhoDbListener l) {
        if (listeners != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    
    /**
     * Adiciona um ouvinte
     */
    public static synchronized void addListener(MoinhoDbListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Notifica os ouvintes de alterações na base de dados
     */
    public static void fireDbUpdated(MoinhoDbEvent e) {
        for (MoinhoDbListener listener : listeners) {
            listener.dbUpdated(e);
        }
    }
    
    /**
     * Notifica os ouvintes de alterações na base de dados
     */
    public static void fireDbUpdated() {
	fireDbUpdated(new MoinhoDbEvent(getConnection(), getEstatisticas(true)));
    }
    
}