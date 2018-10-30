package es.brudi.incidencias.comentarios.db;

import java.sql.Timestamp;
import java.util.List;

import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.Util;

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
	 * @return - O obxecto comentario creado. Null se ocorreu algún erro
	 */
	public static Comentario crear(int idIncidencia, int autor, String accion, int tipo, String texto) {
		int idC = ComentarioDAO.crear(idIncidencia, autor, accion, tipo, texto);
		Comentario comentario = null;
		if(idC > 0) { 
			Timestamp data = Util.obterTimestampActual();
			comentario = new Comentario(idC, idIncidencia, autor, accion, tipo, texto, data);
		}
		return comentario;
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
	 * @return - O obxecto comentario creado. Null se ocorreu algún erro
	 */
	public static Comentario crear(int idIncidencia, int autor, String accion, int tipo, int texto) {
		return ComentarioAccessor.crear(idIncidencia, autor, accion, tipo, String.valueOf(texto));
	}
	

	/**
	 * Obten unha Listaxe de Comentarios que teñan a Id_incidencia que se lle pase.
	 * 
	 * @param idIncidencia
	 * @return
	 */
//	public static List<Comentario> obterPorIdIncidencia(int idIncidencia) {
//		return ComentarioDAO.getByIdIncidencia(idIncidencia);
//	}
	
	/**
	 * Obten unha Listaxe de Comentarios que teñan a Id_incidencia que se lle pase.
	 * Se o usuario non xestiona a instalación a que pertence, devolve unha lista baleira.
	 * @param idIncidencia
	 * @param user
	 * @return
	 */
	public static List<Comentario> obterPorIdIncidencia(int idIncidencia, Usuario user) {
		List<Comentario> ret = ComentarioDAO.obtrePorIdIncidencia(idIncidencia);
		if(!ret.isEmpty() && !user.xestionaInstalacion(ret.get(0).getInstalacion()))
			ret.clear();
		
		return ret;
	}
	
	
}
