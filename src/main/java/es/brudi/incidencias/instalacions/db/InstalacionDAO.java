package es.brudi.incidencias.instalacions.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.db.XestorConexions;
import es.brudi.incidencias.grupos.db.GrupoDAO;
import es.brudi.incidencias.instalacions.Instalacion;
import es.brudi.incidencias.usuarios.db.UsuarioDAO;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de
 * Instalacións.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class InstalacionDAO {

	private InstalacionDAO() {
		throw new IllegalStateException("Utility class");
	}

	public static final String TABLENAME = "Instalacions";
	private static Logger logger = Logger.getLogger(InstalacionDAO.class);

	/**
	 * @param id
	 *            de instalación.
	 * @return Devolve o obxecto de instalación que corresponde co id.
	 */
	protected static Instalacion obterInstalacionPorId(int id) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM " + TABLENAME + " WHERE Id=?;";
		ResultSet res = null;
		Instalacion instalacion = null;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setInt(1, id);
			res = pst.executeQuery();

			if (res.next())
				instalacion = new Instalacion(res);

		} catch (SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}

		return instalacion;
	}

	/**
	 * Obten todas as instalación que pertencen o cliente que se lle pasa. Se se lle
	 * pasa un -1, obtéñense todas as instalacións
	 * 
	 * @param idCliente
	 *            Id do cliente do que se quere obter as instalacions
	 * @return Lista de instalacions
	 */
	protected static List<Instalacion> obterInstalacionsPorCliente(int idCliente) {
		Connection conn = null;
		try {
			conn = XestorConexions.getConexion();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String query = "SELECT * FROM " + TABLENAME + " WHERE Cod_cliente=?;";
		ResultSet res = null;
		List<Instalacion> instalacions = new ArrayList<>();

		if (idCliente < 0) // Se o idCliente é -1, eliminamos a condición e executase directamente.
			query = "SELECT * FROM " + TABLENAME + ";";

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			if (idCliente >= 0) {
				pst.setInt(1, idCliente);
			}

			logger.debug("Realizase a consulta: " + query);

			res = pst.executeQuery();

			while (res.next()) {
				instalacions.add(new Instalacion(res));
			}
		} catch (SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}

		return instalacions;
	}

	/**
	 * obten todas as instalacións xestionas por un usuario
	 * 
	 * @param id Identifiador do usuario.
	 * @return
	 */
	public static List<Instalacion> obterInstalacionsXestionadasUsuario(int idUsuario) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT I.* FROM " + TABLENAME	+ " AS I ON I.Id=GI.Instalacion INNER JOIN " + UsuarioDAO.TABLENAME_INSTALACIONS
				+ " AS UI WHERE UI.Id_usuario = ?;";
		ResultSet res = null;
		List<Instalacion> instalacions = new ArrayList<>();

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setInt(1, idUsuario);

			logger.debug("Realizase a consulta: " + query);

			res = pst.executeQuery();

			while (res.next()) {
				instalacions.add(new Instalacion(res));
			}
		} catch (SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}

		return instalacions;
	}
	
	
	/**
	 * obten todas as instalacións xestionadas por un grupo
	 * 
	 * @param id Identifiador do grupo
	 * @return
	 */
	public static List<Instalacion> obterInstalacionsXestionadasGrupo(int idGrupo) {
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT I.* FROM " + TABLENAME + " AS I INNER JOIN " + GrupoDAO.TABLENAME_INSTALACIONS
				+ " AS GI ON I.Id=GI.Instalacion WHERE GI.Id_grupo = ?;";
		ResultSet res = null;
		List<Instalacion> instalacions = new ArrayList<>();

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setInt(1, idGrupo);

			logger.debug("Realizase a consulta: " + query);

			res = pst.executeQuery();

			while (res.next()) {
				instalacions.add(new Instalacion(res));
			}
		} catch (SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			try {
				if (res != null)
					res.close();
			} catch (SQLException e) {
				logger.error("Excepción cerrando ResultSet: ", e);
			}
		}
		return instalacions;
	}

}
