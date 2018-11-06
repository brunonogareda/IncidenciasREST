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
	public JSONObject<String, Object> obterInstalacionsPorCliente(Usuario user, int idCliente) {
		JSONObject<String, Object> ret;

		List<Instalacion> instalacions = InstalacionAccessor.obterInstalacionsPorCliente(idCliente, user);
		
		if(instalacions == null)
			return Error.GETINSTALACIONS_ERRORDB.toJSONError();
		
		if(instalacions.isEmpty())
			return Error.GETINSTALACIONS_SENINSTALACIONS.toJSONError();
		
		ret = Mensaxe.GETINSTALACIONS_OK.toJSONMensaxe();
		ret.put("instalacions", instalacions);
		return ret;
	}

	/**
	 * Devolve un listado das Instalacións que xestiona o usuario.
	 * @param user
	 * @return
	 */
	public JSONObject<String, Object> obterInstalacionsXestionadas(Usuario user) {
		JSONObject<String, Object> ret;
		
		List<Instalacion> instalacions = InstalacionAccessor.obterInstalacionsXestionadas(user);
		
		if(instalacions == null)
			return Error.GETINSTALACIONS_ERRORDB.toJSONError();
		
		if(instalacions.isEmpty())
			return Error.GETINSTALACIONS_SENINSTALACIONSXEST.toJSONError();
		
		ret = Mensaxe.GETINSTALACIONS_OK.toJSONMensaxe();
		ret.put("instalacions", instalacions);
		
		return ret;
	}
	
}
