package es.brudi.incidencias.usuarios;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.db.dao.UsuarioDAO;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.mensaxes.Mensaxe;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
	 * Método que establece a sesión do usuario se este introduciu correctamente os datos.
	 * Crea un id de sesión e garda no contexto do servlet un obxecto usuario correspondente.
	 * 
	 * @param req - RequestHttp do servlet
	 * @param username - nome que introduce o usuario
	 * @param password - Contrasinal que introduce o usuario
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> login(HttpServletRequest req, String username, String password) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		JSONObject<String, Object> usuario = new JSONObject<String, Object>();
		
		Usuario user = UsuarioDAO.comprobar_usuario(username, password);
		
		if(user == null) {
			return Error.LOGIN_USER.toJSONError();
		}
		
		HttpSession session=req.getSession();
        session.setAttribute("usuario", (Usuario)user);

        ret = Mensaxe.LOGIN_OK.toJSONMensaxe();
        usuario.put("sessionId", session.getId());
        usuario.put("username", user.getNome());
        usuario.put("nome", user.getNome_completo());

        ret.put("usuario", usuario);

        return ret;
	}
	
	/**
	 * Método que permite cambiar o contrasial o usuario
	 * 
	 * @param req - RequestHttp do servlet
	 * @param email - Email novo que desexa modificar o usuario.
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> changemail(HttpServletRequest req, String email) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		Usuario user = this.getUsuario(req);
		
		//Comprobase que o formato do email enviado sexa correcto
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(email)) {
		   ret = Error.CHANGEMAIL_ERRORPARAMETROS.toJSONError();
		}
		else {
			boolean cambiado = UsuarioDAO.cambiarEmail(email, user.getId());
			
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
	 * @param pass_old - Antigo contrasinal do usuario.
	 * @param pass_new1 - Novo contrasinal do usuario.
	 * @param pass_new2 - Repetición do novo contrasinal.
	 * @return Obxecto json coa resposta
	 */
	public JSONObject<String, Object> changepass(HttpServletRequest req, String pass_old, String pass_new1, String pass_new2) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
				
		Usuario user = this.getUsuario(req);
		
		Usuario user_comp = UsuarioDAO.comprobar_usuario(user.getNome(), pass_old);
		
		//Comprobase que o contrasinal antigo é o correcto do usuario que ten a sesión iniciada.
		if(user_comp == null || user_comp.getId() != user.getId()) {
			return Error.CHANGEPASS_ERRORPASS.toJSONError();
		}
		
		//Comprobase que os dous contrasinais novos son iguais.
		if(pass_new1.equals(pass_new2)) {
			boolean cambiado = UsuarioDAO.cambiarPass(pass_new1, user.getId());
			
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
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		ret = Mensaxe.OBTERPERMISOS_OK.toJSONMensaxe();
		ret.put("Permisos", user.getPermisosFinalesJSON());
		
		return ret;
	}
	
	
	/**
	 * Método que comproba que exista un no request da petición un atributo usuario, é dicir, que o usuario que solicita a petición teña a sesión iniciada
	 * 
	 * @param req - RequestHttp do servlet
	 * @return Obxecto json de erro. Devolve nulo en caso de que o login sexa correcto.
	 */
	public JSONObject<String, Object> checkLogin(HttpServletRequest req) {
		Usuario user = this.getUsuario(req);

		if(user==null) {
			return Error.USER_NOLOGIN.toJSONError();
		}

		return null;
	}
	
	/**
	 * 
	 * Método que devolve o obxecto usuario do contexto do request.
	 * 
	 * @param req - RequestHttp do servlet
	 * @return Usuario que ten iniciada a sesión.
	 */
	public Usuario getUsuario(HttpServletRequest req) {
		return (Usuario)req.getSession().getAttribute("usuario");
	}
	
}
