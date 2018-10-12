package es.brudi.incidencias.clientes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.clientes.Cliente;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Clientes.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class ClienteDAO {

	private static final String TABLENAME = "Clientes";
	private static Logger logger = Logger.getLogger(ClienteDAO.class);
	
	private ClienteDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return Número de tuplas na táboa de Clientes.
	 */
	protected static int count() {
	    	
	    Connection conn = DBConnectionManager.getConnection();
	
	    String query = "SELECT COUNT(*) AS count FROM "+TABLENAME;
	    ResultSet res = null;
	    int ret = 0;
	    try (PreparedStatement pst = conn.prepareStatement(query)) {
	    	
	        res = pst.executeQuery();
	        
	        res.next();
	        ret = res.getInt("count");
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
	    
	    return ret;
	}
	
	/**
	 * @return ArrayList con todos os Clientes da base de datos.
	 */
	protected static List<Cliente> getClientes() {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+";";
		ResultSet res = null;
		ArrayList<Cliente> clientes = new ArrayList<>();

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			res = pst.executeQuery();
			
			while(res.next()) {
				Cliente cli = new Cliente(res);
				clientes.add(cli);
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
		
		return clientes;
		
	}
	
	/**
	 * @param id de cliente
	 * @return Devolve o obxecto cliente que corresponde co Id que se lle pasou.
	 */
	protected static Cliente getClienteById(int id) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Cod_cliente=?;";
		ResultSet res = null;
		Cliente ret = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			pst.setInt(1, id);
			res = pst.executeQuery();
							
			if(res.next()) {
				ret = new Cliente(res);
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
}
