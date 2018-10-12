package es.brudi.incidencias.comentarios;

import java.sql.Timestamp;
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
import es.brudi.incidencias.util.Util;

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
		
		if(!user.podeEngadirComentario()) {
			return Error.INSERTARCOMENTARIO_SENPERMISOS2.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.INSERTARCOMENTARIO_SENPERMISOS1.toJSONError();
		}
		
		int idC = ComentarioAccessor.crear(idIncidencia, user.getNome(), Comentario.ACCION_COMENTAR, tipo, texto);
		if(idC <= 0) {
			return Error.INSERTARCOMENTARIO_ERRORDB.toJSONError();
		}
		
		Timestamp data = Util.obterTimestampActual();
		Comentario comentario = new Comentario(idC, idIncidencia, user.getNome(), Comentario.ACCION_COMENTAR, Comentario.COMENTARIO_PUBLICO, texto, data);
				
		logger.debug("Creouse o comentario "+idC+" correctamente na incidencia: "+idIncidencia);
		
		ret = Mensaxe.INSERTARCOMENTARIO_OK.toJSONMensaxe();
		ret.put("Comentario", comentario);
		
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
		
		if(!user.podeVerComentario()) {
			return Error.OBTERCOMENTARIO_SENPERMISOS2.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.OBTERCOMENTARIO_SENPERMISOS1.toJSONError();
		}
		
		List<Comentario> comentarios = ComentarioAccessor.getByIdIncidencia(idIncidencia);

		if(comentarios == null) {
			return Error.OBTERCOMENTARIO_ERRORDB.toJSONError();
		}
				
		if(comentarios.isEmpty()) {
			return Error.OBTERCOMENTARIO_NONEXISTENAINC.toJSONError();
		}
						
		logger.debug("Obtiveronse "+comentarios.size()+" comentarios na incidencia: "+idIncidencia);
		
		ret = Mensaxe.OBTERCOMENTARIOSNC_OK.toJSONMensaxe();
		for(Comentario Comentario : comentarios) {
			if(user.podeVerComentarioTipo(Comentario.getTipo()))
				jsonComentarios.add(Comentario.toJson());
		}
			
		ret.put("Comentarios", jsonComentarios);
		
		return ret;
	}
	
}
