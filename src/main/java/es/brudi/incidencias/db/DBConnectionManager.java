package es.brudi.incidencias.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * 
 * Clase que xestiona as conexións coa base de datos.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class DBConnectionManager {

	private static Connection connection;
	
	//Datos por defecto da conexión coa base de datos
	private static final String DEFAULT_USERNAME_DB = "incidencias_web";
	private static final String DEFAULT_PASSWORD_DB = "incidencias_web";
	private static final String DEFAULT_URL_DB = "jdbc:mysql://127.0.0.1:3306/";
	private static final String DEFAULT_NAME_DB = "brudi";

	private Logger logger = Logger.getLogger(DBConnectionManager.class);
	
	public DBConnectionManager(String dbURL, String dbName, String user, String pwd) {
		conectar(dbURL, dbName, user, pwd);
	}
	
	public DBConnectionManager() {
		conectar(DEFAULT_URL_DB, DEFAULT_NAME_DB, DEFAULT_USERNAME_DB, DEFAULT_PASSWORD_DB);
	}
	
	/**
	 * Este método crea unha nova conexión coa base de datos.
	 * @param dbURL - URL da base de datos para establecer unha conexión mediante o driver jdbc
	 * @param dbName - Nome da base de datos
	 * @param user - Usuario da base de datos
	 * @param pwd - Constrasinal do usuario da base de datos
	 */
	public void conectar(String dbURL, String dbName, String user, String pwd) {
		try
		 {
		   Class.forName("com.mysql.jdbc.Driver").newInstance();
		   connection = DriverManager.getConnection(dbURL+dbName+"?noAccessToProcedureBodies=true", user, pwd);
		   
		 }
		catch(SQLException se)
		 {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e)
		 {
			logger.error("Exception: ", e);
		 }
		
		if (connection == null)
		 {
			logger.error("Error de conexión coa base de datos.");
		 }
		else {
			logger.info("Conexión establecida coa base de datos.");
		}
	}
	
	/**
	 * Este método estático devolve a única conexión que existe coa base de datos.
	 * @return - Conector coa base de datos.
	 */
	public static Connection getConnection(){
		return connection;
	}
}