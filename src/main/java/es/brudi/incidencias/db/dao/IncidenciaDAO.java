package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.incidencias.Incidencia;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Incidencias.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class IncidenciaDAO {

	private final static String TABLENAME = "Incidencias";
	   
	private static Logger logger = Logger.getLogger(IncidenciaDAO.class);
	
	/**
	 * @param id de grupo
	 * @return Devolve o obxecto grupo que corresponde co Id que se lle pasou.
	 */
	public static Incidencia getIncidenciaById(int id) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Id=?;";
		PreparedStatement incidencia;
		Incidencia ret = null;
		ResultSet res;
		try
		 {
			incidencia = conn.prepareStatement(query);
			incidencia.setInt(1, id);
			
			res = incidencia.executeQuery();
			if(res.next()) {
				ret = new Incidencia(res);
			}
			
			res.close();
			incidencia.close();
			
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

	/**
	 * Inserta unha nova incidencia na base de datos.
	 * 
	 * @param cod_parte
	 * @param ot
	 * @param id_instalacion
	 * @param zona_apartamento
	 * @param descripcion_curta
	 * @param observacions
	 * @param estado
	 * @param sol_presuposto
	 * @param data
	 * @param autor
	 * @return - Id da nova incidencia. (Se -1 existiu un erro creando a incidencia).
	 */
	public static int crear(int cod_parte, int ot, int id_instalacion, String zona_apartamento,
			String descripcion_curta, String observacions, String estado, boolean sol_presuposto, Timestamp data, String autor) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "INSERT INTO "+TABLENAME+" (Cod_parte, Orden_traballo, Instalacion, Zona_apartamento, Descripcion_curta, "
						+ "Observacions, Estado, Solicitase_presuposto, Data, Autor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement incidencia;
		try
		 {
			incidencia = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			incidencia.setInt(1, cod_parte);
			incidencia.setInt(2, ot);
			incidencia.setInt(3, id_instalacion);
			incidencia.setString(4, zona_apartamento);
			incidencia.setString(5, descripcion_curta);
			incidencia.setString(6, observacions);
			incidencia.setString(7, estado);
			incidencia.setBoolean(8, sol_presuposto);
			incidencia.setTimestamp(9, data);
			incidencia.setString(10, autor);

			int res = incidencia.executeUpdate();
						
			if(res==1) {
				ResultSet id = incidencia.getGeneratedKeys();
				if(id.next()) {
					return id.getInt(1);
				}
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
		
		return -1;
	}
	
}
