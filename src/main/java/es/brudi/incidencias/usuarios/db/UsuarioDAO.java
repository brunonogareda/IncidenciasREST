package es.brudi.incidencias.usuarios.db;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import es.brudi.incidencias.clientes.Cliente;
import es.brudi.incidencias.clientes.db.ClienteAccessor;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.grupos.Grupo;
import es.brudi.incidencias.grupos.db.GrupoAccessor;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.Variables;

/**
 * 
 * Clase que xestiona as operación coa base de datos relacionados coa táboa de Usuarios.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class UsuarioDAO {

	private static final String TABLENAME = "Usuarios";
	public static final String TABLENAME_INSTALACIONS = "Usuario_instalacion";
	private static Logger logger = Logger.getLogger(UsuarioDAO.class);
	
	private UsuarioDAO() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return Número de tuplas na táboa de Usuarios.
	 */
	protected static int count() {
		    	
		Connection conn = DBConnectionManager.getConnection();
		int ret = 0;
		ResultSet res = null;
		
		String query = "SELECT COUNT(*) AS count FROM "+TABLENAME;

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
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
				logger.error("Error cerrando ResultSet: ", e);
			}
		}
		return ret;
	}
	
	/**
	 * Método se comproba que o usuario que se lle pasa existe e coincide o contrasinal da base de datos co que se lle pasa.
	 * @param username
	 * @param password
	 * @return Devolve o Nome completo do Usuario.
	 */
	protected static String comprobarUsuario(String username, String password) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = Variables.DB_SELECT_ALL+TABLENAME+" WHERE Usuario=?;";
		ResultSet res = null;
		String nome = null;
		
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, username);
			res = pst.executeQuery();
			
			if(res.next()) {
				String pass = res.getString("Password");
				String nomeCompleto = res.getString("Nome_completo");
				
				//Encriptamos o controsinal recibido con SHA-256. Xa que así se almacena da BD.
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
				String passwordHash = DatatypeConverter.printHexBinary(hash);
				
				//Se a pass coincide, insertamos o nomeCompleto. En caso de ser nulo devolvemos un string baleiro.
				if(passwordHash.equalsIgnoreCase(pass))
					nome = (nomeCompleto == null) ? "" : nomeCompleto;
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
				logger.error("Error cerrando ResultSet: ", e);
			}
		}
		return nome;
	}
	
	/**
	 * Obten as instalacions que xestiona un usuario en concreto
	 * @param id de usuario
	 * @return Lista de identificadores de instalacions.
	 */
	private static ArrayList<Integer> getInstalacionsXestionadas(int id) {
		Connection conn = DBConnectionManager.getConnection();
		String query = Variables.DB_SELECT_ALL+TABLENAME_INSTALACIONS+" WHERE Id_usuario=?;";
		ArrayList<Integer> instalacions = new ArrayList<>();
		ResultSet res = null;
		
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			
			pst.setInt(1, id);
			
			res = pst.executeQuery();
			
			while(res.next()) {
				instalacions.add(res.getInt("Instalacion"));
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
				logger.error("Error cerrando ResultSet: ", e);
			}
		 }
		
		return instalacions;
	}

	/**
	 * Obten un usuario da base de datos a partir do nome de usuario.
	 * @param username
	 * @return
	 */
	public static Usuario obterUsuario(String username) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = Variables.DB_SELECT_ALL+TABLENAME+" WHERE Usuario=?;";
		Usuario ret = null;
		ResultSet res = null;
		
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			pst.setString(1, username);
			res = pst.executeQuery();
			if(res.next()) {
				int id = res.getInt("Id");
				String user = res.getString("Usuario");
				int idCliente = res.getInt("Cod_cliente");
				int idGrupo = res.getInt("Id_grupo");
				String email = res.getString("Email");
				String nome = res.getString("Nome_completo");
				String permisos = res.getString("Permisos");
				ArrayList<Integer> instalacions = new ArrayList<>();
				instalacions.addAll(UsuarioDAO.getInstalacionsXestionadas(id));
							
				Cliente c = ClienteAccessor.getClienteById(idCliente);
				Grupo g = GrupoAccessor.getGrupoById(idGrupo);
				ret = new Usuario(id, user, c, g, email, nome, permisos, instalacions);
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
				logger.error("Error pechando ResultSet: ", e);
			}
		 }
		return ret;
	}
	
	/**
	 * Método para cambiar o email do usuario.
	 * @param newMail - Novo email do ususario.
	 * @param id - Id do usuario.
	 * @return boleano de se se cambiou o email correctamente.
	 */
	public static boolean cambiarEmail(String newMail, int id) {
		int res = -1;
		Connection conn = DBConnectionManager.getConnection();
		String query = "UPDATE "+TABLENAME+" SET Email=? WHERE Id=?;";
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			pst.setString(1, newMail);
			pst.setInt(2, id);
			
			res = pst.executeUpdate();
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }
		return (res == 1);
	}
	
	/**
	 * Método para cambiar o contrasinal do usuario
	 * @param newPass - Novo contrasinal do usuario.
	 * @param id - Id do usuario
	 * @return boleano de se se cambiou o contrasinal correctamente.
	 */
	public static boolean cambiarPass(String newPass, int id) {
		boolean ret = false;
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "UPDATE "+TABLENAME+" SET Password=? WHERE Id=?;";
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			logger.debug("Realizase a consulta: "+query);
			
			//Encriptamos o controsinal recibido con SHA-256.
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(newPass.getBytes(StandardCharsets.UTF_8));
			String passwordHash = DatatypeConverter.printHexBinary(hash);
			
			pst.setString(1, passwordHash);
			pst.setInt(2, id);
			
			int res = pst.executeUpdate();
			
			if(res == 1) {
				return true;
			}
			return ret;
		 }
		catch(SQLException se) {
			logger.error("SQLException: " + se.getMessage());
			logger.error("SQLState: " + se.getSQLState());
			logger.error("VendorError: " + se.getErrorCode());
		 }
		catch(Exception e) {
			logger.error("Exception: ", e);
		 }
		return ret;
	}
}
