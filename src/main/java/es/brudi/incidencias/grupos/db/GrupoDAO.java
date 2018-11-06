package es.brudi.incidencias.grupos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.grupos.Grupo;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Grupos.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class GrupoDAO {
	
	private static final String TABLENAME = "Grupos";
	public static final String TABLENAME_INSTALACIONS = "Grupo_instalacion";
	private static Logger logger = Logger.getLogger(GrupoDAO.class);
	
	private GrupoDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return Número de tuplas na táboa de Grupos.
	 */
	public static int count() {
	    	
		Connection conn = DBConnectionManager.getConnection();
	
		String query = "SELECT COUNT(*) AS count FROM "+TABLENAME;
		ResultSet res = null;
		int result = 0;
	    try (PreparedStatement pst = conn.prepareStatement(query)) {
	    	
	        res = pst.executeQuery();
	        res.next();
	        result = res.getInt("count");
	    }
	    catch(SQLException se) {
	    	logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
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
	 * @param id de grupo
	 * @return Devolve o obxecto grupo que corresponde co Id que se lle pasou.
	 */
	public static Grupo getGrupoById(int id) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" LEFT JOIN "+TABLENAME_INSTALACIONS+" ON Id=Id_grupo WHERE Id=?;";
		ResultSet res = null;
		Grupo ret = null;
		try(PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setInt(1, id);
			res = pst.executeQuery();
			ret = new Grupo(res);
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
}
	