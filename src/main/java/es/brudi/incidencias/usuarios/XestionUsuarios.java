package es.brudi.incidencias.usuarios;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.rest.util.SecuredFilter.User;
import es.brudi.incidencias.usuarios.db.UsuarioAccessor;

import javax.ws.rs.core.SecurityContext;

import org.apache.commons.validator.routines.EmailValidator;


/**
 * 
 * Clase que xestiona as funcións relacionados cos usuarios.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class XestionUsuarios {
	
	/**
	 * 
	 * Método que devolve o token para o usuario se este introduciu correctamente os datos.
	 * 
	 * @param username - nome que introduce o usuario
	 * @param password - Contrasinal que introduce o usuario
	 * @return Obxecto json coa resposta, token e datos do usuario.
	 */
	public JSONObject<String, Object> login(String username, String password) {
		JSONObject<String, Object> ret;
		
		Usuario user = UsuarioAccessor.comprobarUsuario(username, password);
		
		if(user == null) {
			return Error.LOGIN_USER.toJSONError();
		}
		
        ret = Mensaxe.LOGIN_OK.toJSONMensaxe();
        
        ret.put("usuario", user.toJSON());
        ret.put("token", XestionTokens.xerarFirmarToken(username));

        return ret;
	}
	
	/**
	 * Método que permite cambiar o contrasial o usuario
	 * 
	 * @param email - Email novo que desexa modificar o usuario.
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> changemail(Usuario user, String email) {
		JSONObject<String, Object> ret;
		
		//Comprobase que o formato do email enviado sexa correcto
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(email)) {
		   ret = Error.CHANGEMAIL_ERRORPARAMETROS.toJSONError();
		}
		else {
			boolean cambiado = UsuarioAccessor.cambiarEmail(email, user.getId());
			
			if(cambiado) {
				ret = Mensaxe.CHANGEMAIL_OK.toJSONMensaxe();
			}
			else {
				ret = Error.CHANGEMAIL_ERROR.toJSONError();
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * Método que permite cambiar o contrainal do usuario.
	 * 
	 * @param req - RequestHttp do servlet
	 * @param passOld - Antigo contrasinal do usuario.
	 * @param passNew1 - Novo contrasinal do usuario.
	 * @param passNew2 - Repetición do novo contrasinal.
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> changepass(Usuario user, String passOld, String passNew1, String passNew2) {
		JSONObject<String, Object> ret;
				
		Usuario userComp = UsuarioAccessor.comprobarUsuario(user.getNome(), passOld);
		
		//Comprobase que o contrasinal antigo é o correcto do usuario que ten a sesión iniciada.
		if(userComp == null || userComp.getId() != user.getId()) {
			return Error.CHANGEPASS_ERRORPASS.toJSONError();
		}
		
		//Comprobase que os dous contrasinais novos son iguais.
		if(passNew1.equals(passNew2)) {
			boolean cambiado = UsuarioAccessor.cambiarPass(passNew1, user.getId());
			
			if(cambiado) {
				ret = Mensaxe.CHANGEPASS_OK.toJSONMensaxe();
			}
			else {
				ret = Error.CHANGEPASS_ERROR.toJSONError();
			}
		}
		else {
			ret = Error.CHANGEPASS_DIFERENTES.toJSONError();
		}
		
			
		return ret;
	}
	
	/**
	 * Método que devolve os permisos do usuario rexistrador
	 * @param req
	 * @return
	 */
	public JSONObject<String, Object> obterPermisos(Usuario user) {
		JSONObject<String, Object> ret;

		ret = Mensaxe.OBTERPERMISOS_OK.toJSONMensaxe();
		ret.put("Permisos", user.getPermisosFinalesJSON());
		
		return ret;
	}
	
	/**
	 * 
	 * Método que devolve o obxecto usuario do contexto do request.
	 * 
	 * @return Usuario que ten iniciada a sesión.
	 */
	public static Usuario getUsuario(SecurityContext securityContext) {
		return ((User)securityContext.getUserPrincipal()).obterUsuario();
	}
	
	
}
