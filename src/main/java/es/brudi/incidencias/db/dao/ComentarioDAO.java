package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Comentarios.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class ComentarioDAO {
	private final static String TABLENAME = "Comentarios";
	   
	private static Logger logger = Logger.getLogger(ComentarioDAO.class);

	/**
	 * Inserta un novo comentario na base de datos.
	 * 
	 * @param id_incidencia - Incidencia coque se corresponde o comentario
	 * @param autor - Autor do comentario
	 * @param accion - Acción realizada para que se inserte o comentario.
	 * @param tipo - Tipo de comentario.
	 * @param texto - Texto do comentario.
	 * @param data - Data do comentario.
	 * @return - Id do novo comentario. -1 existiuu algún error ao insertar.
	 */
	public static int crear(int id_incidencia, String autor, String accion, int tipo, String texto, Timestamp data) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "INSERT INTO "+TABLENAME+" (Id_incidencia, Autor, Accion, Tipo, Texto, Data) VALUES (?, ?, ?, ?, ?, ?);";
		PreparedStatement comentario;
		try
		 {
			comentario = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			comentario.setInt(1, id_incidencia);
			comentario.setString(2, autor);
			comentario.setString(3, accion);
			comentario.setInt(4, tipo);
			comentario.setString(5, texto);
			comentario.setTimestamp(6, data);
			int res = comentario.executeUpdate();
			
			if(res==1) {
				ResultSet id = comentario.getGeneratedKeys();
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
