package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.presupostos.Presuposto;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Presupostos.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class PresupostoDAO {

	private final static String TABLENAME = "Presupostos";
	   
	private static Logger logger = Logger.getLogger(PresupostoDAO.class);
	
	/**
	 * Inserta un novo presuposto na base de datos.
	 * @param id
	 * @param ruta_ficheiro
	 * @param tipo_ficheiro
	 * @param aceptado
	 * @param comentarios
	 * @return
	 */
	public static boolean crear(String id, String ruta_ficheiro, String tipo_ficheiro, String comentarios, boolean aceptado) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (id, Ruta_ficheiro, Tipo_ficheiro, aceptado, Comentarios) VALUES (?, ?, ?, ?, ?);";
		
		PreparedStatement presuposto;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			presuposto = conn.prepareStatement(query);
			
			int i = 1;
			presuposto.setString(i++, id);
			presuposto.setString(i++, ruta_ficheiro);
			presuposto.setString(i++, tipo_ficheiro);
			presuposto.setBoolean(i++, aceptado);
			presuposto.setString(i++, comentarios);
						
			int res = presuposto.executeUpdate();
					
			presuposto.close();
			if(res==1) {
				return true;
			}		
		 }
		catch(SQLException se)
		 {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e)
		 {
			logger.error("Exception: "+e);
		 }
		
		return false;
	}
	
	/**
	 * Obten un presuposto mediante o id.
	 * @param id
	 * @return
	 */
	public static Presuposto getById(String id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		PreparedStatement presuposto;
		Presuposto ret = null;
		ResultSet res;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			presuposto = conn.prepareStatement(query);
			presuposto.setString(1, id);
						
			res = presuposto.executeQuery();
			
			if(res.next()) {
				ret = new Presuposto(res);
			}
			
			res.close();
			presuposto.close();
		 }
		catch(SQLException se)
		 {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e)
		 {
			logger.error("Exception: "+e);
		 }
		
		return ret;
	}

	/**
	 * Modifica os parámetros do presuposto na base de datos
	 * @param id
	 * @param ruta_ficheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipo_ficheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param comentarios - Comentarios do presuposto. NULL non o modifica.
	 * @param aceptado - Presuposto aceptado. NULL non o modifica
	 * @return
	 */
	public static boolean modificar(String id, String ruta_ficheiro, String tipo_ficheiro, String comentarios, String aceptado) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (ruta_ficheiro != null && !ruta_ficheiro.equals("")) ? " Ruta_ficheiro = ?," : "";
		query += (tipo_ficheiro != null && !tipo_ficheiro.equals("")) ? " Tipo_ficheiro = ?," : "";
		query += (comentarios != null && !comentarios.equals("")) ? " Comentarios = ?," : "";
		query += (aceptado != null && (aceptado.equals("true") || aceptado.equals("false"))) ? " Aceptado = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		PreparedStatement presuposto;
		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			presuposto = conn.prepareStatement(query);	
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(presuposto != null && !ruta_ficheiro.equals("")) presuposto.setString(i++, ruta_ficheiro);
			if(presuposto != null && !tipo_ficheiro.equals("")) presuposto.setString(i++, tipo_ficheiro);
			if(comentarios != null && !comentarios.equals("")) presuposto.setString(i++, comentarios);
			if(aceptado != null && aceptado.equals("true")) presuposto.setBoolean(i++, true);
			if(aceptado != null && aceptado.equals("false")) presuposto.setBoolean(i++, false);
			presuposto.setString(i++, id);
	
			int res = presuposto.executeUpdate();
			
			presuposto.close();
			
			if(res == 1) {
				return true;
			}
			return false;
					
		 }
		catch(SQLException se)
		 {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e)
		 {
			logger.error("Exception: "+e);
		 }
		
		return false;
	}
}
