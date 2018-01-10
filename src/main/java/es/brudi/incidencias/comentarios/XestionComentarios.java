package es.brudi.incidencias.comentarios;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.dao.ComentarioDAO;
import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;
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
	 * @param id_incidencia
	 * @param texto
	 * @return
	 */
	public JSONObject<String, Object> insertar(Usuario user, int id_incidencia, String texto, int tipo) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		if(tipo < Comentario.TIPO_COMENTARIOS_MIN || tipo > Comentario.TIPO_COMENTARIO_MAX)
			tipo = Comentario.TIPO_COMENTARIOS_MIN;
		
		if(!user.podeEngadirComentario()) {
			return Error.INSERTARCOMENTARIO_SENPERMISOS2.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.INSERTARCOMENTARIO_SENPERMISOS1.toJSONError();
		}
		
		int idC = ComentarioDAO.crear(id_incidencia, user.getNome(), Comentario.ACCION_COMENTAR, tipo, texto);
		if(idC <= 0) {
			return Error.INSERTARCOMENTARIO_ERRORDB.toJSONError();
		}
		
		Timestamp data = Util.obterTimestampActual();
		Comentario comentario = new Comentario(idC, id_incidencia, user.getNome(), Comentario.ACCION_COMENTAR, Comentario.COMENTARIO_PUBLICO, texto, data);
				
		logger.debug("Creouse o comentario "+idC+" correctamente na incidencia: "+id_incidencia);
		
		ret = Mensaxe.INSERTARCOMENTARIO_OK.toJSONMensaxe();
		ret.put("Comentario", comentario);
		
		return ret;
	}

	/**
	 * Obter un grupo de comentarios de unha incidencia.
	 * @param user
	 * @param id_incidencia
	 * @return
	 */
	public JSONObject<String, Object> obterXIncidencia(Usuario user, int id_incidencia) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		JSONArray<Object> jsonComentarios = new JSONArray<Object>();
		
		if(!user.podeVerComentario()) {
			return Error.OBTERCOMENTARIO_SENPERMISOS2.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.OBTERCOMENTARIO_SENPERMISOS1.toJSONError();
		}
		
		ArrayList<Comentario> Comentarios = ComentarioDAO.getByIdIncidencia(id_incidencia);

		if(Comentarios == null) {
			return Error.OBTERCOMENTARIO_ERRORDB.toJSONError();
		}
				
		if(Comentarios.size() <= 0) {
			return Error.OBTERCOMENTARIO_NONEXISTENAINC.toJSONError();
		}
						
		logger.debug("Obtiveronse "+Comentarios.size()+" comentarios na incidencia: "+id_incidencia);
		
		ret = Mensaxe.OBTERCOMENTARIOSNC_OK.toJSONMensaxe();
		for(Comentario Comentario : Comentarios) {
			if(user.podeVerComentarioTipo(Comentario.getTipo()))
				jsonComentarios.add(Comentario.toJson());
		}
			
		ret.put("Comentarios", jsonComentarios);
		
		return ret;
	}
	
}
