package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

	private final static String TABLENAME = "Clientes";
	   
	private static Logger logger = Logger.getLogger(ClienteDAO.class);
	
	/**
	 * @return Número de tuplas na táboa de Clientes.
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
	 * @return ArrayList con todos os Clientes da base de datos.
	 */
	public static ArrayList<Cliente> getClientes() {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+";";
		Statement cliente;
		try
		 {
			ArrayList<Cliente> clientes = new ArrayList<Cliente>();
			
			cliente = conn.createStatement();
			
			ResultSet res = cliente.executeQuery(query);
			
			while(res.next()) {
	
				int id = res.getInt("Cod_cliente");
				String nome = res.getString("Nome");
				String nome_curto = res.getString("Nome_curto");
				String nif = res.getString("NIF");
				//byte cod_parte_propio = res.getByte("Cod_parte_propio");
				boolean cod_parte_propio = res.getBoolean("Cod_parte_propio");					
				
				Cliente cli = new Cliente(id, nome, nome_curto, nif, cod_parte_propio);
				
				clientes.add(cli);
			}
			
			res.close();
			cliente.close();
			return clientes;
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
		
		return null;
		
	}
	
	/**
	 * @param id de cliente
	 * @return Devolve o obxecto cliente que corresponde co Id que se lle pasou.
	 */
	public static Cliente getClienteById(int id) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Cod_cliente=?;";
		PreparedStatement cliente;
		try
		 {
			
			cliente = conn.prepareStatement(query);
			cliente.setInt(1, id);
			
			ResultSet res = cliente.executeQuery();
							
			if(res.next()) {
				String nome = res.getString("Nome");
				String nome_curto = res.getString("Nome_curto");
				String nif = res.getString("NIF");
				byte cod_parte_propio = res.getByte("Cod_parte_propio");
				
				Cliente ret = new Cliente(id, nome, nome_curto, nif, cod_parte_propio);
				
				res.close();
				cliente.close();
				
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
			logger.error("Exception: "+e);
		 }
		
		return null;
		
	}
}
