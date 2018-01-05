package es.brudi.incidencias.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.clientes.XestionClientes;

/**
 * 
 * Servlet coas diferentes accións relacionadas con clientes, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a coexión coa base de datos é correcta,
 * e que o usuario que fai a petición teña a sesión aberta. Todos devolven un Http response con un json.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
@Path("/cliente")
public class ClienteRest {
	
	private Logger logger = Logger.getLogger(ClienteRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	/**
	 * Devolve un listado dos clientes da base de datos.
	 */
	@Path("/get")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> get() { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();

        logger.debug("Invocouse o método get() de clientes.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionClientes xest = new XestionClientes();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	json = xest.getClientes(req);
	        }
	     
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    } 


}
