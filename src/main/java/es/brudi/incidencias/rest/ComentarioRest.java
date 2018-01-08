package es.brudi.incidencias.rest;

import java.util.EmptyStackException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import es.brudi.incidencias.comentarios.XestionComentarios;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.util.Util;

/**
 * 
 * Servlet coas diferentes accións relacionadas cos comentarios, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a coexión coa base de datos é correcta,
 * e que o usuario que fai a petición teña a sesión aberta. Todos devolven un Http response con un json.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
@Path("/comentario")
public class ComentarioRest {
	
private Logger logger = Logger.getLogger(ComentarioRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	/**
	 * Crea un novo comentario en unha incidencia na base de datos
	 */
	@Path("/insertar")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> insertar(@QueryParam("id_incidencia") String id_incidenciaS,
    										   @QueryParam("texto") String texto) { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();
        logger.debug("Invocouse o método insertar() de comentario.");
        
        int id_incidencia = -1;
        try {
      		id_incidencia = Util.stringToInt(false, id_incidenciaS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionComentarios xest = new XestionComentarios();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	Usuario user = xestu.getUsuario(req);
	        	json = xest.insertar(user, id_incidencia, texto);
	        }
	     
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    }
	
	
	/**
	 * Obtén comentarios mediante o id da incidencia
	 * 
	 * @param id
	 * @return
	 */
	@Path("/obterXIncidencia")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> obterXIncidencia(@QueryParam("id_incidencia") String id_incidenciaS) { 
		JSONObject<String, Object> json = new JSONObject<String, Object>();

        logger.debug("Invocouse o método obterXIncidencia() de comentario.");
                
        int id_incidencia = -1;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	try {
      		id_incidencia = Util.stringToInt(false, id_incidenciaS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionComentarios xest = new XestionComentarios();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	Usuario user = xestu.getUsuario(req);
	        	json = xest.obterXIncidencia(user, id_incidencia);
	        }
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    } 


}
