package es.brudi.incidencias.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

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
			logger.debug("Realizase a consulta: "+query);

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
	 * @param cod_parte - Se ven un 0, establecese na base de datos a null.
	 * @param ot - Se ven un 0, establecese na base de datos a null.
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
			logger.debug("Realizase a consulta: "+query);

			int i = 1;
			
			//Se son -1, ponse a null.
			String cod_parteS = (cod_parte == 0) ? null : String.valueOf(cod_parte);
			String otS = (ot == 0) ? null : String.valueOf(ot);
			
			incidencia = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			incidencia.setString(i++, cod_parteS);
			incidencia.setString(i++, otS);
			incidencia.setInt(i++, id_instalacion);
			incidencia.setString(i++, zona_apartamento);
			incidencia.setString(i++, descripcion_curta);
			incidencia.setString(i++, observacions);
			incidencia.setString(i++, estado);
			incidencia.setBoolean(i++, sol_presuposto);
			incidencia.setTimestamp(i++, data);
			incidencia.setString(i++, autor);

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

	/**
	 * Busca na base de datos as incidencias que coincidan cos parámetros.
	 * 
	 * @param cod_parte - Cod_parte a buscar. 0: busca calquera cod_parte. -1: busca os cod_parte nulos.
	 * @param ot - Orden_traballo a buscar. 0: busca calquera ot. -1: busca os ot nulos.
	 * @param id_instalacion - Id_instalacions a buscar. 0: busca calquera instalación.
	 * @param zona_apartamento - Zona_apartamento a buscar. NULL: busca calquera zona_apartamento.
	 * @param descripcion_curta - Descripción_curta a buscar. NULL: busca calquera descripcion_curta.
	 * @param observacions - Observacions a buscar. NULL: busca calquera observacions.
	 * @param estado - Estado a buscar. NULL: busca calquera estado.
	 * @param sol_presuposto - sol_presuposto a "true" ou a "false". NULL: busca calquera sol_presuposto
	 * @param presuposto - presuposto a buscar. NULL: busca calquera presuposto. "-1": busca os presupostos nulos.
	 * @param factura - factura a buscar. NULL: busca calquera factura. "-1": busca as facutras nulas.
	 * @param data_menor - busca datas maiores que esta. NULL: busca calquera data.
	 * @param data_maior - busca datas menores que esta. NULL: busca calquera data.
	 * @param autor - autora buscar. NULL: busca calquera autor.
	 * @param cod_cliente - Cod_cliente a buscar. 0: busca calquera cliente.
	 * @param ver - Número de incidencias máximas a buscar. NULL: Sen límite.
	 * @return
	 */
	public static ArrayList<Incidencia> get(int cod_parte, int ot, int id_instalacion,
			String zona_apartamento, String descripcion_curta, String observacions, String estado,
			String sol_presuposto, String presuposto, String factura, Timestamp data_menor, Timestamp data_maior,
			String autor, int cod_cliente, int ver) {
		Connection conn = DBConnectionManager.getConnection();

		//Construimos a query final. Se algún parámetro se busca calquera, non se engade o where na consulta.
		String query = "SELECT * FROM "+TABLENAME+" INC JOIN Instalacions INS ON INC.Instalacion=INS.Id WHERE";
		query += " INS.Cod_cliente LIKE ?";
		query += (cod_parte > 0) ? " AND Cod_parte = ?" : "";
		query += (cod_parte < 0 ) ? " AND Cod_parte IS NULL" : "";
		query += (ot > 0) ? " AND Orden_traballo = ?" : "";
		query += (ot < 0 ) ? " AND Orden_traballo IS NULL" : "";
		query += (id_instalacion > 0) ? " AND Instalacion = ?" : "";
		query += (zona_apartamento != null && !zona_apartamento.equals("")) ? " AND Zona_apartamento LIKE ?" : "";
		query += (descripcion_curta != null && !descripcion_curta.equals("")) ? " AND Descripcion_curta LIKE ?" : "";
		query += (observacions != null && !observacions.equals("")) ? " AND Observacions LIKE ?" : "";
		query += (estado != null && !estado.equals("")) ? " AND Estado = ?" : "";
		query += (sol_presuposto != null && (sol_presuposto.equals("true") || sol_presuposto.equals("false"))) ? " AND Solicitase_presuposto = ?" : "";
		if(presuposto != null && !presuposto.equals("")) {
			query += (presuposto.equals("-1")) ? " AND Presuposto IS NULL" : " AND Presuposto = ?";
		}
		if(factura != null && !factura.equals("")) {
			query += (factura.equals("-1")) ? " AND Factura IS NULL" : " AND Factura = ?";
		}
		query += (data_menor != null) ? " AND Data >= ?" : "";
		query += (data_maior != null) ? " AND Data <= ?" : "";
		query += (autor != null && !autor.equals("")) ? " AND Autor= ?" : "";
		query += " ORDER BY Data";												//Engadimos un condición para ordenar por data
		query += (ver > 0) ? " LIMIT ?" : "";
		query += ";";
		
		PreparedStatement incidencia;
		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			incidencia = conn.prepareStatement(query);
			
			String cod_clienteS = (cod_cliente == 0) ? "%" : String.valueOf(cod_cliente);
			
			//Engadimos os valores a query en caso de ser necesario.
			int i = 1;
			incidencia.setString(i++, cod_clienteS);
			if(cod_parte > 0) incidencia.setInt(i++, cod_parte);
			if(ot > 0) incidencia.setInt(i++, ot);
			if(id_instalacion > 0)  incidencia.setInt(i++, id_instalacion);
			if(zona_apartamento != null && !zona_apartamento.equals("")) incidencia.setString(i++, "%"+zona_apartamento+"%");
			if(descripcion_curta != null && !descripcion_curta.equals("")) incidencia.setString(i++, "%"+descripcion_curta+"%");
			if(observacions != null && !observacions.equals("")) incidencia.setString(i++, "%"+observacions+"%");
			if(estado != null && !estado.equals("")) incidencia.setString(i++, estado);
			if(sol_presuposto != null && sol_presuposto.equals("true")) incidencia.setBoolean(i++, true);
			if(sol_presuposto != null && sol_presuposto.equals("false")) incidencia.setBoolean(i++, false);
			if(presuposto != null && !presuposto.equals("") && !presuposto.equals("-1")) incidencia.setString(i++, presuposto);
			if(factura != null && !factura.equals("") && !factura.equals("-1")) incidencia.setString(i++, factura);
			if(data_menor != null) incidencia.setTimestamp(i++, data_menor);
			if(data_maior != null) incidencia.setTimestamp(i++, data_maior);
			if(autor != null && !autor.equals("")) incidencia.setString(i++, autor);
			if(ver > 0) incidencia.setInt(i++, ver);
						
			ResultSet res = incidencia.executeQuery();
			
			ArrayList<Incidencia> ret = new ArrayList<Incidencia>();
					
			while(res.next()) {			
				ret.add(new Incidencia(res));
			}
			
			res.close();
			incidencia.close();
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
	
	/**
	 * Modifica varios parámetros da incidencia que corresponda co Id que se lle pasa.
	 * 
	 * @param id - Identificador da incidencia que se quere modificar.
	 * @param cod_parte - valor que se quere dar a cod_parte. 0: non se modifica. -1: ponse a nulo.
	 * @param ot - valor que se quere dar a Orden de traballo. 0: non se modifica. -1: ponse a nulo.
	 * @param id_instalacion - instalación a que se lle asigna a incidencia. 0: non se modifica.
	 * @param zona_apartamento - valor que se lle da a zona_apartamento. NULL: non se modifica.
	 * @param descripcion_curta - valor que se lle da a descripcion_curta. NULL: non se modifica.
	 * @param observacions - valor que se lle asigna a observacions. NULL: non se modifica.
	 * @param estado - valor que se lle asigna a estado. NULL: non se modifica.
	 * @param sol_presuposto - Ponse a true ou a false. NULL: non se modifica.
	 * @return - True en caso de que se modifica correctamente. False ocorreu algún erro.
	 */
	public static boolean modificarIncidencia(int id, int cod_parte, int ot, int id_instalacion, String zona_apartamento,
			String descripcion_curta, String observacions, String estado, String sol_presuposto) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (cod_parte > 0) ? " Cod_parte = ?," : "";
		query += (cod_parte < 0 ) ? " Cod_parte = NULL," : "";
		query += (ot > 0) ? " Orden_traballo = ?," : "";
		query += (ot < 0 ) ? " Orden_traballo = NULL," : "";
		query += (id_instalacion > 0) ? " Instalacion = ?," : "";
		query += (zona_apartamento != null && !zona_apartamento.equals("")) ? " Zona_apartamento = ?," : "";
		query += (descripcion_curta != null && !descripcion_curta.equals("")) ? " Descripcion_curta = ?," : "";
		query += (observacions != null && !observacions.equals("")) ? " Observacions = ?," : "";
		query += (estado != null && !estado.equals("")) ? " Estado = ?," : "";
		query += (sol_presuposto != null && (sol_presuposto.equals("true") || sol_presuposto.equals("false"))) ? " Solicitase_presuposto = ?," : "";
		query += " Autor = autor";
		query += " WHERE Id = ?;";
		
		PreparedStatement incidencia;
		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			incidencia = conn.prepareStatement(query);	
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(cod_parte > 0) incidencia.setInt(i++, cod_parte);
			if(ot > 0) incidencia.setInt(i++, ot);
			if(id_instalacion > 0)  incidencia.setInt(i++, id_instalacion);
			if(zona_apartamento != null && !zona_apartamento.equals("")) incidencia.setString(i++, "%"+zona_apartamento+"%");
			if(descripcion_curta != null && !descripcion_curta.equals("")) incidencia.setString(i++, "%"+descripcion_curta+"%");
			if(observacions != null && !observacions.equals("")) incidencia.setString(i++, "%"+observacions+"%");
			if(estado != null && !estado.equals("")) incidencia.setString(i++, estado);
			if(sol_presuposto != null && sol_presuposto.equals("true")) incidencia.setBoolean(i++, true);
			if(sol_presuposto != null && sol_presuposto.equals("false")) incidencia.setBoolean(i++, false);
			incidencia.setInt(i++, id);
	
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
	

	/**
	 * Modifica o estado da incidencia
	 * @param id
	 * @param estado
	 * @return
	 */
	public static boolean modificarEstado(int id, String estado) {
		return IncidenciaDAO.modificarIncidencia(id, 0, 0, 0, null, null, null, estado, null);
	}
	
	/**
	 * Elimina unha incidencia
	 * 
	 * @param id
	 * @return
	 */
	public static boolean delete(int id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "DELETE FROM "+TABLENAME+" WHERE Id = ?;";
		
		PreparedStatement incidencia;
		try
		 {
			logger.debug("Realizase a consulta: "+query);
			
			incidencia = conn.prepareStatement(query);	
			incidencia.setInt(1, id);
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
