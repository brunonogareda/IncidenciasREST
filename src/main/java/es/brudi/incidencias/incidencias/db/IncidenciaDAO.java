package es.brudi.incidencias.incidencias.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

	public static final String TABLENAME = "Incidencias";
	private static Logger logger = Logger.getLogger(IncidenciaDAO.class);
	
	private IncidenciaDAO() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @param id de Incidencia
	 * @return Devolve o obxecto incidencia que corresponde co Id que se lle pasou.
	 */
	protected static Incidencia obterIncidenciaPorId(int id) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Id=?;";
		Incidencia ret = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {			
			logger.debug("Realizase a consulta: "+query);

			pst.setInt(1, id);
			
			res = pst.executeQuery();
			if(res.next()) {
				ret = new Incidencia(res);
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
	
	/**
	 * @param id de Factura
	 * @return Devolve o obxecto incidencia que corresponde que conteña a factura correspondente.
	 */
	protected static Incidencia obterIncidenciaPorFactura(String factura) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Factura=?;";
		Incidencia ret = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {			
			logger.debug("Realizase a consulta: "+query);

			pst.setString(1, factura);
			res = pst.executeQuery();
			if(res.next()) {
				ret = new Incidencia(res);
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

	/**
	 * @param id de Presuposto
	 * @return Devolve o obxecto incidencia que corresponde que conteña o presuposto correspondente.
	 */
	protected static Incidencia obterIncidenciaPorPresuposto(String presuposto) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Presuposto=?;";
		Incidencia ret = null;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query))
		 {			
			logger.debug("Realizase a consulta: "+query);

			pst.setString(1, presuposto);
			res = pst.executeQuery();
			if(res.next()) {
				ret = new Incidencia(res);
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
	
	

	/**
	 * Inserta unha nova incidencia na base de datos.
	 * 
	 * @param codParte - Se ven un 0, establecese na base de datos a null.
	 * @param ot - Se ven un 0, establecese na base de datos a null.
	 * @param idInstalacion
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param estado
	 * @param solPresuposto
	 * @param data
	 * @param autor
	 * @return - Id da nova incidencia. (Se -1 existiu un erro creando a incidencia).
	 */
	protected static int crear(int codParte, int ot, int idInstalacion, String zonaApartamento,
			String descripcionCurta, String observacions, String estado, boolean solPresuposto, Timestamp data, String autor) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "INSERT INTO "+TABLENAME+" (Cod_parte, Orden_traballo, Instalacion, Zona_apartamento, Descripcion_curta, "
						+ "Observacions, Estado, Solicitase_presuposto, Data, Autor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		int id = -1;
		ResultSet res = null;
		try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			logger.debug("Realizase a consulta: "+query);

			int i = 1;
			
			//Se son -1, ponse a null.
			String codParteS = (codParte == 0) ? null : String.valueOf(codParte);
			String otS = (ot == 0) ? null : String.valueOf(ot);
			
			pst.setString(i++, codParteS);
			pst.setString(i++, otS);
			pst.setInt(i++, idInstalacion);
			pst.setString(i++, zonaApartamento);
			pst.setString(i++, descripcionCurta);
			pst.setString(i++, observacions);
			pst.setString(i++, estado);
			pst.setBoolean(i++, solPresuposto);
			pst.setTimestamp(i++, data);
			pst.setString(i++, autor);

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
	 * Busca na base de datos as incidencias que coincidan cos parámetros.
	 * 
	 * @param codParte - Cod_parte a buscar. 0: busca calquera cod_parte. -1: busca os cod_parte nulos.
	 * @param ot - Orden_traballo a buscar. 0: busca calquera ot. -1: busca os ot nulos.
	 * @param idInstalacion - Id_instalacions a buscar. 0: busca calquera instalación.
	 * @param zonaApartamento - Zona_apartamento a buscar. NULL: busca calquera zona_apartamento.
	 * @param descripcionCurta - Descripción_curta a buscar. NULL: busca calquera descripcion_curta.
	 * @param observacions - Observacions a buscar. NULL: busca calquera observacions.
	 * @param estado - Estado a buscar. NULL: busca calquera estado.
	 * @param solPresuposto - sol_presuposto a "true" ou a "false". NULL: busca calquera sol_presuposto
	 * @param presuposto - presuposto a buscar. NULL: busca calquera presuposto. "-1": busca os presupostos nulos.
	 * @param factura - factura a buscar. NULL: busca calquera factura. "-1": busca as facutras nulas.
	 * @param dataMenor - busca datas maiores que esta. NULL: busca calquera data.
	 * @param dataMaior - busca datas menores que esta. NULL: busca calquera data.
	 * @param autor - autora buscar. NULL: busca calquera autor.
	 * @param codCliente - Cod_cliente a buscar. 0: busca calquera cliente.
	 * @param ver - Número de incidencias máximas a buscar. NULL: Sen límite.
	 * @return
	 */
	protected static List<Incidencia> obter(int codParte, int ot, int idInstalacion,
			String zonaApartamento, String descripcionCurta, String observacions, List<String> estados,
			String solPresuposto, String presuposto, String factura, Timestamp dataMenor, Timestamp dataMaior,
			String autor, int codCliente, int ver) {
		Connection conn = DBConnectionManager.getConnection();

		//Construimos a query final. Se algún parámetro se busca calquera, non se engade o where na consulta.
		String query = "SELECT * FROM "+TABLENAME+" INC JOIN Instalacions INS ON INC.Instalacion=INS.Id WHERE";
		query += " INS.Cod_cliente LIKE ?";
		query += (codParte > 0) ? " AND Cod_parte = ?" : "";
		query += (codParte < 0 ) ? " AND Cod_parte IS NULL" : "";
		query += (ot > 0) ? " AND Orden_traballo = ?" : "";
		query += (ot < 0 ) ? " AND Orden_traballo IS NULL" : "";
		query += (idInstalacion > 0) ? " AND Instalacion = ?" : "";
		query += (zonaApartamento != null && !zonaApartamento.isEmpty()) ? " AND Zona_apartamento LIKE ?" : "";
		query += (descripcionCurta != null && !descripcionCurta.isEmpty()) ? " AND Descripcion_curta LIKE ?" : "";
		query += (observacions != null && !observacions.isEmpty()) ? " AND Observacions LIKE ?" : "";
		if(estados != null && !estados.isEmpty()) { //Recorremos o listado de estados e engaimos según o número.
			query += " AND (";
			for(int j=0; j<estados.size(); j++) {
				query += " Estado = ?";
				if(j<estados.size()-1) query += " OR";
				else query += " )";
			}
		}
		query += (solPresuposto != null && (solPresuposto.equals("true") || solPresuposto.equals("false"))) ? " AND Solicitase_presuposto = ?" : "";
		if(presuposto != null && !presuposto.isEmpty()) {
			query += (presuposto.equals("-1")) ? " AND Presuposto IS NULL" : " AND Presuposto = ?";
		}
		if(factura != null && !factura.equals("")) {
			query += (factura.equals("-1")) ? " AND Factura IS NULL" : " AND Factura = ?";
		}
		query += (dataMenor != null) ? " AND Data >= ?" : "";
		query += (dataMaior != null) ? " AND Data <= ?" : "";
		query += (autor != null && !autor.isEmpty()) ? " AND Autor= ?" : "";
		query += " ORDER BY Data";												//Engadimos un condición para ordenar por data
		query += (ver > 0) ? " LIMIT ?" : "";
		query += ";";
		
		ResultSet res = null;
		ArrayList<Incidencia> incidencias = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			String codClienteS = (codCliente == 0) ? "%" : String.valueOf(codCliente);
			
			//Engadimos os valores a query en caso de ser necesario.
			int i = 1;
			pst.setString(i++, codClienteS);
			if(codParte > 0) pst.setInt(i++, codParte);
			if(ot > 0) pst.setInt(i++, ot);
			if(idInstalacion > 0)  pst.setInt(i++, idInstalacion);
			if(zonaApartamento != null && !zonaApartamento.isEmpty()) pst.setString(i++, "%"+zonaApartamento+"%");
			if(descripcionCurta != null && !descripcionCurta.isEmpty()) pst.setString(i++, "%"+descripcionCurta+"%");
			if(observacions != null && !observacions.isEmpty()) pst.setString(i++, "%"+observacions+"%");
			if(estados != null && !estados.isEmpty()) {
				for(String estado : estados)
					pst.setString(i++, estado);
			}
			if(solPresuposto != null && solPresuposto.equals("true")) pst.setBoolean(i++, true);
			if(solPresuposto != null && solPresuposto.equals("false")) pst.setBoolean(i++, false);
			if(presuposto != null && !presuposto.isEmpty() && !presuposto.equals("-1")) pst.setString(i++, presuposto);
			if(factura != null && !factura.isEmpty() && !factura.equals("-1")) pst.setString(i++, factura);
			if(dataMenor != null) pst.setTimestamp(i++, dataMenor);
			if(dataMaior != null) pst.setTimestamp(i++, dataMaior);
			if(autor != null && !autor.isEmpty()) pst.setString(i++, autor);
			if(ver > 0) pst.setInt(i++, ver);
						
			res = pst.executeQuery();
			
					
			while(res.next()) {			
				incidencias.add(new Incidencia(res));
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

		return incidencias;
	}
	
	/**
	 * Modifica varios parámetros da incidencia que corresponda co Id que se lle pasa.
	 * 
	 * @param id - Identificador da incidencia que se quere modificar.
	 * @param codParte - valor que se quere dar a cod_parte. 0: non se modifica. -1: ponse a nulo.
	 * @param ot - valor que se quere dar a Orden de traballo. 0: non se modifica. -1: ponse a nulo.
	 * @param idInstalacion - instalación a que se lle asigna a incidencia. 0: non se modifica.
	 * @param zonaApartamento - valor que se lle da a zona_apartamento. NULL: non se modifica.
	 * @param descripcionCurta - valor que se lle da a descripcion_curta. NULL: non se modifica.
	 * @param observacions - valor que se lle asigna a observacions. NULL: non se modifica.
	 * @param estado - valor que se lle asigna a estado. NULL: non se modifica.
	 * @param solPresuposto - Ponse a true ou a false. NULL: non se modifica.
	 * @return - True en caso de que se modifica correctamente. False ocorreu algún erro.
	 */
	protected static boolean modificarIncidencia(int id, int codParte, int ot, int idInstalacion, String zonaApartamento,
			String descripcionCurta, String observacions, String estado, String solPresuposto) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET";
		query += (codParte > 0) ? " Cod_parte = ?," : "";
		query += (codParte < 0 ) ? " Cod_parte = NULL," : "";
		query += (ot > 0) ? " Orden_traballo = ?," : "";
		query += (ot < 0 ) ? " Orden_traballo = NULL," : "";
		query += (idInstalacion > 0) ? " Instalacion = ?," : "";
		query += (zonaApartamento != null && !zonaApartamento.equals("")) ? " Zona_apartamento = ?," : "";
		query += (descripcionCurta != null && !descripcionCurta.equals("")) ? " Descripcion_curta = ?," : "";
		query += (observacions != null && !observacions.equals("")) ? " Observacions = ?," : "";
		query += (estado != null && !estado.equals("")) ? " Estado = ?," : "";
		query += (solPresuposto != null && (solPresuposto.equals("true") || solPresuposto.equals("false"))) ? " Solicitase_presuposto = ?," : "";
		query += " Autor = autor";
		query += " WHERE Id = ?;";
		
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			if(codParte > 0) pst.setInt(i++, codParte);
			if(ot > 0) pst.setInt(i++, ot);
			if(idInstalacion > 0)  pst.setInt(i++, idInstalacion);
			if(zonaApartamento != null && !zonaApartamento.equals("")) pst.setString(i++, zonaApartamento);
			if(descripcionCurta != null && !descripcionCurta.equals("")) pst.setString(i++, descripcionCurta);
			if(observacions != null && !observacions.equals("")) pst.setString(i++, observacions);
			if(estado != null && !estado.equals("")) pst.setString(i++, estado);
			if(solPresuposto != null && solPresuposto.equals("true")) pst.setBoolean(i++, true);
			if(solPresuposto != null && solPresuposto.equals("false")) pst.setBoolean(i++, false);
			pst.setInt(i++, id);
	
			result = pst.executeUpdate();
			
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }

		return (result == 1);
	}
	

	/**
	 * Modifica o estado da incidencia
	 * @param id
	 * @param estado
	 * @return
	 */
	protected static boolean modificarEstado(int id, String estado) {
		return IncidenciaDAO.modificarIncidencia(id, 0, 0, 0, null, null, null, estado, null);
	}
	
	/**
	 * Modifica a factura en unha incidencia e cambia o estado da incidencia
	 * @param idIncidencia
	 * @param idFactura
	 */
	protected static boolean modifcarFacturaEstado(int idIncidencia, String idFactura, String estado) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET Factura = ?, Estado = ? WHERE Id = ?;";
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			logger.debug("Realizase a consulta: "+query);
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			pst.setString(i++, idFactura);
			pst.setString(i++, estado);
			pst.setInt(i, idIncidencia);
	
			result = pst.executeUpdate();
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }
		
		return (result == 1);
		
	}
	
	/**
	 * Modifica o presuposto en unha incidencia e cambia o estado da incidencia
	 * @param idIncidencia
	 * @param idPresuposto
	 */
	protected static boolean modifcarPresupostoEstado(int idIncidencia, String idPresuposto, String estado) {
		Connection conn = DBConnectionManager.getConnection();

		//Contruese a query segundo os datos proporcionados.
		String query = "UPDATE "+TABLENAME+" SET Presuposto = ?, Estado = ? WHERE Id = ?;";
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			logger.debug("Realizase a consulta: "+query);
			
			//Engádense os parámetros pasados a query.
			int i = 1;
			pst.setString(i++, idPresuposto);
			pst.setString(i++, estado);
			pst.setInt(i, idIncidencia);
	
			result = pst.executeUpdate();
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }
		
		return (result == 1);
	}

	
	/**
	 * Elimina unha incidencia
	 * 
	 * @param id
	 * @return
	 */
	protected static boolean delete(int id) {
		Connection conn = DBConnectionManager.getConnection();

		String query = "DELETE FROM "+TABLENAME+" WHERE Id = ?;";
		int result = -1;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setInt(1, id);
			result = pst.executeUpdate();
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
		
		return (result == 1);
	}

}
