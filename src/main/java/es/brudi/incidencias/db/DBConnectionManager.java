package es.brudi.incidencias.db;

import java.sql.Connection;
import java.sql.SQLException;

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

	/**
	 * Este método estático devolve a única conexión que existe coa base de datos.
	 * @return - Conector coa base de datos.
	 */
	public static Connection getConnection(){
		try {
			return XestorConexions.getConexion();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}