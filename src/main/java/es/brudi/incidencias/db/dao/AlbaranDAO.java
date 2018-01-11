package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import es.brudi.incidencias.albarans.Albaran;
import es.brudi.incidencias.db.DBConnectionManager;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Imaxes_albarans.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class AlbaranDAO {

	private final static String TABLENAME = "Imaxes_albarans";
	   
	private static Logger logger = Logger.getLogger(AlbaranDAO.class);
	
	public static int getNextId() {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT AUTO_INCREMENT FROM  INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME   = '"+TABLENAME+"';";
		
		PreparedStatement albaran;
		ResultSet res;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			albaran = conn.prepareStatement(query);
			res = albaran.executeQuery();
			
			res.next();
	        int ret = res.getInt("AUTO_INCREMENT");
	        res.close();
	        albaran.close();
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
	 * Inserta un novo albaran na base de datos.
	 * @param id_incidencia
	 * @param ruta_ficheiro
	 * @param tipo_ficheiro
	 * @param nome
	 * @param proveedores
	 * @param num_albaran
	 * @param comentarios
	 * @param antes_despois
	 * @return
	 */
	public static int crear(int id_incidencia, String ruta_ficheiro, String tipo_ficheiro, String nome, String proveedor, String num_albaran, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (Id_incidencia, Nome, Proveedor, Num_albaran, Ruta_ficheiro, Tipo_ficheiro, Comentarios) VALUES (?, ?, ?, ?, ?, ?, ?);";
		
		PreparedStatement albaran;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			albaran = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			albaran.setInt(i++, id_incidencia);
			albaran.setString(i++, nome);
			albaran.setString(i++, proveedor);
			albaran.setString(i++, num_albaran);
			albaran.setString(i++, ruta_ficheiro);
			albaran.setString(i++, tipo_ficheiro);
			albaran.setString(i++, comentarios);
						
			int res = albaran.executeUpdate();
					
			
			if(res==1) {
				ResultSet id = albaran.getGeneratedKeys();
				if(id.next()) {
					int ret = id.getInt(1);
					id.close();
					albaran.close();
					return ret;
				}
				albaran.close();
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
	 * Obten un albaran mediante o id.
	 * @param id
	 * @return
	 */
	public static Albaran getById(int id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		PreparedStatement albaran;
		Albaran ret = null;
		ResultSet res;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			albaran = conn.prepareStatement(query);
			albaran.setInt(1, id);
						
			res = albaran.executeQuery();
			
			if(res.next()) {
				ret = new Albaran(res);
			}
			
			res.close();
			albaran.close();
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
	 * Obten unha Listaxe de Albarans que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param id_incidencia
	 * @return
	 */
	public static ArrayList<Albaran> getByIdIncidencia(int id_incidencia) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE Id_incidencia = ?";
		
		PreparedStatement albaran;
		ArrayList<Albaran> ret = new ArrayList<Albaran>();

		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			albaran = conn.prepareStatement(query);
			
			albaran.setInt(1, id_incidencia);
						
			ResultSet res = albaran.executeQuery();
					
			while(res.next()) {			
				ret.add(new Albaran(res));
			}
			
			res.close();
			albaran.close();
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
			logger.error("Exception: ", e);
		 }
		
		return null;
	}
	
	
	/**
	 * Modifica os parámetros do albarán na base de datos
	 * @param id
	 * @param ruta_ficheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipo_ficheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome do albaran. NULL non o modifica.
	 * @param proveedor - Nome do proveedor. NULL non o modifica.
	 * @param num_albaran - Número do albarán. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @return
	 */
	public static boolean modificar(int id, String ruta_ficheiro, String tipo_ficheiro, String nome, String proveedor, String num_albaran, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (ruta_ficheiro != null && !ruta_ficheiro.equals("")) ? " Ruta_ficheiro = ?," : "";
		query += (tipo_ficheiro != null && !tipo_ficheiro.equals("")) ? " Tipo_ficheiro = ?," : "";
		query += (nome != null && !nome.equals("")) ? " Nome = ?," : "";
		query += (proveedor != null && !proveedor.equals("")) ? " Proveedor = ?," : "";
		query += (num_albaran != null && !num_albaran.equals("")) ? " Num_albaran = ?," : "";

		query += (comentarios != null && !comentarios.equals("")) ? " Comentarios = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		PreparedStatement albaran;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			albaran = conn.prepareStatement(query);	
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(ruta_ficheiro != null && !ruta_ficheiro.equals("")) albaran.setString(i++, ruta_ficheiro);
			if(tipo_ficheiro != null && !tipo_ficheiro.equals("")) albaran.setString(i++, tipo_ficheiro);
			if(nome != null && !nome.equals("")) albaran.setString(i++, nome);
			if(proveedor != null && !proveedor.equals("")) albaran.setString(i++, proveedor);
			if(num_albaran != null && !num_albaran.equals("")) albaran.setString(i++, num_albaran);
			if(comentarios != null && !comentarios.equals("")) albaran.setString(i++, comentarios);
			albaran.setInt(i++, id);
	
			int res = albaran.executeUpdate();
			
			albaran.close();
			
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
