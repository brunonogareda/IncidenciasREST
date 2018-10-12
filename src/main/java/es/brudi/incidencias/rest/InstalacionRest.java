package es.brudi.incidencias.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.instalacions.XestionInstalacions;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.Util;

/**
 * 
 * Servlet coas diferentes accións relacionadas coas instalacións, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a coexión coa base de datos é correcta,
 * e que o usuario que fai a petición teña a sesión aberta. Todos devolven un Http response con un json.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
@Path("/instalacion")
@Checkdb
public class InstalacionRest {
	
	private Logger logger = Logger.getLogger(InstalacionRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	@Context private SecurityContext securityContext;
	
	/**
	 * Devolve un listado das instalacións da base de datos según o id de cliente que se lle pase.
	 */
	@Path("/getByCliente")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> getByCliente(@QueryParam("idCliente") String idClienteS) { 
		
		int idCliente;
		
		//O parámetro debe ser un número maior ou igual a 0.
		//En caso de non existir o parámetro, retornarase todas as incidencias. Sempre que o usuario teña permisos suficientes.
		if(Util.isNumeric(idClienteS)) {
			idCliente = Integer.parseInt(idClienteS);
			if(idCliente<0)
				return Error.GETINSTALACIONS_ERRORPARAMNUM.toJSONError();
		}
		else if(idClienteS==null) {
			idCliente = -1;
		}
		else {
			return Error.GETINSTALACIONS_ERRORPARAMNUM.toJSONError();
		}

        logger.debug("Invocouse o método getByCliente() de Instalacion.");
         
		XestionInstalacions xest = new XestionInstalacions();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.getInstalacionsByCliente(user, idCliente);
	}

}
