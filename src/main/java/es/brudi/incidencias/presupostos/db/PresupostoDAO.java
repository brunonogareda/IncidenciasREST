package es.brudi.incidencias.presupostos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.incidencias.db.IncidenciaDAO;
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

	private PresupostoDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	private static final String TABLENAME = "Presupostos";
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
	protected static boolean crear(String id, String rutaFicheiro, String tipoFicheiro, String comentarios, boolean aceptado) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (Id, Ruta_ficheiro, Tipo_ficheiro, aceptado, Comentarios) VALUES (?, ?, ?, ?, ?);";
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			int i = 1;
			pst.setString(i++, id);
			pst.setString(i++, rutaFicheiro);
			pst.setString(i++, tipoFicheiro);
			pst.setBoolean(i++, aceptado);
			pst.setString(i, comentarios);
						
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
	
	/**
	 * Obten un presuposto mediante o id.
	 * @param id
	 * @return
	 */
	protected static Presuposto obterPorId(String id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		Presuposto ret = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setString(1, id);
			res = pst.executeQuery();
			
			if(res.next())
				ret = new Presuposto(res);
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
	 * Obten o presuposto solicitado coa incidencia a que pertence a instalación a que está asignado.
	 * @param id_presuposto
	 * @return Presuposto
	 */
	protected static Presuposto obterPresupostoEInstalacionPorId(String idPresuposto) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT P.*, I.Instalacion AS Instalacion FROM "+TABLENAME+" AS P INNER JOIN "+IncidenciaDAO.TABLENAME+" AS I ON P.Id=I.Presuposto WHERE P.Id=?;";
		Presuposto presuposto = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setString(1, idPresuposto);
						
			res = pst.executeQuery();
			
			if(res.next()) {
				presuposto = new Presuposto(res);
				presuposto.setInstalacion(res.getInt("Instalacion"));
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
		
		return presuposto;
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
	protected static boolean modificar(String id, String rutaFicheiro, String tipoFicheiro, String comentarios, String aceptado) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = crearQueryModificar(rutaFicheiro, tipoFicheiro, comentarios, aceptado);
		
		int result = -1;
		try(PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);

			//Engádense os parámetros pasados a query.
			int i = 1;
			if(rutaFicheiro != null && !rutaFicheiro.equals("")) pst.setString(i++, rutaFicheiro);
			if(tipoFicheiro != null && !tipoFicheiro.equals("")) pst.setString(i++, tipoFicheiro);
			if(comentarios != null && !comentarios.equals("")) pst.setString(i++, comentarios);
			if(aceptado != null && aceptado.equals("true")) pst.setBoolean(i++, true);
			if(aceptado != null && aceptado.equals("false")) pst.setBoolean(i++, false);
			pst.setString(i, id);
	
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
	
	private static String crearQueryModificar(String rutaFicheiro, String tipoFicheiro, String comentarios, String aceptado) {
		
		String query = "UPDATE "+TABLENAME+" SET";
		query += (rutaFicheiro != null && !rutaFicheiro.equals("")) ? " Ruta_ficheiro = ?," : "";
		query += (tipoFicheiro != null && !tipoFicheiro.equals("")) ? " Tipo_ficheiro = ?," : "";
		query += (comentarios != null && !comentarios.equals("")) ? " Comentarios = ?," : "";
		query += (aceptado != null && (aceptado.equals("true") || aceptado.equals("false"))) ? " Aceptado = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		return query;
	}
}
