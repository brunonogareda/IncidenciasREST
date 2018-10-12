package es.brudi.incidencias.presupostos.db;

import es.brudi.incidencias.presupostos.Presuposto;
import es.brudi.incidencias.usuarios.Usuario;

/**
 * 
 * Realiza peticións contra o DAO e procesa os datos.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Agosto - 2018
 *
 */
public class PresupostoAccessor {
	
	private PresupostoAccessor() {
		throw new IllegalStateException("Utility class");
	}

	public static Presuposto obterPresupostoPorId(String id, Usuario usuario) {
		Presuposto presuposto = null;
		
		presuposto = PresupostoDAO.obterPresupostoEInstalacionPorId(id);
		
		if(presuposto != null && usuario.xestionaInstalacion(presuposto.getInstalacion()))
			return presuposto;
		
		return presuposto;
	}
	
	/**
	 * Inserta un novo presuposto.
	 * @param id
	 * @param ruta_ficheiro
	 * @param tipo_ficheiro
	 * @param aceptado
	 * @param comentarios
	 * @return
	 */
	public static boolean crear(String id, String rutaFicheiro, String tipoFicheiro, String comentarios, boolean aceptado) {
		return PresupostoDAO.crear(id, rutaFicheiro, tipoFicheiro, comentarios, aceptado);
	}
	
	/**
	 * Obten un presuposto mediante o id.
	 * @param id
	 * @return
	 */
	public static Presuposto obterPorId(String id) {
		return PresupostoDAO.obterPorId(id);
	}
	
	/**
	 * Obten o presuposto solicitado coa incidencia a que pertence a instalación a que está asignado.
	 * @param id_presuposto
	 * @return Presuposto
	 */
	public static Presuposto obterPresupostoEInstalacionPorId(String idPresuposto) {
		return PresupostoDAO.obterPresupostoEInstalacionPorId(idPresuposto);
	}	

	/**
	 * Modifica os parámetros do presuposto na base de datos
	 * @param id
	 * @param ruta_ficheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipo_ficheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param comentarios - Comentarios do presuposto. NULL non o modifica.
	 * @param aceptado - Presuposto aceptado. NULL non o modifica
	 * @return
	 */
	public static boolean modificar(String id, String rutaFicheiro, String tipoFicheiro, String comentarios, String aceptado) {
		return PresupostoDAO.modificar(id, rutaFicheiro, tipoFicheiro, comentarios, aceptado);
	}
}
