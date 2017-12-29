package es.brudi.incidencias.incidencias;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import org.json.simple.JSONObject;

import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.db.dao.InstalacionDAO;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
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

	@SuppressWarnings("unchecked")
	public JSONObject create(Usuario user, int cod_parte, int ot, int id_instalacion, String zona_apartamento,
			String descripcion_curta, String observacions, boolean sol_presuposto) {
		JSONObject ret = new JSONObject();
		
		LocalDate dataLocal = LocalDate.now(ZoneId.of("Europe/Madrid"));
		Date data = Date.valueOf(dataLocal);
		String estado = "Pendente";
		String autor = user.getNome();
		
		Instalacion inst = InstalacionDAO.getInstalacionById(id_instalacion);
		
		if(inst == null) { //Comprobamos se existe a instalación
			return Error.DEFAULT.toJSONError();
		}
		if(inst.getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		  user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é Brudi.
			return Error.DEFAULT.toJSONError();
		}
		
		//TODO - Comprobar permisos de solicitar presuposto.
		//TODO - Engadir comentario ao crear parte.
		
		if(!user.podeCrearIncidencia()) {
			return Error.USER_NOPERMISOS.toJSONError();
		}
		
		int id = IncidenciaDAO.create(cod_parte, ot, id_instalacion, zona_apartamento,
				descripcion_curta, observacions, estado, sol_presuposto, data, autor);
		
		if(id>=0) {
			Incidencia inc = IncidenciaDAO.getIncidenciaById(id);
			if(inc == null ) {
				return Error.DEFAULT.toJSONError();
			}
			ret = Mensaxe.DEFAULT.toJSONMensaxe();
			ret.put("Incidencia", inc.toJson());
		}
		else {
			ret = Error.DEFAULT.toJSONError();
		}

		return ret;
	}
	
}
