package es.brudi.incidencias.comentarios.db;

import java.util.List;

import es.brudi.incidencias.comentarios.Comentario;

public class ComentarioAccessor {

	private ComentarioAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Inserta un novo comentario.
	 * 
	 * @param idIncidencia - Incidencia coque se corresponde o comentario
	 * @param autor - Autor do comentario
	 * @param accion - Acción realizada para que se inserte o comentario.
	 * @param tipo - Tipo de comentario.
	 * @param texto - Texto do comentario.
	 * @param data - Data do comentario.
	 * @return - Id do novo comentario. -1 existiuu algún error ao insertar.
	 */
	public static int crear(int idIncidencia, String autor, String accion, int tipo, String texto) {
		return ComentarioDAO.crear(idIncidencia, autor, accion, tipo, texto);
	}
	
	/**
	 * Inserta un novo comentario.
	 * 
	 * @param idIncidencia - Incidencia coque se corresponde o comentario
	 * @param autor - Autor do comentario
	 * @param accion - Acción realizada para que se inserte o comentario.
	 * @param tipo - Tipo de comentario.
	 * @param texto - Texto do comentario.
	 * @param data - Data do comentario.
	 * @return - Id do novo comentario. -1 existiuu algún error ao insertar.
	 */
	public static int crear(int idIncidencia, String autor, String accion, int tipo, int texto) {
		return ComentarioAccessor.crear(idIncidencia, autor, accion, tipo, String.valueOf(texto));
	}
	

	/**
	 * Obten unha Listaxe de Comentarios que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
	public static List<Comentario> getByIdIncidencia(int idIncidencia) {
		return ComentarioDAO.getByIdIncidencia(idIncidencia);
	}
	
	
}
