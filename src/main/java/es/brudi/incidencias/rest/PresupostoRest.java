package es.brudi.incidencias.rest;

import java.io.InputStream;
import java.util.EmptyStackException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.presupostos.XestionPresupostos;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.util.Util;

/**
 * 
 * Servlet coas diferentes accións relacionadas con presupostos, que pode
 * realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a conexión coa base de
 * datos é correcta, e que o usuario que fai a petición teña a sesión aberta. E
 * que os parámetros necesarios estean na petición. Todos devolven un Http
 * response con un json. Excepto o método de obterFicheiro.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
@Path("/presuposto")
@Checkdb
public class PresupostoRest {

	private Logger logger = Logger.getLogger(PresupostoRest.class);

	@Context
	private HttpServletRequest req;
	@Context
	private HttpServletResponse res;
	@Context
	private SecurityContext securityContext;

	/**
	 * Crea un novo presuposto na base de datos. En caso de recibir un ficheiro
	 * gardao en local. Devolve o presuposto creado
	 * 
	 * @param idIncidenciaS
	 *            - Identificador da incidencia a que corresponde o presuposto.
	 *            OBLIGATORIO
	 * @param idPresuposto
	 *            - Identificador do presuposto. OBLIGATORIO
	 * @param comentarios
	 * @param aceptado
	 *            Se o presuposto está aceptado ou non ("true" -> true / En outro
	 *            caso false)
	 * @param uploadedInputStream
	 *            (file)
	 * @param fileDetail
	 *            (file)
	 * @return
	 */
	@Path("/insertar")
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JSONObject<String, Object> insertar(@FormDataParam("id_incidencia") String idIncidenciaS,
			@FormDataParam("id_presuposto") String idPresuposto, @FormDataParam("comentarios") String comentarios,
			@FormDataParam("aceptado") String aceptadoS, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método insertar() de presuposto.");

		int idIncidencia = -1;
		boolean aceptado = false;

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			idIncidencia = Util.stringToInt(false, idIncidenciaS);
			if (idPresuposto == null || idPresuposto.equals(""))
				throw new EmptyStackException();
			if (idPresuposto.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		if (aceptadoS != null && aceptadoS.equals("true"))
			aceptado = true;

		logger.debug("Parámetros correctos.");

		XestionPresupostos xest = new XestionPresupostos();

		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, idIncidencia, idPresuposto, comentarios, aceptado, uploadedInputStream, fileDetail);

	}

	/**
	 * Modifica os parámetros de un presuposto existente. Devolve o presuposto
	 * modificada
	 * 
	 * @param id_preusposto
	 *            - OBLIGATORIO
	 * @param comentarios
	 * @param aceptado
	 * @param uploadedInputStream
	 *            (file)
	 * @param fileDetail
	 *            (file)
	 * @return
	 */
	@Path("/modificar")
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JSONObject<String, Object> modificar(@FormDataParam("id_presuposto") String idPresuposto,
			@FormDataParam("comentarios") String comentarios, @FormDataParam("aceptado") String aceptado,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método modificar() de presuposto.");

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			if (idPresuposto == null || idPresuposto.equals(""))
				throw new EmptyStackException();
			if (idPresuposto.equals("-1"))
				throw new NumberFormatException();
			if (aceptado != null) { // Se aceptado non é nulo, true ou false, devolvemos un erro.
				if (!aceptado.equals("true") && !aceptado.equals("false"))
					throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		logger.debug("Parámetros correctos.");

		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.modificar(user, idPresuposto, comentarios, aceptado, uploadedInputStream, fileDetail);
	}

	/**
	 * Obten un presuposto
	 * 
	 * @param idPresuposto
	 * @return
	 */
	@Path("/obter")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> obter(@QueryParam("id_presuposto") String idPresuposto) {

		logger.debug("Invocouse o método obter() de presuposto.");

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado.
		try {
			if (idPresuposto == null || idPresuposto.equals(""))
				throw new EmptyStackException();
			if (idPresuposto.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		logger.debug("Parámetros correctos.");

		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obter(user, idPresuposto);
	}

	/**
	 * Descarga o ficheiro do presuposto. Non devolve un JSON, so o estado da
	 * resposta e o ficheiro
	 * 
	 * @param idPresuposto
	 * @return
	 */
	@Path("/obterFicheiro")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterFicheiro(@QueryParam("id_presuposto") String idPresuposto) {

		logger.debug("Invocouse o método obterFicheiro() de presuposto.");

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado.
		try {
			if (idPresuposto == null || idPresuposto.equals(""))
				throw new EmptyStackException();
			if (idPresuposto.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException | EmptyStackException e) {
			return Response.status(Status.BAD_REQUEST).entity("").build();
		}
		logger.debug("Parámetros correctos.");

		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterFicheiro(user, idPresuposto);
	}

}
