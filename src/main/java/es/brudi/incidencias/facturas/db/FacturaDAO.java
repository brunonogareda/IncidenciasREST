package es.brudi.incidencias.facturas.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.facturas.Factura;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Facturas.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class FacturaDAO {

	private static final String TABLENAME = "Facturas";
	private static Logger logger = Logger.getLogger(FacturaDAO.class);
	
	private FacturaDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Inserta unha nova factura na base de datos.
	 * @param id
	 * @param rutaFicheiro
	 * @param tipoFicheiro
	 * @param comentarios
	 * @return
	 */
	protected static boolean crear(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (id, Ruta_ficheiro, Tipo_ficheiro, Comentarios) VALUES (?, ?, ?, ?);";
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			int i = 1;
			pst.setString(i++, id);
			pst.setString(i++, rutaFicheiro);
			pst.setString(i++, tipoFicheiro);
			pst.setString(i, comentarios);
						
			result = pst.executeUpdate();
			
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }
		
		return (result==1);
	}
	
	/**
	 * Obten unha factura mediante o id.
	 * @param id
	 * @return
	 */
	protected static Factura getById(String id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		Factura factura = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setString(1, id);
			res = pst.executeQuery();
			
			if(res.next()) {
				factura = new Factura(res);
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
		
		return factura;
	}

	/**
	 * Modifica os parámetros da factura na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param comentarios - Comentarios da factura. NULL non o modifica.
	 * @return
	 */
	protected static boolean modificar(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (rutaFicheiro != null && !rutaFicheiro.isEmpty()) ? " Ruta_ficheiro = ?," : "";
		query += (tipoFicheiro != null && !tipoFicheiro.isEmpty()) ? " Tipo_ficheiro = ?," : "";
		query += (comentarios != null && !comentarios.isEmpty()) ? " Comentarios = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		int result = -1;
		
		logger.debug("Realizase a consulta: "+query);
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(rutaFicheiro != null && !rutaFicheiro.isEmpty()) pst.setString(i++, rutaFicheiro);
			if(tipoFicheiro != null && !tipoFicheiro.isEmpty()) pst.setString(i++, tipoFicheiro);
			if(comentarios != null && !comentarios.isEmpty()) pst.setString(i++, comentarios);
			pst.setString(i++, id);
	
			result = pst.executeUpdate();
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
		
		return (result==1);
	}
}
