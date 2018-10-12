package es.brudi.incidencias.albarans.db;

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

	private static final String TABLENAME = "Imaxes_albarans";
	   
	private static Logger logger = Logger.getLogger(AlbaranDAO.class);
	
	private AlbaranDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	protected static int getNextId() {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT AUTO_INCREMENT FROM  INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME   = '"+TABLENAME+"';";
		int ret = -1;
		ResultSet res = null;
		
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			res = pst.executeQuery();
			
			res.next();
	        ret = res.getInt("AUTO_INCREMENT");
	        
	        return ret;
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception in DAO: ", e);
		 }
		finally {
			try {
				if(res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}
		
		return ret;
	}
	
	/**
	 * Inserta un novo albaran na base de datos.
	 * @param idIncidencia
	 * @param rutaFicheiro
	 * @param tipoFicheiro
	 * @param nome
	 * @param proveedores
	 * @param numAlbaran
	 * @param comentarios
	 * @return
	 */
	protected static int crear(int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String proveedor, String numAlbaran, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (Id_incidencia, Nome, Proveedor, Num_albaran, Ruta_ficheiro, Tipo_ficheiro, Comentarios) VALUES (?, ?, ?, ?, ?, ?, ?);";
		
		ResultSet res = null;
		int id = -1;
		
		try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
		 {
			logger.debug("Realizase a consulta: "+query);
			
			int i = 1;
			pst.setInt(i++, idIncidencia);
			pst.setString(i++, nome);
			pst.setString(i++, proveedor);
			pst.setString(i++, numAlbaran);
			pst.setString(i++, rutaFicheiro);
			pst.setString(i++, tipoFicheiro);
			pst.setString(i++, comentarios);
						
			int result = pst.executeUpdate();
					
			if(result==1) {
				res = pst.getGeneratedKeys();
				if(res.next()) {
					id = res.getInt(1);
					return id;
				}
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
		finally {
			try {
				if(res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}
		
		return id;
	}
	
	/**
	 * Obten un albaran mediante o id.
	 * @param id
	 * @return
	 */
	protected static Albaran getById(int id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		Albaran ret = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			logger.debug("Realizase a consulta: "+query);
			
			pst.setInt(1, id);
						
			res = pst.executeQuery();
			
			if(res.next()) {
				ret = new Albaran(res);
			}
			
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception in DAO: ", e);
		 }
		finally {
			try {
				if(res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}
		
		return ret;
	}

	/**
	 * Obten unha Listaxe de Albarans que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param id_incidencia
	 * @return
	 */
	protected static ArrayList<Albaran> getByIdIncidencia(int idIncidencia) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE Id_incidencia = ?";
		
		ArrayList<Albaran> ret = new ArrayList<>();

		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			logger.debug("Realizase a consulta: "+query);
			
			pst.setInt(1, idIncidencia);
						
			res = pst.executeQuery();
					
			while(res.next()) {			
				ret.add(new Albaran(res));
			}
			
			res.close();
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }
		finally {
			try {
				if(res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}
		
		return ret;
	}
	
	
	/**
	 * Modifica os parámetros do albarán na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome do albaran. NULL non o modifica.
	 * @param proveedor - Nome do proveedor. NULL non o modifica.
	 * @param numAlbaran - Número do albarán. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @return
	 */
	protected static boolean modificar(int id, String rutaFicheiro, String tipoFicheiro, String nome, String proveedor, String numAlbaran, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (rutaFicheiro != null && !rutaFicheiro.isEmpty()) ? " Ruta_ficheiro = ?," : "";
		query += (tipoFicheiro != null && !tipoFicheiro.isEmpty()) ? " Tipo_ficheiro = ?," : "";
		query += (nome != null && !nome.isEmpty()) ? " Nome = ?," : "";
		query += (proveedor != null && !proveedor.isEmpty()) ? " Proveedor = ?," : "";
		query += (numAlbaran != null && !numAlbaran.isEmpty()) ? " Num_albaran = ?," : "";

		query += (comentarios != null && !comentarios.isEmpty()) ? " Comentarios = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		int ret = -1;
		
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(rutaFicheiro != null && !rutaFicheiro.isEmpty()) pst.setString(i++, rutaFicheiro);
			if(tipoFicheiro != null && !tipoFicheiro.isEmpty()) pst.setString(i++, tipoFicheiro);
			if(nome != null && !nome.isEmpty()) pst.setString(i++, nome);
			if(proveedor != null && !proveedor.isEmpty()) pst.setString(i++, proveedor);
			if(numAlbaran != null && !numAlbaran.isEmpty()) pst.setString(i++, numAlbaran);
			if(comentarios != null && !comentarios.isEmpty()) pst.setString(i++, comentarios);
			pst.setInt(i++, id);
	
			ret = pst.executeUpdate();
			
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception in DAO: ", e);
		 }
		
		return (ret == 1);
	}
}
