package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.facturas.Factura;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Grupos.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class FacturaDAO {

	private final static String TABLENAME = "Facturas";
	   
	private static Logger logger = Logger.getLogger(FacturaDAO.class);
	
	/**
	 * Inserta unha nova factura na base de datos.
	 * @param id
	 * @param url
	 * @param comentarios
	 * @return
	 */
	public static boolean crear(String id, String ruta_ficheiro, String tipo_ficheiro, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "INSERT INTO "+TABLENAME+" (id, Ruta_ficheiro, Tipo_ficheiro, Comentarios) VALUES (?, ?, ?, ?);";
		
		PreparedStatement factura;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			factura = conn.prepareStatement(query);
			
			int i = 1;
			factura.setString(i++, id);
			factura.setString(i++, ruta_ficheiro);
			factura.setString(i++, tipo_ficheiro);
			factura.setString(i++, comentarios);
						
			int res = factura.executeUpdate();
			
			factura.close();
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
	 * Obten unha factura mediante o id.
	 * @param id
	 * @return
	 */
	public static Factura getById(String id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT * FROM "+TABLENAME+" WHERE id = ?;";
		
		PreparedStatement factura;
		Factura ret = null;
		ResultSet res;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			factura = conn.prepareStatement(query);
			factura.setString(1, id);
						
			res = factura.executeQuery();
			
			if(res.next()) {
				ret = new Factura(res);
			}
			
			res.close();
			factura.close();
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

	public static boolean modificar(String id_factura, String ruta_ficheiro, String tipo_ficheiro, String comentarios) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (ruta_ficheiro != null && !ruta_ficheiro.equals("")) ? " Ruta_ficheiro = ?," : "";
		query += (tipo_ficheiro != null && !tipo_ficheiro.equals("")) ? " Tipo_ficheiro = ?," : "";
		query += (comentarios != null && !comentarios.equals("")) ? " Comentarios = ?," : "";
		query += " Id = Id";
		query += " WHERE Id = ?;";
		
		PreparedStatement incidencia;
		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			incidencia = conn.prepareStatement(query);	
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(ruta_ficheiro != null && !ruta_ficheiro.equals("")) incidencia.setString(i++, ruta_ficheiro);
			if(tipo_ficheiro != null && !tipo_ficheiro.equals("")) incidencia.setString(i++, tipo_ficheiro);
			if(comentarios != null && !comentarios.equals("")) incidencia.setString(i++, comentarios);
			incidencia.setString(i++, id_factura);
	
			int res = incidencia.executeUpdate();
			
			incidencia.close();
			
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
