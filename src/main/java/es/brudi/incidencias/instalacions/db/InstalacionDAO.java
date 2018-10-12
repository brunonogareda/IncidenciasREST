package es.brudi.incidencias.instalacions.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.instalacions.Instalacion;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Instalacións.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class InstalacionDAO {
	
	private InstalacionDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	public static final String TABLENAME = "Instalacions";
	private static Logger logger = Logger.getLogger(InstalacionDAO.class);
	
	/**
	 * @param id de instalación.
	 * @return Devolve o obxecto de instalación que corresponde co id.
	 */
	protected static Instalacion getInstalacionById(int id) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Id=?;";
		ResultSet res = null;
		Instalacion instalacion = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setInt(1, id);
			res = pst.executeQuery();
			
			if(res.next())
				instalacion = new Instalacion(res);
			
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
		
		return instalacion;
	}
	
	/**
	 * Obten todas as instalación que pertencen o cliente que se lle pasa.
	 * Se se lle pasa un -1, obtéñense todas as instalacións
	 * @param idCliente Id do cliente do que se quere obter as instalacions
	 * @return Lista de instalacions
	 */
	protected static List<Instalacion> getInstalacionsByCliente(int idCliente) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Cod_cliente=?;";
		ResultSet res = null;
		List<Instalacion> instalacions = new ArrayList<>();
		
		if (idCliente < 0) //Se o idCliente é -1, eliminamos a condición e executase directamente.
			query = "SELECT * FROM "+TABLENAME+";";
		
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			if(idCliente >= 0) {
				pst.setInt(1, idCliente);
			}

			logger.debug("Realizase a consulta: "+query);
			
			res = pst.executeQuery();
						
			while(res.next()) {			
				instalacions.add(new Instalacion(res));
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
		
		return instalacions;
	}

}
