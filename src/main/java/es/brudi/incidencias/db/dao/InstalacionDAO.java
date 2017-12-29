package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
	
	private final static String TABLENAME = "Instalacions";
	   
	private static Logger logger = Logger.getLogger(InstalacionDAO.class);
	
	/**
	 * @param id de instalación.
	 * @return Devolve o obxecto de instalación que corresponde co id.
	 */
	public static Instalacion getInstalacionById(int id) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Id=?;";
		PreparedStatement grupo;
		try
		 {
			
			grupo = conn.prepareStatement(query);
			grupo.setInt(1, id);
			
			ResultSet res = grupo.executeQuery();
			
			if(res.next()) {
				
				Instalacion ret = new Instalacion(res);
				
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
			logger.error("Exception: "+e);
		 }
		
		return null;
	}
	
	/**
	 * Obten todas as instalación que pertencen o cliente que se lle pasa.
	 * Se se lle pasa un -1, obtéñense todas as instalacións
	 * @param idCliente Id do cliente do que se quere obter as instalacions
	 * @return Lista de instalacions
	 */
	public static ArrayList<Instalacion> getInstalacionsByCliente(int idCliente) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Cod_cliente=?;";
		PreparedStatement grupo;
		try
		 {
			
			//Se o idCliente é -1, eliminamos a condición e executase directamente.
			if(idCliente<0) {
				query = "SELECT * FROM "+TABLENAME+";";
				grupo = conn.prepareStatement(query);
			}
			else {
				grupo = conn.prepareStatement(query);
				grupo.setInt(1, idCliente);
			}
			
			logger.debug("Realizase a consulta: "+query);
			
			ResultSet res = grupo.executeQuery();
			
			ArrayList<Instalacion> ret = new ArrayList<Instalacion>();
						
			while(res.next()) {			
				ret.add(new Instalacion(res));
			}
			res.close();
			grupo.close();
			return ret;
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
