package es.brudi.incidencias.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.List;

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

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.XestionIncidencias;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
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
@Checkdb
public class IncidenciaRest {
	
private Logger logger = Logger.getLogger(IncidenciaRest.class);
	
	public final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");

	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	@Context private SecurityContext securityContext;

	/**
	 * Crea unha nova incidencia.
	 * 
	 * @param cod_parte
	 * @param ot
	 * @param id_cliente
	 * @param id_instalacion
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param sol_presuposto
	 * @return
	 */
	@Path("/create")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> create(@QueryParam("cod_parte") String codParteS,
								   @QueryParam("ot") String otS,
								   @QueryParam("id_instalacion") String idInstalacionS,
								   @QueryParam("zona_apartamento") String zonaApartamento,
								   @QueryParam("descripcion_curta") String descripcionCurta,
								   @QueryParam("observacions") String observacions,
								   @QueryParam("sol_presuposto") String solPresupostoS) { 
		
        logger.debug("Invocouse o método create() de Incidencia.");
		
		int codParte;
		int ot;
		int idInstalacion;
		boolean solPresuposto = false;

		//Comprobase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
		try {
			codParte = Util.stringToInt(true, codParteS);
			ot = Util.stringToInt(true, otS);
			idInstalacion = Util.stringToInt(false, idInstalacionS);
			
			if(codParte < 0 || ot < 0 || idInstalacion < 0) //Estes parámetros non poden ser negativos. Se son -1 serán nulos.
				throw new NumberFormatException();
			
			//Se o parametro sol_presuposto ven en true deixase en true, en outro caso ponse a false.
			if(solPresupostoS != null && solPresupostoS.equals("true")) {
				solPresuposto = true;
			}
			
			//Estes deben estar tamén cubertos, en outro caso retornamos erro de faltan parámetros.
			if(zonaApartamento==null || descripcionCurta==null || observacions==null)
				throw new EmptyStackException();
			
		}
		catch(NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		}
		catch(EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		
		logger.debug("Parámetros correctos.");

		XestionIncidencias xest = new XestionIncidencias();

		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions,
				solPresuposto);
    }
	
	/**
	 * Obten unha inicidencia según o id.
	 * 
	 * @param idS
	 * @return
	 */
	@Path("/getById")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> getById(@QueryParam("id") String idS) {

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

		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.getById(user, id);
	}
	

	/**
	 * Obten incidencias según os parámetros que se lle pasan.
	 * 
	 * @param codParteS
	 * @param otS
	 * @param idInstalacionS
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param estado
	 * @param solPresuposto
	 * @param factura
	 * @param presuposto
	 * @param dataMenorS
	 * @param dataMaiorS
	 * @param autor
	 * @param codClienteS
	 * @param verS
	 * @return
	 */
	@Path("/get")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> get(@QueryParam("cod_parte") String codParteS,
								   		  @QueryParam("ot") String otS,
								   		  @QueryParam("id_instalacion") String idInstalacionS,
								   		  @QueryParam("zona_apartamento") String zonaApartamento,
								   		  @QueryParam("descripcion_curta") String descripcionCurta,
								   		  @QueryParam("observacions") String observacions,
										  @QueryParam("estado") List<String> estados,								   
								   		  @QueryParam("sol_presuposto") String solPresuposto,
								   		  @QueryParam("factura") String factura,
										  @QueryParam("presuposto") String presuposto,
										  @QueryParam("data_menor") String dataMenorS,
										  @QueryParam("data_maior") String dataMaiorS,
										  @QueryParam("autor") String autor,
										  @QueryParam("cod_cliente") String codClienteS,
										  @QueryParam("ver") String verS) {

		logger.debug("Invocouse o método get() de Incidencia.");

		int codParte;
		int ot;
		int idInstalacion;
		int codCliente;
		int ver;
		Calendar dataMenor = null;
		Calendar dataMaior = null;
		
		//Comprobase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
		try {
			codParte = Util.stringToInt(true, codParteS);
			ot = Util.stringToInt(true, otS);
			idInstalacion = Util.stringToInt(true, idInstalacionS);
			codCliente = Util.stringToInt(true, codClienteS);
			ver = Util.stringToInt(true, verS);
			
			if(ver < 0) //Se é menor que -1, error de parámetro.
				throw new NumberFormatException();
						
			if(solPresuposto != null) { // Se sol_presuposto non é nulo, true ou false, devolvemos un erro.
				if(!solPresuposto.equals("true") && !solPresuposto.equals("false"))
					throw new NumberFormatException();
			}
			
			if(dataMenorS != null && !dataMenorS.equals("")) {
				dataMenor = Calendar.getInstance();
				dataMenor.setTime(formatoData.parse(dataMenorS));
			}
			if(dataMaiorS != null && !dataMaiorS.equals("")) {
				dataMaior = Calendar.getInstance();
				dataMaior.setTime(formatoData.parse(dataMaiorS));
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

		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.get(user, codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions, estados,
				solPresuposto, factura, presuposto, dataMenor, dataMaior, autor, codCliente, ver);
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
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> cambiarEstado(@QueryParam("id") String idS, @QueryParam("estado") String estado) {

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

		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.cambiarEstado(user, id, estado);
	}
	
	/**
	 * Borra a incidencia que corresponda co Id que se lle proporciona.
	 * 
	 * @param idS
	 * @return
	 */
	@Path("/borrar")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> borrar(@QueryParam("id") String idS) {

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

		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.borrar(user, id);
	}
	
}
