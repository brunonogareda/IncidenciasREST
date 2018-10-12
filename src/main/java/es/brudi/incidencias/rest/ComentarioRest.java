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
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import es.brudi.incidencias.comentarios.XestionComentarios;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
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
@Checkdb
public class ComentarioRest {
	
private Logger logger = Logger.getLogger(ComentarioRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	@Context private SecurityContext securityContext;
	
	/**
	 * Crea un novo comentario en unha incidencia na base de datos
	 */
	@Path("/insertar")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> insertar(@QueryParam("id_incidencia") String idIncidenciaS,
    										   @QueryParam("texto") String texto,
    										   @QueryParam("tipo") String tipoS) { 
		
        logger.debug("Invocouse o método insertar() de comentario.");
        
        int idIncidencia;
        int tipo;
        try {
      		idIncidencia = Util.stringToInt(false, idIncidenciaS);
      		tipo = Util.stringToInt(true, tipoS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
         
		XestionComentarios xest = new XestionComentarios();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.insertar(user, idIncidencia, texto, tipo);
    }
	
	
	/**
	 * Obtén comentarios mediante o id da incidencia
	 * 
	 * @param id
	 * @return
	 */
	@Path("/obterXIncidencia")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> obterXIncidencia(@QueryParam("id_incidencia") String idIncidenciaS) { 

        logger.debug("Invocouse o método obterXIncidencia() de comentario.");
                
        int idIncidencia;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	try {
      		idIncidencia = Util.stringToInt(false, idIncidenciaS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
         
		XestionComentarios xest = new XestionComentarios();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterXIncidencia(user, idIncidencia);
    } 


}
