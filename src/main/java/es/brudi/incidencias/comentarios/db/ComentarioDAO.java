package es.brudi.incidencias.comentarios.db;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.incidencias.db.IncidenciaDAO;
import es.brudi.incidencias.util.Util;

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
	
	private static final String TABLENAME = "Comentarios";
	private static Logger logger = Logger.getLogger(ComentarioDAO.class);

	private ComentarioDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Inserta un novo comentario na base de datos.
	 * 
	 * @param idIncidencia - Incidencia coque se corresponde o comentario
	 * @param autor - Autor do comentario
	 * @param accion - Acción realizada para que se inserte o comentario.
	 * @param tipo - Tipo de comentario.
	 * @param texto - Texto do comentario.
	 * @param data - Data do comentario.
	 * @return - Id do novo comentario. -1 existiuu algún error ao insertar.
	 */
	protected static int crear(int idIncidencia, int autor, String accion, int tipo, String texto) {
		
		Timestamp data = Util.obterTimestampActual();
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "INSERT INTO "+TABLENAME+" (Id_incidencia, Autor, Accion, Tipo, Texto, Data) VALUES (?, ?, ?, ?, ?, ?);";
		ResultSet res = null;
		int id = -1;
		try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			logger.debug("Realizase a consulta: "+query);

			pst.setInt(1, idIncidencia);
			pst.setInt(2, autor);
			pst.setString(3, accion);
			pst.setInt(4, tipo);
			pst.setString(5, texto);
			pst.setTimestamp(6, data);
			
			int result = pst.executeUpdate();
			
			if(result==1) {
				res = pst.getGeneratedKeys();
				if(res.next()) {
					id = res.getInt(1);
				}
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
		
		return id;
	}
	

	/**
	 * Obten unha Listaxe de Comentarios que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	protected static ArrayList<Comentario> obtrePorIdIncidencia(int idIncidencia) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "SELECT C.*, I.Instalacion AS Instalacion FROM "+TABLENAME+" AS C INNER JOIN "+IncidenciaDAO.TABLENAME+" AS I ON C.Id_incidencia=I.Id WHERE C.Id_incidencia=?;";
		
		ResultSet res = null;
		ArrayList<Comentario> comentarios = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setInt(1, idIncidencia);
			res = pst.executeQuery();
			
			while(res.next()) {
				Comentario com = new Comentario(res);
				com.setInstalacion(res.getInt("Instalacion"));
				comentarios.add(com);
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
		
		return comentarios;
	}
	
	
}
