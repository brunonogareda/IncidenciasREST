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
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;

/**
 * 
 * Servlet coas diferentes accións relacionadas con usuarios, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos (excepto logout) comproban en primeiro lugar que a coexión coa base de datos é correcta,
 * e (excepto o login) que o usuario que fai a petición teña a sesión aberta.
 * Ademáis comproba que se reciban os parámetros necesarios
 * Todos devolven un Http response con un json.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
@Path("/usuario")
@XmlRootElement
@Checkdb
public class UsuarioRest {
	
	private Logger logger = Logger.getLogger(UsuarioRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	@Context private SecurityContext securityContext;
	
	/**
	 * 
	 * Inicia a sesión do usuario. Devolve un mensaxe de estado e datos do usuario.
	 */
	@Path("/login")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> login(@QueryParam("username") String username, @QueryParam("password") String password) { 
		
		JSONObject<String, Object> json;
		logger.debug("Invocouse o método login() de usuario.");
		if (username == null || password == null) {
			json = Error.LOGIN_SENPARAMETROS.toJSONError();
		} else {
			XestionUsuarios xest = new XestionUsuarios();
			json = xest.login(username, password);
		}
		return json;
    } 

	/**
	 * 
	 * Cambia o contrasinal do usuario que se encontra logueado. Devolve un mensaxe de estado.
	 */
	@Path("/changepassword")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> changepassword(@QueryParam("pass_old") String passOld, @QueryParam("pass_new1") String passNew1,
			@QueryParam("pass_new2") String passNew2) {
		
		JSONObject<String, Object> json;

		logger.debug("Invocouse o método changepassword() de usuario.");
		if (passOld != null) {

			if (passNew1 != null && passNew2 != null) {

				Usuario user = XestionUsuarios.getUsuario(securityContext);
				XestionUsuarios xest = new XestionUsuarios();
				json = xest.changepass(user, passOld, passNew1, passNew2);
			} else {
				json = Error.CHANGEPASS_SENPARAMETROPASS.toJSONError();
			}
		} else {
			json = Error.CHANGEPASS_SENPARAMETROANT.toJSONError();
		}
		return json;
	}
	
	/**
	 * 
	 * Cambia o email do usuario logueado. Devolve un mensaxe de estado.
	 */
	@Path("/changemail")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> changemail(@QueryParam("mail") String mail) {
		
		JSONObject<String, Object> json;

		logger.debug("Invocouse o método changemail() de usuario.");
		if(mail != null) {

			XestionUsuarios xest = new XestionUsuarios();
			Usuario user = XestionUsuarios.getUsuario(securityContext);
			
			json = xest.changemail(user, mail);
		}
		else {
			json = Error.CHANGEMAIL_SENPARAMETROS.toJSONError();
		}

        return json;
	}
	
	/**
	 * 
	 * Obten os permisos do usuario
	 */
	@Path("/obterPermisos")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> obterPermisos() {
        logger.debug("Invocouse o método obterPermisos() de usuario.");
	    XestionUsuarios xest = new XestionUsuarios();
	    Usuario user = XestionUsuarios.getUsuario(securityContext);
        return xest.obterPermisos(user);
	}

}
