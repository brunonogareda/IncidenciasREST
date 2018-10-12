package es.brudi.incidencias.imaxes.db;

import java.util.List;

import es.brudi.incidencias.imaxes.Imaxe;
import es.brudi.incidencias.imaxes.Imaxe.Tipo;

public class ImaxeAccessor {
	
	private ImaxeAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	public static int getNextId() {
		return ImaxeDAO.getNextId();
	}
	
	/**
	 * Inserta unha nova i na base de datos.
	 * @param idIncidencia
	 * @param rutaFicheiro
	 * @param tipoFicheiro
	 * @param nome
	 * @param comentarios
	 * @param antesDespois
	 * @return
	 */
	public static int crear(int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String comentarios, Tipo antesDespois) {
		return ImaxeDAO.crear(idIncidencia, rutaFicheiro, tipoFicheiro, nome, comentarios, antesDespois.getBoolTipo());
	}
	
	/**
	 * Obten unha imaxe mediante o id.
	 * @param id
	 * @return
	 */
	public static Imaxe getById(int id) {
		return ImaxeDAO.getById(id);
	}

	/**
	 * Obten unha Listaxe de Imaxe que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Imaxe> getByIdIncidencia(int idIncidencia) {
		return ImaxeDAO.getByIdIncidencia(idIncidencia);
	}
	
	/**
	 * Modifica os parámetros da imaxe na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome da imaxe. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @param antesDespois - Antes/Despois da incidencia. NULL non o modifica
	 * @return
	 */
	public static boolean modificar(int id, String rutaFicheiro, String tipoFicheiro, String nome, String comentarios, String antesDespois) {
		return ImaxeDAO.modificar(id, rutaFicheiro, tipoFicheiro, nome, comentarios, antesDespois);
	}
}
