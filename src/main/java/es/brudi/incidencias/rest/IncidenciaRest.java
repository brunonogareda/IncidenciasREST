package es.brudi.incidencias.rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.XestionIncidencias;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.usuarios.Usuario;

/**
 * 
 * Servlet coas diferentes accións relacionadas coas incidencias, que pode
 * realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a coexión coa base de
 * datos é correcta, e que o usuario que fai a petición teña a sesión aberta.
 * Todos devolven un Http response con un json.
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

	@Context
	private HttpServletRequest req;
	@Context
	private HttpServletResponse res;
	@Context
	private SecurityContext securityContext;

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
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> insertar(@DefaultValue("-1") @FormDataParam("codParte") int codParte,
											   @DefaultValue("-1") @FormDataParam("ot") int ot,
											   @DefaultValue("-1") @FormDataParam("idInstalacion") int idInstalacion,
											   @FormDataParam("zonaApartamento") String zonaApartamento,
											   @FormDataParam("descripcionCurta") String descripcionCurta,
											   @FormDataParam("observacions") String observacions,
											   @FormDataParam("solPresuposto") String solPresupostoS) {

		logger.debug("Invocouse o método insertar() de Incidencia.");
		boolean solPresuposto = false;
		// Comprobase que os parámetros obligatorios se pasaron e que están no formato
		if (idInstalacion == -1 || zonaApartamento == null || descripcionCurta == null || observacions == null)
			return Error.FALTANPARAM.toJSONError();

		if (codParte < -1 || ot < -1 || idInstalacion < -1) // Estes parámetros non poden ser negativos. Se son -1 serán nulos
			return Error.ERRORPARAM.toJSONError();

		// Se o parametro sol_presuposto ven en true deixase en true, en outro caso
		// ponse a false.
		if (solPresupostoS != null && solPresupostoS.equals("true"))
			solPresuposto = true;

		logger.debug("Parámetros correctos.");
		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions, solPresuposto);
	}

	/**
	 * Obten unha inicidencia según o id.
	 * 
	 * @param idS
	 * @return
	 */
	@Path("/{id}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> obterPorId(@DefaultValue("-1") @PathParam("id") int id) {

		logger.debug("Invocouse o método getById() de Incidencia.");
		// Comprobase que os parámetros obligatorios
		if(id == -1)
			return Error.FALTANPARAM.toJSONError();

		logger.debug("Parámetros correctos.");
		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterPorId(user, id);
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
	@Path("/buscar")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> buscar(@DefaultValue("-1") @QueryParam("codParte") int codParte,
										 	 @DefaultValue("-1") @QueryParam("ot") int ot,
										 	 @DefaultValue("-1") @QueryParam("idInstalacion") int idInstalacion,
										 	 @QueryParam("zonaApartamento") String zonaApartamento,
										 	 @QueryParam("descripcion_curta") String descripcionCurta,
										 	 @QueryParam("observacions") String observacions,
										 	 @QueryParam("estado") List<String> estados,
										 	 @QueryParam("solPresuposto") String solPresuposto,
										 	 @QueryParam("factura") String factura,
										 	 @QueryParam("presuposto") String presuposto,
										 	 @DefaultValue("-1") @QueryParam("dataMenor") long dataMenor,
										 	 @DefaultValue("-1") @QueryParam("dataMaior") long dataMaior,
										 	 @DefaultValue("-1") @QueryParam("autor") int autor,
										 	 @DefaultValue("0") @QueryParam("ver") int ver) {

		logger.debug("Invocouse o método buscar() de Incidencia.");
		Calendar dataMenorC = null;
		Calendar dataMaiorC = null;
		// Comprobase que os parámetros obligatorios se pasaron e que están no formato
		if (ver < -1) // Se é menor que -1, error de parámetro.
			return Error.ERRORPARAM.toJSONError();

		if (solPresuposto != null && !solPresuposto.equalsIgnoreCase("true")
				&& !solPresuposto.equalsIgnoreCase("false")) // Se sol_presuposto non é nulo, true ou false, devolvemos un erro.
			return Error.ERRORPARAM.toJSONError();

		if (dataMenor != -1) {
			dataMenorC = Calendar.getInstance();
			dataMenorC.setTimeInMillis(dataMenor);
		}
		if (dataMaior != -1) {
			dataMaiorC = Calendar.getInstance();
			dataMaiorC.setTimeInMillis(dataMaior);
		}
		
		logger.debug("Parámetros correctos.");
		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obter(user, codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions, estados,
				solPresuposto, factura, presuposto, dataMenorC, dataMaiorC, autor, ver);
	}

	/**
	 * Modifica o estado de unha incidencia.
	 * 
	 * @param id
	 * @param estado
	 * @return
	 */
	@Path("{id}/estado")
	@PUT
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> cambiarEstado(@DefaultValue("-1") @PathParam("id") int id,
													@FormDataParam("estado") String estado) {

		logger.debug("Invocouse o método cambiarEstado() de Incidencia.");

		// Comprobase que os parámetros obligatorios se pasaron
		if(id == -1 || estado == null || estado.isEmpty())
			return Error.FALTANPARAM.toJSONError();

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
	@Path("{id}")
	@DELETE
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> borrar(@DefaultValue("-1") @PathParam("id") int id) {

		logger.debug("Invocouse o método borrar() de Incidencia.");

		// Comprobase que os parámetros obligatorios se pasaron
		if(id == -1)
			return Error.FALTANPARAM.toJSONError();

		logger.debug("Parámetros correctos.");
		XestionIncidencias xest = new XestionIncidencias();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.borrar(user, id);
	}

}
