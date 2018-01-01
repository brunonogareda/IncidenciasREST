package es.brudi.incidencias.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.XestionIncidencias;
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
	
	public final static SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");

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
	public JSONObject<String, Object> create(@QueryParam("cod_parte") String cod_parteS,
								   @QueryParam("ot") String otS,
								   @QueryParam("id_instalacion") String id_instalacionS,
								   @QueryParam("zona_apartamento") String zona_apartamento,
								   @QueryParam("descripcion_curta") String descripcion_curta,
								   @QueryParam("observacions") String observacions,
								   @QueryParam("sol_presuposto") String sol_presupostoS) { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();
		
        logger.debug("Invocouse o método create() de Incidencia.");
		
		int cod_parte, ot, id_instalacion;
		boolean sol_presuposto = false;

		//Comprobase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
		try {
			cod_parte = Util.stringToInt(true, cod_parteS);
			ot = Util.stringToInt(true, otS);
			id_instalacion = Util.stringToInt(false, id_instalacionS);
			
			if(cod_parte < 0 || ot < 0 || id_instalacion < 0) //Estes parámetros non poden ser negativos. Se son -1 serán nulos.
				throw new NumberFormatException();
			
			//Se o parametro sol_presuposto ven en true deixase en true, en outro caso ponse a false.
			if(sol_presupostoS != null && sol_presupostoS.equals("true")) {
				sol_presuposto = true;
			}
			
			//Estes deben estar tamén cubertos, en outro caso retornamos erro de faltan parámetros.
			if(zona_apartamento==null || descripcion_curta==null || observacions==null)
				throw new EmptyStackException();
			
		}
		catch(NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		}
		catch(EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		
		logger.debug("Parámetros correctos.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionIncidencias xest = new XestionIncidencias();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	Usuario user = xestu.getUsuario(req);
	        	json = xest.crear(user, cod_parte, ot, id_instalacion, zona_apartamento, descripcion_curta, observacions, sol_presuposto);
	        }
	     
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    }
	
	/**
	 * Obten unha inicidencia según o id.
	 * 
	 * @param idS
	 * @return
	 */
	@Path("/getById")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> getById(@QueryParam("id") String idS) {

		JSONObject<String, Object> json = new JSONObject<String, Object>();

		logger.debug("Invocouse o método getById() de Incidencia.");

		int id;

		// Comprobase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			id = Util.stringToInt(false, idS);
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}

		logger.debug("Parámetros correctos.");
		if (DBConnectionManager.getConnection() != null) {

			XestionIncidencias xest = new XestionIncidencias();
			XestionUsuarios xestu = new XestionUsuarios();

			json = xestu.checkLogin(req);
			if (json == null) {
				Usuario user = xestu.getUsuario(req);
				json = xest.getById(user, id);
			}

		} else {
			logger.warn("Non existe conexión coa base de datos.");
			json = Error.DATABASE.toJSONError();
		}

		return json;
	}
	

	/**
	 * Obten incidencias según os parámetros que se lle pasan.
	 * 
	 * @param cod_parteS
	 * @param otS
	 * @param id_instalacionS
	 * @param zona_apartamento
	 * @param descripcion_curta
	 * @param observacions
	 * @param estado
	 * @param sol_presuposto
	 * @param factura
	 * @param presuposto
	 * @param data_menorS
	 * @param data_maiorS
	 * @param autor
	 * @param cod_clienteS
	 * @param verS
	 * @return
	 */
	@Path("/get")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> get(@QueryParam("cod_parte") String cod_parteS,
								   		  @QueryParam("ot") String otS,
								   		  @QueryParam("id_instalacion") String id_instalacionS,
								   		  @QueryParam("zona_apartamento") String zona_apartamento,
								   		  @QueryParam("descripcion_curta") String descripcion_curta,
								   		  @QueryParam("observacions") String observacions,
										  @QueryParam("estado") String estado,								   
								   		  @QueryParam("sol_presuposto") String sol_presuposto,
								   		  @QueryParam("factura") String factura,
										  @QueryParam("presuposto") String presuposto,
										  @QueryParam("data_menor") String data_menorS,
										  @QueryParam("data_maior") String data_maiorS,
										  @QueryParam("autor") String autor,
										  @QueryParam("cod_cliente") String cod_clienteS,
										  @QueryParam("ver") String verS) {

		JSONObject<String, Object> json = new JSONObject<String, Object>();

		logger.debug("Invocouse o método get() de Incidencia.");

		int cod_parte=-1, ot=-1, id_instalacion=-1, cod_cliente=-1, ver=-1;

		Calendar data_menor = null;
		Calendar data_maior = null;
		
		//Comprobase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
		try {
			cod_parte = Util.stringToInt(true, cod_parteS);
			ot = Util.stringToInt(true, otS);
			id_instalacion = Util.stringToInt(true, id_instalacionS);
			cod_cliente = Util.stringToInt(true, cod_clienteS);
			ver = Util.stringToInt(true, verS);
			
			if(ver < 0) //Se é menor que -1, error de parámetro.
				throw new NumberFormatException();
						
			if(sol_presuposto != null) { // Se sol_presuposto non é nulo, true ou false, devolvemos un erro.
				if(!sol_presuposto.equals("true") && !sol_presuposto.equals("false"))
					throw new NumberFormatException();
			}
			
			if(data_menorS != null && !data_menorS.equals("")) {
				data_menor = Calendar.getInstance();
				data_menor.setTime(FORMATO_DATA.parse(data_menorS));
			}
			if(data_maiorS != null && !data_maiorS.equals("")) {
				data_maior = Calendar.getInstance();
				data_maior.setTime(FORMATO_DATA.parse(data_maiorS));
			}
			
		}
		catch(NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		}
		catch(EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		catch(ParseException e) {
			return Error.OBTERINCIDENCIAS_PARAMETRODATAERROR.toJSONError();
		}
		
		logger.debug("Parámetros correctos.");
		if (DBConnectionManager.getConnection() != null) {

			XestionIncidencias xest = new XestionIncidencias();
			XestionUsuarios xestu = new XestionUsuarios();

			json = xestu.checkLogin(req);
			if (json == null) {
				Usuario user = xestu.getUsuario(req);
				json = xest.get(user, cod_parte, ot, id_instalacion, zona_apartamento, descripcion_curta, observacions, estado, sol_presuposto, factura, presuposto, data_menor, data_maior, autor, cod_cliente, ver);
			}

		} else {
			logger.warn("Non existe conexión coa base de datos.");
			json = Error.DATABASE.toJSONError();
		}

		return json;
	}

	/**
	 * Modifica o estado de unha incidencia.
	 * 
	 * @param idS
	 * @param estado
	 * @return
	 */
	@Path("/cambiarEstado")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> cambiarEstado(@QueryParam("id") String idS, @QueryParam("estado") String estado) {

		JSONObject<String, Object> json = new JSONObject<String, Object>();

		logger.debug("Invocouse o método cambiarEstado() de Incidencia.");

		int id;

		// Comprobase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			id = Util.stringToInt(false, idS);
			
			if(estado == null || estado.equals(""))
				throw new EmptyStackException();
			
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}

		logger.debug("Parámetros correctos.");
		if (DBConnectionManager.getConnection() != null) {

			XestionIncidencias xest = new XestionIncidencias();
			XestionUsuarios xestu = new XestionUsuarios();

			json = xestu.checkLogin(req);
			if (json == null) {
				Usuario user = xestu.getUsuario(req);
				json = xest.cambiarEstado(user, id, estado);
			}

		} else {
			logger.warn("Non existe conexión coa base de datos.");
			json = Error.DATABASE.toJSONError();
		}

		return json;
	}
	
	/**
	 * Borra a incidencia que corresponda co Id que se lle proporciona.
	 * 
	 * @param idS
	 * @return
	 */
	@Path("/borrar")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> borrar(@QueryParam("id") String idS) {

		JSONObject<String, Object> json = new JSONObject<String, Object>();

		logger.debug("Invocouse o método borrar() de Incidencia.");

		int id;

		// Comprobase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			id = Util.stringToInt(false, idS);
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}

		logger.debug("Parámetros correctos.");
		if (DBConnectionManager.getConnection() != null) {

			XestionIncidencias xest = new XestionIncidencias();
			XestionUsuarios xestu = new XestionUsuarios();

			json = xestu.checkLogin(req);
			if (json == null) {
				Usuario user = xestu.getUsuario(req);
				json = xest.borrar(user, id);
			}

		} else {
			logger.warn("Non existe conexión coa base de datos.");
			json = Error.DATABASE.toJSONError();
		}

		return json;
	}
	
}
