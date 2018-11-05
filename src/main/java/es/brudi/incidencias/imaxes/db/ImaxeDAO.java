package es.brudi.incidencias.imaxes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.imaxes.Imaxe;
import es.brudi.incidencias.incidencias.db.IncidenciaDAO;

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

	private static final String TABLENAME = "Imaxes_incidencias";
	private static Logger logger = Logger.getLogger(ImaxeDAO.class);
	
	private ImaxeDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	protected static int getNextId() {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT AUTO_INCREMENT FROM  INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME   = '"+TABLENAME+"';";
		
		ResultSet res = null;
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			res = pst.executeQuery();
			
			res.next();
	        result = res.getInt("AUTO_INCREMENT");
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
		
		return result;
	}
	
	/**
	 * Inserta unha nova i na base de datos.
	 * @param idIncidencia
	 * @param rutaFicheiro
	 * @param tipoFicheiro
	 * @param nome
	 * @param comentarios
	 * @param antesDespois
	 * @return
	 */
	public static int crear(int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String comentarios, boolean antesDespois) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (Id_incidencia, Nome, Ruta_imaxe, Tipo_imaxe, Antes_despois, Comentarios) VALUES (?, ?, ?, ?, ?, ?);";
		
		ResultSet res = null;
		int ret = -1;
		try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
		 {
			logger.debug("Realizase a consulta: "+query);
			
			int i = 1;
			pst.setInt(i++, idIncidencia);
			pst.setString(i++, nome);
			pst.setString(i++, rutaFicheiro);
			pst.setString(i++, tipoFicheiro);
			pst.setBoolean(i++, antesDespois);
			pst.setString(i++, comentarios);
						
			int result = pst.executeUpdate();
			
			if(result==1) {
				res = pst.getGeneratedKeys();
				if(res.next()) {
					ret = res.getInt(1);
				}
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
	 * Obten unha imaxe mediante o id.
	 * @param id
	 * @return
	 */
	public static Imaxe obterPorId(int id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT IM.*, I.Instalacion FROM "+TABLENAME+" AS IM INNER JOIN "+IncidenciaDAO.TABLENAME+" AS I ON IM.Id_incidencia=I.Id WHERE IM.Id = ?;";
		
		Imaxe ret = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setInt(1, id);
			res = pst.executeQuery();
			
			if(res.next()) {
				ret = new Imaxe(res);
				ret.setIdInstalacion(res.getInt("Instalacion"));
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
	 * Obten unha Listaxe de Imaxe que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Imaxe> obterPorIdIncidencia(int idIncidencia) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT IM.*, I.Instalacion FROM "+TABLENAME+" AS IM INNER JOIN "+IncidenciaDAO.TABLENAME+" AS I ON IM.Id_incidencia=I.Id WHERE I.Id = ?;";
		
		ResultSet res = null;
		List<Imaxe> ret = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setInt(1, idIncidencia);
						
			res = pst.executeQuery();
					
			while(res.next()) {	
				Imaxe imx = new Imaxe(res);
				imx.setIdInstalacion(res.getInt("Instalacion"));
				ret.add(imx);

			}
			
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
	 * Modifica os parámetros da imaxe na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome da imaxe. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @param antesDespois - Antes/Despois da incidencia. NULL non o modifica
	 * @return
	 */
	public static boolean modificar(int id, String rutaFicheiro, String tipoFicheiro, String nome, String comentarios, String antesDespois) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (rutaFicheiro != null && !rutaFicheiro.isEmpty()) ? " Ruta_imaxe = ?," : "";
		query += (tipoFicheiro != null && !tipoFicheiro.isEmpty()) ? " Tipo_imaxe = ?," : "";
		query += (nome != null && !nome.isEmpty()) ? " Nome = ?," : "";
		query += (comentarios != null && !comentarios.isEmpty()) ? " Comentarios = ?," : "";
		query += ("antes".equalsIgnoreCase(antesDespois) || "despois".equalsIgnoreCase(antesDespois)
				|| "despues".equalsIgnoreCase(antesDespois) || "después".equalsIgnoreCase(antesDespois)) ? " Antes_despois = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(rutaFicheiro != null && !rutaFicheiro.isEmpty()) pst.setString(i++, rutaFicheiro);
			if(tipoFicheiro != null && !tipoFicheiro.isEmpty()) pst.setString(i++, tipoFicheiro);
			if(nome != null && !nome.isEmpty()) pst.setString(i++, nome);
			if(comentarios != null && !comentarios.isEmpty()) pst.setString(i++, comentarios);
			if("antes".equalsIgnoreCase(antesDespois)) pst.setBoolean(i++, false);
			if("despois".equalsIgnoreCase(antesDespois) || "despues".equalsIgnoreCase(antesDespois) || "después".equalsIgnoreCase(antesDespois)) pst.setBoolean(i++, true);
			pst.setInt(i++, id);
	
			result = pst.executeUpdate();
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception in DAO: ", e);
		 }
		
		return (result == 1);
	}
}
