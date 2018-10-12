package es.brudi.incidencias.instalacions;

import java.util.List;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.instalacions.db.InstalacionAccessor;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;

/**
 * 
 * Clase que xestiona as funcións relacionados coas Instalacions.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class XestionInstalacions {

	/**
	 * Devolve un listado de Instalacións, que pertenzcan o cliente con ese id. En caso de ser -1, consultará todas as instalacións.
	 * @param user - Usuario registrado
	 * @param idCliente - identificador do cliente
	 * @return Obxecto JSON coa resposta.
	 */
	public JSONObject<String, Object> getInstalacionsByCliente(Usuario user, int idCliente) {
		JSONObject<String, Object> ret;

		if(user.getCliente().getCodCliente()!=idCliente && user.getCliente().getCodCliente()!=0) {
			return Error.GETINSTALACIONS_SENPERMISOS.toJSONError();
		}
		
		List<Instalacion> instalacions = InstalacionAccessor.getInstalacionsByCliente(idCliente);
		
		if(instalacions != null) {
			if(!instalacions.isEmpty()) {
				ret = Mensaxe.GETINSTALACIONS_OK.toJSONMensaxe();
				ret.put("instalacions", instalacions);
			}
			else {
				ret = Error.GETINSTALACIONS_SENINSTALACIONS.toJSONError();
			}
		}
		else {
			ret = Error.GETINSTALACIONS_ERRORDB.toJSONError();
		}
		return ret;
	}
	
}
