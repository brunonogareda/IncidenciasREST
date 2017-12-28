package es.brudi.incidencias.instalacions;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import es.brudi.incidencias.db.dao.InstalacionDAO;
import es.brudi.incidencias.error.Error;
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
	@SuppressWarnings("unchecked")
	public JSONObject getInstalacionsByCliente(Usuario user, int idCliente) {
		JSONObject ret = new JSONObject();
		JSONArray jsonInstalacions = new JSONArray();

		if(user.getCliente().getCod_cliente()!=idCliente && user.getCliente().getCod_cliente()!=0) {
			return Error.GETINSTALACIONS_SENPERMISOS.toJSONError();
		}
		
		ArrayList<Instalacion> Instalacions = InstalacionDAO.getInstalacionsByCliente(idCliente);
		
		if(Instalacions != null) {
			if(Instalacions.size()>0) {
				for(Instalacion inst : Instalacions) {
					jsonInstalacions.add(inst.toJson());
				}
				ret = Mensaxe.GETINSTALACIONS_OK.toJSONMensaxe();
				ret.put("instalacions", jsonInstalacions);
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
