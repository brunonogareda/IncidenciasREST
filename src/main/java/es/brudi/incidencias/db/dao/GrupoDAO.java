package es.brudi.incidencias.db.dao;

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
	
	private final static String TABLENAME = "Grupos";
		   
	private static Logger logger = Logger.getLogger(GrupoDAO.class);
	
	/**
	 * @return Número de tuplas na táboa de Grupos.
	 */
	public static int count() {
	    	
		Connection conn = DBConnectionManager.getConnection();
	
		String query = "SELECT COUNT(*) AS count FROM "+TABLENAME;
	    PreparedStatement counter;
	    try
	    {
	        counter = conn.prepareStatement(query);
	        ResultSet res = counter.executeQuery();
	        res.next();
	        int ret = res.getInt("count");
	        res.close();
	        counter.close();
	        return ret;
	    }
	    catch(SQLException se)
	    {
	    	logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
	    }
	    return 0;
	}
	
	/**
	 * @param id de grupo
	 * @return Devolve o obxecto grupo que corresponde co Id que se lle pasou.
	 */
	public static Grupo getGrupoById(int id) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Id=?;";
		PreparedStatement grupo;
		try
		 {
			
			grupo = conn.prepareStatement(query);
			grupo.setInt(1, id);
			
			ResultSet res = grupo.executeQuery();
			
			if(res.next()) {
				String nome = res.getString("Nome");
				String permisos = res.getString("Permisos");
				
				Grupo ret = new Grupo(id, nome, permisos);
				res.close();
				grupo.close();
				
				return ret;
			}
			else {
				return null;
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
			logger.error("Exception: ", e);
		 }
		
		return null;
	}
}
	