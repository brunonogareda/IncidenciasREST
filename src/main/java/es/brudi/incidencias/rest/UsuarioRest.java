package es.brudi.incidencias.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET; 
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;

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
public class UsuarioRest {
	
	private Logger logger = Logger.getLogger(UsuarioRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	/**
	 * 
	 * Inicia a sesión do usuario. Devolve un mensaxe de estado e datos do usuario.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Path("/login")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> login(@QueryParam("username") String username, @QueryParam("password") String password) { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();

        logger.debug("Invocouse o método login() de usuario.");
        if(DBConnectionManager.getConnection() != null ) {
        	
	        if(username==null || password==null) {
	        	json = Error.LOGIN_SENPARAMETROS.toJSONError();
	        }
	        else {
	        	XestionUsuarios xest = new XestionUsuarios();
	        	json = xest.login(req, username, password);
	        }
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
		
    } 

	/**
	 * 
	 * Pecha a sesión do usuario. Devolve un mensaxe de estado.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Path("/logout")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> logout() { 
				
		JSONObject<String, Object> json = new JSONObject<String, Object>();
        
        logger.debug("Invocouse o método logout() de usuario.");
        HttpSession session=req.getSession();
        session.invalidate();  
        
        json = Mensaxe.LOGOUT_OK.toJSONMensaxe();
        
        return json;
    }

	/**
	 * 
	 * Cambia o contrasinal do usuario que se encontra logueado. Devolve un mensaxe de estado.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Path("/changepassword")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> changepassword(@QueryParam("pass_old") String pass_old, @QueryParam("pass_new1") String pass_new1,
			@QueryParam("pass_new2") String pass_new2) {
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();
        
        logger.debug("Invocouse o método changepassword() de usuario.");
        if(DBConnectionManager.getConnection() != null ) {	        
	        if(pass_old != null) {
	        	
	        	if(pass_new1 != null && pass_new2 != null) {
	        	
			        XestionUsuarios xest = new XestionUsuarios();
			        json = xest.checkLogin(req);
			        if (json == null) {
			        	json = xest.changepass(req, pass_old, pass_new1, pass_new2);
			        }
	        	}
	        	else {
	        		json = Error.CHANGEPASS_SENPARAMETROPASS.toJSONError();
	        	}
	        }
	        else {
	        	json = Error.CHANGEPASS_SENPARAMETROANT.toJSONError();
	        }
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
	}
	
	/**
	 * 
	 * Cambia o email do usuario logueado. Devolve un mensaxe de estado.
	 * @throws ServletException
	 * @throws IOException
	 */
	@Path("/changemail")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> changemail(@QueryParam("mail") String mail) {
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();

        logger.debug("Invocouse o método changemail() de usuario.");
        if(DBConnectionManager.getConnection() != null ) {
        	
	        if(mail != null) {
	        	
		        XestionUsuarios xest = new XestionUsuarios();
		        json = xest.checkLogin(req);
		        if (json == null) {
		        	json = xest.changemail(req, mail);
		        }
	        }
	        else {
	        	json = Error.CHANGEMAIL_SENPARAMETROS.toJSONError();
	        }
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }

        return json;
	}

}
