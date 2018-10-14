package es.brudi.incidencias.clientes;

import java.util.List;

import org.apache.log4j.Logger;

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

	private Logger logger = Logger.getLogger(XestionClientes.class);
	
	/**
	 * 
	 * Método que obtén todos os clientes do sistema. Se todo é orrecto, devolve un
	 * obxecto json que contén un array cos clientes. Só os usuario que pertencen o
	 * cliente 0 (Brudi), poden obter esta información.
	 * 
	 * @param req
	 *            - RequestHttp do servlet
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> getClientes(Usuario user) {
		JSONObject<String, Object> ret;

		// Comproba que o usuario que realiza a petición teña permisos para ver Clientes
		if (!user.podeVerClientes())
			return Error.USER_NOPERMISOS.toJSONError();

		List<Cliente> clientes = ClienteAccessor.getClientes();
		if (clientes == null)
			return Error.GETCLIENTES_ERRORDB.toJSONError();

		if (clientes.isEmpty())
			return Error.GETCLIENTES_SENCLIENTES.toJSONError();

		logger.debug("Obtivéronse os clientes correctamente.");
		ret = Mensaxe.GETCLIENTES_OK.toJSONMensaxe();
		ret.put("clientes", clientes);
		return ret;
	}

}
