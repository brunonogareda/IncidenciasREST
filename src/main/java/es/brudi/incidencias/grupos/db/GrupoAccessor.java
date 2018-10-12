package es.brudi.incidencias.grupos.db;

import es.brudi.incidencias.grupos.Grupo;

public class GrupoAccessor {
	
	private GrupoAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return Número de tuplas na táboa de Grupos.
	 */
	public static int count() {
		return GrupoDAO.count();
	}
	
	/**
	 * @param id de grupo
	 * @return Devolve o obxecto grupo que corresponde co Id que se lle pasou.
	 */
	public static Grupo getGrupoById(int id) {
		return GrupoDAO.getGrupoById(id);
	}
}
