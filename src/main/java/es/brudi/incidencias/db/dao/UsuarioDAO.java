package es.brudi.incidencias.db.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import es.brudi.incidencias.clientes.Cliente;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.grupos.Grupo;
import es.brudi.incidencias.usuarios.Usuario;

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

	private final static String TABLENAME = "Usuarios";
		   
	private static Logger logger = Logger.getLogger(UsuarioDAO.class);
	
	/**
	 * @return Número de tuplas na táboa de Usuarios.
	 */
	public static int count() {
		    	
		Connection conn = DBConnectionManager.getConnection();
		    	
		String query = "SELECT COUNT(*) AS count FROM "+TABLENAME;
		PreparedStatement counter;
		try
		{
			logger.debug("Realizase a consulta: "+query);
			
		    counter = conn.prepareStatement(query);
		    ResultSet res = counter.executeQuery();
		    res.next();
		    return res.getInt("count");
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
	 * Método se comproba que o usuario que se lle pasa existe e coincide o contrasinal da base de datos co que se lle pasa.
	 * @param username
	 * @param password
	 * @return Devolve un obxecto usuario.
	 */
	public static Usuario comprobar_usuario(String username, String password) {
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "SELECT * FROM "+TABLENAME+" WHERE Usuario=?;";
		PreparedStatement usuario;
		
		try
		 {
			
			usuario = conn.prepareStatement(query);
			usuario.setString(1, username);
			
			ResultSet res = usuario.executeQuery();
			
			if(res.next()) {
				int id = res.getInt("Id");
				String user = res.getString("Usuario");
				String pass = res.getString("Password");
				int id_cliente = res.getInt("Cod_cliente");
				int id_grupo = res.getInt("Id_grupo");
				String email = res.getString("Email");
				String nome = res.getString("Nome_completo");
				String permisos = res.getString("Permisos");
				
				//Encriptamos o controsinal recibido con SHA-256. Xa que así se almacena da BD.
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
				String password_hash = DatatypeConverter.printHexBinary(hash);
								
				if(username.equals(user) && password_hash.equalsIgnoreCase(pass)) {
					
					Cliente c = ClienteDAO.getClienteById(id_cliente);
					Grupo g = GrupoDAO.getGrupoById(id_grupo);
					
					Usuario ret = new Usuario(id, user, c, g, email, nome, permisos);
					
					return ret;
	
				}
				else {
					return null;
				}
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
			logger.error("Exception: ", e);
		 }
		
		return null;
	}
	
	/**
	 * Método para cambiar o email do usuario.
	 * @param new_mail - Novo email do ususario.
	 * @param id - Id do usuario.
	 * @return boleano de se se cambiou o email correctamente.
	 */
	public static boolean cambiarEmail(String new_mail, int id) {
		boolean ret = false;
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "UPDATE "+TABLENAME+" SET Email=? WHERE Id=?;";
		PreparedStatement update;
		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			update = conn.prepareStatement(query);
			update.setString(1, new_mail);
			update.setInt(2, id);
			
			int res = update.executeUpdate();
			
			update.close();
			
			if(res == 1) {
				return true;
			}
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
			logger.error("Exception: ", e);
		 }
		
		return ret;
	}
	
	/**
	 * Método para cambiar o contrasinal do usuario
	 * @param new_pass - Novo contrasinal do usuario.
	 * @param id - Id do usuario
	 * @return boleano de se se cambiou o contrasinal correctamente.
	 */
	public static boolean cambiarPass(String new_pass, int id) {
		boolean ret = false;
		
		Connection conn = DBConnectionManager.getConnection();
		String query = "UPDATE "+TABLENAME+" SET Password=? WHERE Id=?;";
		PreparedStatement update;
		try
		 {
			
			logger.debug("Realizase a consulta: "+query);
			
			//Encriptamos o controsinal recibido con SHA-256.
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(new_pass.getBytes(StandardCharsets.UTF_8));
			String password_hash = DatatypeConverter.printHexBinary(hash);
			
			
			update = conn.prepareStatement(query);
			update.setString(1, password_hash);
			update.setInt(2, id);
			
			int res = update.executeUpdate();
			
			update.close();
			
			if(res == 1) {
				return true;
			}
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
			logger.error("Exception: ", e);
		 }
		
		return ret;
	}
	
}
