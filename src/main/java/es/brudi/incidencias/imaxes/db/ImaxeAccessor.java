package es.brudi.incidencias.imaxes.db;

import java.util.List;

import es.brudi.incidencias.imaxes.Imaxe;
import es.brudi.incidencias.imaxes.Imaxe.Tipo;
import es.brudi.incidencias.usuarios.Usuario;

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
	 * @return Imaxe nova
	 */
	public static Imaxe crear(int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String comentarios, Tipo antesDespois) {
		int idI = ImaxeDAO.crear(idIncidencia, rutaFicheiro, tipoFicheiro, nome, comentarios, antesDespois.getBoolTipo());
		Imaxe imaxe = null;
		if(idI >= 0)
			imaxe = new Imaxe(idI, idIncidencia, nome, antesDespois, rutaFicheiro, tipoFicheiro, comentarios);
		return imaxe;
	}
	
	/**
	 * Obten unha imaxe mediante o id.
	 * @param id
	 * @return
	 */
	public static Imaxe obterPorId(int id) {
		return ImaxeDAO.obterPorId(id);
	}

	/**
	 * Obten unha imaxe mediante o id sempre que corresponda a unha instalación que xestione o usuario.
	 * @param id
	 * @param user
	 * @return
	 */
	public static Imaxe obterPorId(int id, Usuario user) {
		Imaxe imaxe = ImaxeDAO.obterPorId(id);
		if(imaxe != null && !user.xestionaInstalacion(imaxe.getIdInstalacion()))
			return null;
		return imaxe;
	}
	
	/**
	 * Obten unha Listaxe de Imaxe que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Imaxe> obterPorIdIncidencia(int idIncidencia) {
		return ImaxeDAO.obterPorIdIncidencia(idIncidencia);
	}
	
	/**
	 * Obten unha Listaxe de Imaxe que teñan a Id_incidencia que se lle pase sempre que o usuario
	 * xestione a instalación a que pertence.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Imaxe> obterPorIdIncidencia(int idIncidencia, Usuario user) {
		List<Imaxe> imaxes = ImaxeDAO.obterPorIdIncidencia(idIncidencia);
		if(imaxes != null && (!imaxes.isEmpty() && !user.xestionaInstalacion(imaxes.get(0).getIdInstalacion())))
			imaxes.clear();
		return imaxes;
	}
	
	/**
	 * Modifica os parámetros da imaxe na base de datos
	 * @param id
	 * @param idIncidencia - Id da incidencia (Non se modifica)
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param nome - Nome da imaxe. NULL non o modifica.
	 * @param comentarios - Comentarios da imaxe. NULL non o modifica.
	 * @param tipo - Antes/Despois da incidencia. NULL non o modifica
	 * @param tipoOrixinal - Tipo orixinal da imaxe. (Non se modifica)
	 * @return Imaxe modificada
	 */
	public static Imaxe modificar(int id, int idIncidencia, String rutaFicheiro, String tipoFicheiro, String nome, String comentarios, String tipo, Tipo tipoOrixinal) {
		boolean mod = ImaxeDAO.modificar(id, rutaFicheiro, tipoFicheiro, nome, comentarios, tipo);
		Tipo tipoNovo = (tipo == null || tipo.isEmpty()) ?  tipoOrixinal : Tipo.getTipoFromString(tipo);
		if(mod)
			return new Imaxe(id, idIncidencia, nome, tipoNovo, rutaFicheiro, tipoFicheiro, comentarios);
		else
			return null;
		
	}
}
