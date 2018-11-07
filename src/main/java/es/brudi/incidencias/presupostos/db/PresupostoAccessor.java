package es.brudi.incidencias.presupostos.db;

import java.util.ArrayList;
import java.util.List;

import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
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

	/**
	 * Devolve o presuposto buscando por Id. Sempres que o usuario que se pasa xestione a instalación correspondente a incidencia.
	 * @param id
	 * @param usuario
	 * @return
	 */
	public static Presuposto obterPorId(String id, Usuario user) {
		Presuposto presuposto = PresupostoDAO.obterPresupostoPorId(id);
		if(presuposto != null) {
			List<Incidencia> incidencias = IncidenciaAccessor.obterPorPresuposto(id);
			List<Integer> idIncidencias = new ArrayList<>();
			for(Incidencia inc : incidencias) {
				idIncidencias.add(inc.getId());
				if(!user.xestionaInstalacion(inc.getInstalacion().getId()))
					return null;
			}
			presuposto.setIdIncidencias(idIncidencias);
		}
		return presuposto;
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
	 * Inserta un novo presuposto.
	 * @param id
	 * @param ruta_ficheiro
	 * @param tipo_ficheiro
	 * @param aceptado
	 * @param comentarios
	 * @return Presuposto creado
	 */
	public static Presuposto crear(String id, String rutaFicheiro, String tipoFicheiro, String comentarios, boolean aceptado) {
		Presuposto presuposto = null;
		if (PresupostoDAO.crear(id, rutaFicheiro, tipoFicheiro, comentarios, aceptado))
			presuposto = new Presuposto(id, aceptado, rutaFicheiro, tipoFicheiro, comentarios);
		return presuposto;
	}
	
	/**
	 * Obten o presuposto solicitado coa incidencia a que pertence a instalación a que está asignado.
	 * @param id_presuposto
	 * @return Presuposto
	 */
	public static Presuposto obterPresupostoEInstalacionPorId(String idPresuposto) {
		return PresupostoDAO.obterPresupostoPorId(idPresuposto);
	}	

	/**
	 * Modifica os parámetros do presuposto na base de datos
	 * @param id
	 * @param ruta_ficheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipo_ficheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param comentarios - Comentarios do presuposto. NULL non o modifica.
	 * @param aceptado - Presuposto aceptado. NULL non o modifica
	 * @param aceptadoOrixinal - Estado de aceptado antes de modificar
	 * @return Presuposto modificado
	 */
	public static Presuposto modificar(String id, String rutaFicheiro, String tipoFicheiro, String comentarios, String aceptado, boolean aceptadoOrixinal) {
		Presuposto presuposto = null;
		boolean aceptadoNovo = (aceptado == null || aceptado.isEmpty()) ?  aceptadoOrixinal : Boolean.valueOf(aceptado);
		if(PresupostoDAO.modificar(id, rutaFicheiro, tipoFicheiro, comentarios, aceptado))
			presuposto = new Presuposto(id, aceptadoNovo, rutaFicheiro, tipoFicheiro, comentarios);
		return presuposto;
	}
}
