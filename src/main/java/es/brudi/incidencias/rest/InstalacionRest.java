package es.brudi.incidencias.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.instalacions.XestionInstalacions;
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
public class InstalacionRest {
	
	private Logger logger = Logger.getLogger(InstalacionRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	/**
	 * Devolve un listado das instalacións da base de datos según o id de cliente que se lle pase.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Path("/getByCliente")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> getByCliente(@QueryParam("idCliente") String idClienteS) { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();
		
		int idCliente = -1;
		
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
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionInstalacions xest = new XestionInstalacions();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	json = xest.getInstalacionsByCliente(xestu.getUsuario(req), idCliente);
	        }
	     
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    } 

}
