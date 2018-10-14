package es.brudi.incidencias.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
@Path("/")
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> login(@FormDataParam("username") String username, @FormDataParam("password") String password) { 
		logger.debug("Invocouse o método login() de usuario.");
		if (username == null || password == null)
			return Error.LOGIN_SENPARAMETROS.toJSONError();
		
		logger.debug("Parámetros correctos.");
		XestionUsuarios xest = new XestionUsuarios();
		return xest.login(username, password);
    }

	/**
	 * 
	 * Cambia o contrasinal do usuario que o solicita. Devolve un mensaxe de estado.
	 */
	@Path("/usuario/cambiarPassword")
	@PUT
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> cambiarPassword(@FormDataParam("passOld") String passOld, @FormDataParam("passNew1") String passNew1,
			@FormDataParam("passNew2") String passNew2) {
		logger.debug("Invocouse o método cambiarPassword() de usuario.");
		if (passOld == null)
			return Error.CHANGEPASS_SENPARAMETROANT.toJSONError();
		if (passNew1 == null || passNew2 == null)
			return Error.CHANGEPASS_SENPARAMETROPASS.toJSONError();

		logger.debug("Parámetros correctos.");
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		XestionUsuarios xest = new XestionUsuarios();
		return xest.changepass(user, passOld, passNew1, passNew2);
	}
	
	/**
	 * 
	 * Cambia o email do usuario que o solicita. Devolve un mensaxe de estado.
	 */
	@Path("/usuario/cambiarMail")
	@PUT
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> cambiarMail(@FormDataParam("mail") String mail) {
		logger.debug("Invocouse o método cambiarMail() de usuario.");
		if(mail == null)
			return Error.CHANGEMAIL_SENPARAMETROS.toJSONError();
		//Comprobase que o formato do email enviado sexa correcto
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(mail))
			return Error.CHANGEMAIL_ERRORPARAMETROS.toJSONError();
		
		logger.debug("Parámetros correctos.");
		XestionUsuarios xest = new XestionUsuarios();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.changemail(user, mail);
	}
	
	/**
	 * 
	 * Obten os permisos do usuario
	 */
	@Path("/usuario/obterPermisos")
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
