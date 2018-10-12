package es.brudi.incidencias.albarans.db;

import java.util.List;

import es.brudi.incidencias.albarans.Albaran;

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
	 * @return
	 */
	public static int crear(int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String proveedor, String numAlbaran, String comentarios) {
		return AlbaranDAO.crear(idIncidencia, rutaFicheiro, tipoFicheiro, nome, proveedor, numAlbaran, comentarios);

	}
	
	/**
	 * Obten un albaran mediante o id.
	 * @param id
	 * @return
	 */
	public static Albaran getById(int id) {
		return AlbaranDAO.getById(id);
	}

	/**
	 * Obten unha Listaxe de Albarans que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Albaran> getByIdIncidencia(int idIncidencia) {
		return getByIdIncidencia(idIncidencia);
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
	
}