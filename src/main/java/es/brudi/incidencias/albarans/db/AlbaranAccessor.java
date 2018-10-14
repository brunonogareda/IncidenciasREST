package es.brudi.incidencias.albarans.db;

import java.util.List;

import es.brudi.incidencias.albarans.Albaran;
import es.brudi.incidencias.usuarios.Usuario;

public class AlbaranAccessor {

	private AlbaranAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	public static int getNextId() {
		return AlbaranDAO.getNextId();
	}
	
	/**
	 * Inserta un novo albara.
	 * @param idIncidencia
	 * @param rutaFicheiro
	 * @param tipoFicheiro
	 * @param nome
	 * @param proveedores
	 * @param numAlbaran
	 * @param comentarios
	 * @return un obxecto Albaran.
	 */
	public static Albaran crear(int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String proveedor, String numAlbaran, String comentarios) {
		int idA = AlbaranDAO.crear(idIncidencia, rutaFicheiro, tipoFicheiro, nome, proveedor, numAlbaran, comentarios);
		if(idA > 0)
			return new Albaran(idA, idIncidencia, nome, proveedor, numAlbaran, comentarios, rutaFicheiro, tipoFicheiro);
		else
			return null;

	}
	
	/**
	 * Obten un albaran mediante o id.
	 * @param id
	 * @return
	 */
//	public static Albaran obterPorId(int id) {
//		return AlbaranDAO.obterPorId(id);
//	}

	/**
	 * Devolve o albarán buscando por Id. Sempre que o usuario que se pasa xestione a instalación correspondente a incidencia.
	 * @param id
	 * @param usuario
	 * @return
	 */
	public static Albaran obterPorId(int id, Usuario usuario) {
		Albaran albaran = AlbaranDAO.obterAlbaranEInstalacionPorId(id);
		if(albaran != null && !usuario.xestionaInstalacion(albaran.getInstalacion()))
			albaran = null;
		
		return albaran;
	}
	
	/**
	 * Obten unha Listaxe de Albarans que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
//	public static List<Albaran> obterPorIdIncidencia(int idIncidencia) {
//		return AlbaranDAO.obterPorIdIncidencia(idIncidencia);
//	}
//	
	/**
	 * Obten unha Listaxe de Albarans que teñan a Id_incidencia que se lle pase.
	 * Se o usuario non xestiona a instalación a que corresponde devolve unha lista baleira.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Albaran> obterPorIdIncidencia(int idIncidencia, Usuario usuario) {
		List<Albaran> ret = AlbaranDAO.obterPorIdIncidencia(idIncidencia);
		if(!ret.isEmpty() && !usuario.xestionaInstalacion(ret.get(0).getInstalacion()))
			ret.clear();
		
		return ret;
	}
	
	/**
	 * Modifica os parámetros do albarán na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome do albaran. NULL non o modifica.
	 * @param proveedor - Nome do proveedor. NULL non o modifica.
	 * @param numAlbaran - Número do albarán. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @return
	 */
	public static boolean modificar(int id, String rutaFicheiro, String tipoFicheiro, String nome, String proveedor, String numAlbaran, String comentarios) {
		return AlbaranDAO.modificar(id, rutaFicheiro, tipoFicheiro, nome, proveedor, numAlbaran, comentarios);
	}

	/**
	 * Borra o albarán da base de datos.
	 * @param id - Identificador do albarán
	 * @return
	 */
	public static boolean eliminar(int id) {
		return AlbaranDAO.eliminar(id);
	}
	
}