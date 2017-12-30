package es.brudi.incidencias.clientes;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import es.brudi.incidencias.util.JSONArray;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.db.dao.ClienteDAO;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;

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
	public JSONObject<String, Object> getClientes(HttpServletRequest req) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		JSONArray<Object> jsonClientes = new JSONArray<Object>();
		
		XestionUsuarios xest = new XestionUsuarios();
		Usuario user = xest.getUsuario(req);
		
		//Compraba que o usuario que realiza a petición pertenzca a Brudi.
		if(user.getCliente().getCod_cliente()!=0) {
			return Error.USER_NOPERMISOS.toJSONError();
		}
		
		ArrayList<Cliente> Clientes = ClienteDAO.getClientes();
		
		if(Clientes != null) {
			if(Clientes.size()>0) {
				for(Cliente cli : Clientes) {
					jsonClientes.add(cli.toJson());
				}
				ret = Mensaxe.GETCLIENTES_OK.toJSONMensaxe();
				ret.put("clientes", jsonClientes);
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
