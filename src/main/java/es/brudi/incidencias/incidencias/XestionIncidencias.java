package es.brudi.incidencias.incidencias;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

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
	
	@SuppressWarnings("unchecked")
	public JSONObject create(Usuario user, int cod_parte, int ot, int id_instalacion, String zona_apartamento,
			String descripcion_curta, String observacions, boolean sol_presuposto) {
		JSONObject ret = new JSONObject();
		
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
		  user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é Brudi.
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
	
}
