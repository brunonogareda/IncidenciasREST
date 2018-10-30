package es.brudi.incidencias.comentarios;

import java.util.List;

import org.apache.log4j.Logger;

import es.brudi.incidencias.comentarios.db.ComentarioAccessor;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONArray;
import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Clase que xestiona as funcións relacionados cos comentarios.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionComentarios {
	
	private static Logger logger = Logger.getLogger(XestionComentarios.class);

	/**
	 * Crea un novo comentario na base de datos.
	 * @param user
	 * @param idIncidencia
	 * @param texto
	 * @return
	 */
	public JSONObject<String, Object> insertar(Usuario user, int idIncidencia, String texto, int tipo) {
		JSONObject<String, Object> ret;
		
		if(tipo < Comentario.TIPO_COMENTARIOS_MIN || tipo > Comentario.TIPO_COMENTARIO_MAX)
			tipo = Comentario.TIPO_COMENTARIOS_MIN;
		
		if(!user.podeEngadirComentario())
			return Error.INSERTARCOMENTARIO_SENPERMISOS.toJSONError();

		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia, user);
		if(inc == null)
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		
		Comentario comentario = ComentarioAccessor.crear(idIncidencia, user.getId(), Comentario.ACCION_COMENTAR, tipo, texto);
		if(comentario == null)
			return Error.INSERTARCOMENTARIO_ERRORDB.toJSONError();
		
		logger.debug("Creouse o comentario "+comentario.getId()+" correctamente na incidencia: "+idIncidencia);
		
		ret = Mensaxe.INSERTARCOMENTARIO_OK.toJSONMensaxe();
		ret.put("comentario", comentario);
		
		return ret;
	}

	/**
	 * Obter un grupo de comentarios de unha incidencia.
	 * @param user
	 * @param idIncidencia
	 * @return
	 */
	public JSONObject<String, Object> obterXIncidencia(Usuario user, int idIncidencia) {
		JSONObject<String, Object> ret;
		JSONArray<Object> jsonComentarios = new JSONArray<>();
		
		if(!user.podeVerComentario())
			return Error.OBTERCOMENTARIO_SENPERMISOS2.toJSONError();

		List<Comentario> comentarios = ComentarioAccessor.obterPorIdIncidencia(idIncidencia, user);
		if(comentarios == null)
			return Error.OBTERCOMENTARIO_ERRORDB.toJSONError();

		if(comentarios.isEmpty())
			return Error.OBTERCOMENTARIO_NONEXISTENAINC.toJSONError();
						
		logger.debug("Obtiveronse "+comentarios.size()+" comentarios na incidencia: "+idIncidencia);
		
		ret = Mensaxe.OBTERCOMENTARIOSNC_OK.toJSONMensaxe();
		for(Comentario Comentario : comentarios) {
			if(user.podeVerComentarioTipo(Comentario.getTipo()))
				jsonComentarios.add(Comentario.toJson());
		}
		ret.put("comentarios", jsonComentarios);
		return ret;
	}
	
}
