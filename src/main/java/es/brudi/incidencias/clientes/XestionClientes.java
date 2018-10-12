package es.brudi.incidencias.clientes;

import java.util.List;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.clientes.db.ClienteAccessor;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;

/**
 * 
 * Clase que xestiona as funcións relacionados cos clientes.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class XestionClientes {

	/**
	 * 
	 * Método que obtén todos os clientes do sistema. Se todo é orrecto, devolve un obxecto json que contén un array cos clientes.
	 * Só os usuario que pertencen o cliente 0 (Brudi), poden obter esta información. 
	 * 
	 * @param req - RequestHttp do servlet
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> getClientes(Usuario user) {
		JSONObject<String, Object> ret;
		
		//Compraba que o usuario que realiza a petición pertenzca a Brudi.
		if(user.getCliente().getCodCliente()!=0) {
			return Error.USER_NOPERMISOS.toJSONError();
		}
		
		List<Cliente> clientes = ClienteAccessor.getClientes();
		
		if(clientes != null) {
			if(!clientes.isEmpty()) {
				ret = Mensaxe.GETCLIENTES_OK.toJSONMensaxe();
				ret.put("clientes", clientes);
			}
			else {
				ret = Error.GETCLIENTES_SENCLIENTES.toJSONError();
			}
		}
		else {
			ret = Error.GETCLIENTES_ERRORDB.toJSONError();
		}
		return ret;
	}
	
}
