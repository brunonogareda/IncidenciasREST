package es.brudi.incidencias.rest;

import java.io.IOException;
import java.util.EmptyStackException;

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
import org.json.simple.JSONObject;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.XestionIncidencias;
import es.brudi.incidencias.instalacions.XestionInstalacions;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.Util;

/**
 * 
 * Servlet coas diferentes accións relacionadas coas incidencias, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a coexión coa base de datos é correcta,
 * e que o usuario que fai a petición teña a sesión aberta. Todos devolven un Http response con un json.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
@Path("/incidencia")
public class IncidenciaRest {
	
private Logger logger = Logger.getLogger(IncidenciaRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;

	/**
	 * Crea unha nova incidencia.
	 * 
	 * @param cod_parte
	 * @param ot
	 * @param id_cliente
	 * @param id_instalacion
	 * @param zona_apartamento
	 * @param descripcion_curta
	 * @param observacions
	 * @param sol_presuposto
	 * @return
	 */
	@Path("/create")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getByCliente(@QueryParam("cod_parte") String cod_parteS,
								   @QueryParam("ot") String otS,
								   @QueryParam("id_instalacion") String id_instalacionS,
								   @QueryParam("zona_apartamento") String zona_apartamento,
								   @QueryParam("descripcion_curta") String descripcion_curta,
								   @QueryParam("observacions") String observacions,
								   @QueryParam("sol_presuposto") String sol_presupostoS) { 
		
		JSONObject json = new JSONObject();
		
        logger.debug("Invocouse o método create() de Incidencia.");
		
		int cod_parte, ot, id_instalacion;
		boolean sol_presuposto = false;

		//Comprobase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
		try {
			cod_parte = Util.stringToInt(true, cod_parteS);
			ot = Util.stringToInt(true, otS);
			id_instalacion = Util.stringToInt(false, id_instalacionS);
			
			//Se o parametro sol_presuposto ven en true deixase en true, en outro caso ponse a false.
			if(sol_presupostoS != null && sol_presupostoS.equals("true")) {
				sol_presuposto = true;
			}
			
			//Estes deben estar tamén cubertos, en outro caso retornamos erro de faltan parámetros.
			if(zona_apartamento==null || descripcion_curta==null || observacions==null)
				throw new EmptyStackException();
			
		}
		catch(NumberFormatException e) {
			return Error.CREATEINCIDENCIA_ERRORPARAM.toJSONError();
		}
		catch(EmptyStackException e) {
			return Error.CREATEINCIDENCIA_FALTANPARAM.toJSONError();
		}
		
		logger.debug("Parámetros correctos.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionIncidencias xest = new XestionIncidencias();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	Usuario user = xestu.getUsuario(req);
	        	json = xest.create(user, cod_parte, ot, id_instalacion, zona_apartamento, descripcion_curta, observacions, sol_presuposto);
	        }
	     
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    } 
	
}
