package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.imaxes.Imaxe;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Imaxes.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class ImaxeDAO {

	private final static String TABLENAME = "Imaxes_incidencias";
	   
	private static Logger logger = Logger.getLogger(ImaxeDAO.class);
	
	public static int getNextId() {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT AUTO_INCREMENT FROM  INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME   = '"+TABLENAME+"';";
		
		PreparedStatement imaxe;
		ResultSet res;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			imaxe = conn.prepareStatement(query);
			res = imaxe.executeQuery();
			
			res.next();
	        int ret = res.getInt("AUTO_INCREMENT");
	        res.close();
	        imaxe.close();
	        return ret;
			
		 }
		catch(SQLException se)
		 {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e)
		 {
			logger.error("Exception in DAO: ", e);
		 }
		
		return -1;
	}
	
	/**
	 * Inserta unha nova i na base de datos.
	 * @param id_incidencia
	 * @param ruta_ficheiro
	 * @param tipo_ficheiro
	 * @param nome
	 * @param comentarios
	 * @param antes_despois
	 * @return
	 */
	public static int crear(int id_incidencia, String ruta_ficheiro, String tipo_ficheiro, String nome, String comentarios, boolean antes_despois) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (Id_incidencia, Nome, Ruta_ficheiro, Tipo_ficheiro, Antes_despois, Comentarios) VALUES (?, ?, ?, ?, ?, ?);";
		
		PreparedStatement imaxe;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			imaxe = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			imaxe.setInt(i++, id_incidencia);
			imaxe.setString(i++, nome);
			imaxe.setString(i++, ruta_ficheiro);
			imaxe.setString(i++, tipo_ficheiro);
			imaxe.setBoolean(i++, antes_despois);
			imaxe.setString(i++, comentarios);
						
			int res = imaxe.executeUpdate();
					
			
			if(res==1) {
				ResultSet id = imaxe.getGeneratedKeys();
				if(id.next()) {
					int ret = id.getInt(1);
					id.close();
					imaxe.close();
					return ret;
				}
				imaxe.close();
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
			logger.error("Exception in DAO: ", e);
		 }
		
		return -1;
	}
	
	/**
	 * Obten unha imaxe mediante o id.
	 * @param id
	 * @return
	 */
	public static Imaxe getById(int id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		PreparedStatement imaxe;
		Imaxe ret = null;
		ResultSet res;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			imaxe = conn.prepareStatement(query);
			imaxe.setInt(1, id);
						
			res = imaxe.executeQuery();
			
			if(res.next()) {
				ret = new Imaxe(res);
			}
			
			res.close();
			imaxe.close();
		 }
		catch(SQLException se)
		 {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e)
		 {
			logger.error("Exception in DAO: ", e);
		 }
		
		return ret;
	}

	/**
	 * Modifica os parámetros da imaxe na base de datos
	 * @param id
	 * @param ruta_ficheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipo_ficheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome da imaxe. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @param antes_despois - Antes/Despois da incidencia. NULL non o modifica
	 * @return
	 */
	public static boolean modificar(int id, String ruta_ficheiro, String tipo_ficheiro, String nome, String comentarios, String antes_despois) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (ruta_ficheiro != null && !ruta_ficheiro.equals("")) ? " Ruta_ficheiro = ?," : "";
		query += (tipo_ficheiro != null && !tipo_ficheiro.equals("")) ? " Tipo_ficheiro = ?," : "";
		query += (nome != null && !nome.equals("")) ? " Nome = ?," : "";
		query += (comentarios != null && !comentarios.equals("")) ? " Comentarios = ?," : "";
		query += ("antes".equalsIgnoreCase(antes_despois) || "despois".equalsIgnoreCase(antes_despois)
				|| "despues".equalsIgnoreCase(antes_despois) || "después".equalsIgnoreCase(antes_despois)) ? " Antes_despois = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		PreparedStatement imaxe;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			imaxe = conn.prepareStatement(query);	
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(ruta_ficheiro != null && !ruta_ficheiro.equals("")) imaxe.setString(i++, ruta_ficheiro);
			if(tipo_ficheiro != null && !tipo_ficheiro.equals("")) imaxe.setString(i++, tipo_ficheiro);
			if(nome != null && !nome.equals("")) imaxe.setString(i++, nome);
			if(comentarios != null && !comentarios.equals("")) imaxe.setString(i++, comentarios);
			if("antes".equalsIgnoreCase(antes_despois)) imaxe.setBoolean(i++, false);
			if("despois".equalsIgnoreCase(antes_despois) || "despues".equalsIgnoreCase(antes_despois) || "después".equalsIgnoreCase(antes_despois)) imaxe.setBoolean(i++, true);
			imaxe.setInt(i++, id);
	
			int res = imaxe.executeUpdate();
			
			imaxe.close();
			
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
			logger.error("Exception in DAO: ", e);
		 }
		
		return false;
	}
}
