package es.brudi.incidencias.usuarios.db;

import es.brudi.incidencias.usuarios.Usuario;

public class UsuarioAccessor {

	private UsuarioAccessor() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return Número de tuplas na táboa de Usuarios.
	 */
	public static int count() {
		return UsuarioDAO.count();
	}
	
	/**
	 * Método se comproba que o usuario que se lle pasa existe e coincide o contrasinal da base de datos co que se lle pasa.
	 * @param username
	 * @param password
	 * @return Devolve un obxecto usuario.
	 */
	public static Usuario comprobarUsuario(String username, String password) {
		return UsuarioDAO.comprobarUsuario(username, password);
	}
	
	/**
	 * Obten un usuario da base de datos a partir do nome de usuario.
	 * @param username
	 * @return
	 */
	public static Usuario obterUsuario(String username) {
		return UsuarioDAO.obterUsuario(username);
	}
	
	/**
	 * Método para cambiar o email do usuario.
	 * @param newMail - Novo email do ususario.
	 * @param id - Id do usuario.
	 * @return boleano de se se cambiou o email correctamente.
	 */
	public static boolean cambiarEmail(String newMail, int id) {
		return UsuarioDAO.cambiarEmail(newMail, id);
	}
	
	/**
	 * Método para cambiar o contrasinal do usuario
	 * @param newPass - Novo contrasinal do usuario.
	 * @param id - Id do usuario
	 * @return boleano de se se cambiou o contrasinal correctamente.
	 */
	public static boolean cambiarPass(String newPass, int id) {
		return UsuarioDAO.cambiarPass(newPass, id);
	}
	
}
