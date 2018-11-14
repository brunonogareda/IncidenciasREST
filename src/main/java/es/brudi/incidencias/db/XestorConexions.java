package es.brudi.incidencias.db;

import java.sql.Connection;
import java.sql.SQLException;

// pool
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Singleton abstracto que proporciona os mecanismos precisos para manexar as conexiones a base de datos
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Novembro - 2018
 */
public abstract class XestorConexions {

    private static final String   	NOME_CLASE_DRIVER 				= "com.mysql.jdbc.Driver";
    private static final String		INITIAL_CONTEXT					= "java:/comp/env";
    private static final String		JNDI							= "jdbc/incidencias";

    private static DataSource   	ds;
	private static Logger 			logger 							= Logger.getLogger(XestorConexions.class);

    private XestorConexions() {
		throw new IllegalStateException("Utility class");
    }

	public static void iniciarPool() {

		try {
			Class.forName(NOME_CLASE_DRIVER);
			String initialContext = INITIAL_CONTEXT;
			String jndi = JNDI;
			Context initContext = new InitialContext();
			logger.debug("InitialContext: " + initialContext);
			logger.debug("Jndi: " + jndi);
			Context envContext = (Context) initContext.lookup(initialContext);
			XestorConexions.ds = (DataSource) envContext.lookup(jndi);
			if (XestorConexions.ds == null)
				logger.error("Null DataSource");

			logger.debug("Iniciada a pool de conexións.");
		} catch (ClassNotFoundException e1) {
			logger.error(e1);
		} catch (Exception e) {
			logger.error("Excepcion ó crear o DataSource. " + e.toString());
			logger.error("Exception - ", e);
		}

	}
    
    /**
     * Metodo para obtener la conexion con la base de datos.
     *
     * @return Un obxeto ConexionPool que contien a conexion coa base de datos.
     */
    public static Connection getConexion() throws SQLException {
        return XestorConexions.ds.getConnection();
    }

}
