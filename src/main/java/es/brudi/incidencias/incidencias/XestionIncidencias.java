package es.brudi.incidencias.incidencias;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import es.brudi.incidencias.clientes.Cliente;
import es.brudi.incidencias.db.dao.ClienteDAO;
import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.permisos.Permiso;
import es.brudi.incidencias.usuarios.Usuario;

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
				
		//TODO - Comprobar que a instalación pertence ao cliente do usuario que crea a incidencia.
		//TODO - Comprobar permisos de solicitar presuposto.
		
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
