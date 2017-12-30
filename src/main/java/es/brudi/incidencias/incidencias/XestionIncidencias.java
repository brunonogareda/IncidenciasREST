package es.brudi.incidencias.incidencias;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.db.dao.ComentarioDAO;
import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.db.dao.InstalacionDAO;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.Util;
import es.brudi.incidencias.instalacions.Instalacion;

/**
 * 
 * Clase que xestiona as funcións relacionados coas incidencias.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class XestionIncidencias {
	
	private static Logger logger = Logger.getLogger(XestionIncidencias.class);
	
	/**
	 * Crea unha nova incidencia.
	 * @param user - Usuario que realiza a petición.
	 * @param cod_parte
	 * @param ot
	 * @param id_instalacion
	 * @param zona_apartamento
	 * @param descripcion_curta
	 * @param observacions
	 * @param sol_presuposto
	 * @return
	 */
	public JSONObject<String, Object> create(Usuario user, int cod_parte, int ot, int id_instalacion, String zona_apartamento,
			String descripcion_curta, String observacions, boolean sol_presuposto) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		Calendar agora = Calendar.getInstance();
		agora.setTimeZone(Util.timeZone);
		Timestamp data = new Timestamp(agora.getTimeInMillis());
		String estado = "Pendente";
		String autor = user.getNome();
		
		Instalacion inst = InstalacionDAO.getInstalacionById(id_instalacion);
		
		if(inst == null) { //Comprobamos se existe a instalación
			return Error.CREATEINCIDENCIA_NONEXISTEINST.toJSONError();
		}
		if(inst.getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		  user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREATEINCIDENCIA_INSTPERMISOS.toJSONError();
		}
		
		if(!user.podeMarcarSolPresuposto()) { //Comprobamos permisos de solicitar presuposto.
			return Error.USER_NOPERMISOS.toJSONError();
		}
		if(!user.podeCrearIncidencia()) { //Comprobamos permisos de creación
			return Error.USER_NOPERMISOS.toJSONError();
		}
		
		int id = IncidenciaDAO.crear(cod_parte, ot, id_instalacion, zona_apartamento,
				descripcion_curta, observacions, estado, sol_presuposto, data, autor);
		
		if(id < 0) {
			return Error.CREATEINCIDENCIA_ERRORCREANDO.toJSONError();
		}

		Incidencia inc = IncidenciaDAO.getIncidenciaById(id);
		
		if(inc == null ) {
			logger.error("Creouse a incidencia, pero occoreu un erro o recuperala da base de datos.");
			return Error.CREATEINCIDENCIA_ERRORCREANDO.toJSONError();
		}
		
		logger.debug("Creouse a incidencia correctamente: "+id);
		
		int id_c = ComentarioDAO.crear(id, autor, Comentario.ACCION_CREAR, Comentario.COMENTARIO_PUBLICO, "", data);
		
		if(id_c < 0) {
			return Error.DEFAULT.toJSONError();
		}
			
		ret = Mensaxe.DEFAULT.toJSONMensaxe();
		ret.put("Incidencia", inc.toJson());
		
		return ret;
	}

	/**
	 * Obten unha incidencia utilizando o identificiador.
	 * @param user - Usuario que solicita a petición.
	 * @param id - Identificación da incidencia que se desexa obter.
	 * @return
	 */
	public JSONObject<String, Object> getById(Usuario user, int id) {
		JSONObject<String,Object> ret = new JSONObject<String,Object>();
						
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id);
		
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
				  user.getCliente().getCod_cliente() != 0) { //Comprobamos se a incidencia é do usuario ou se o usuario é o cliente 0.
			return Error.OBTERINCIDENCIA_SENPERMISOS.toJSONError();
		}
		
		if(!user.podeVerIncidencia()) { //Comprobamos se o usuario ten permisos para ver incidencias.
			if(!(user.podeVerIncidenciaPropia() && inc.getAutor() == user.getNome())) {
				return Error.USER_NOPERMISOS.toJSONError();
			}
		}
		
		logger.debug("Obtense a incidencia: "+id);
		
		ret = Mensaxe.DEFAULT.toJSONMensaxe();
		ret.put("Incidencia", inc.toJson());

		return ret;
	}
	
}
