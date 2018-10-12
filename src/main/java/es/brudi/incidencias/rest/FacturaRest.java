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

import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.facturas.XestionFacturas;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.util.Util;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * 
 * Servlet coas diferentes accións relacionadas con facturas, que pode realizar
 * o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a conexión coa base de
 * datos é correcta, e que o usuario que fai a petición teña a sesión aberta. E
 * que os parámetros necesarios estean na petición. Todos devolven un Http
 * response con un json. Excepto o método de obterFicheiro
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
@Path("/factura")
@Checkdb
public class FacturaRest {

	private Logger logger = Logger.getLogger(FacturaRest.class);

	@Context
	private HttpServletRequest req;
	@Context
	private HttpServletResponse res;
	@Context
	private SecurityContext securityContext;

	/**
	 * Crea unha nova factura na base de datos. En caso de recibir un ficheiro
	 * gardao en local. Devolve a factura creada
	 * 
	 * @param idIncidenciaS
	 *            - Identificador da incidencia a que corresponde a factura.
	 *            OBLIGATORIO
	 * @param idFactura
	 *            - Identificador da factura. OBLIGATORIO
	 * @param comentarios
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
			@FormDataParam("id_factura") String idFactura, @FormDataParam("comentarios") String comentarios,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método insertar() de factura.");

		int idIncidencia;

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			idIncidencia = Util.stringToInt(false, idIncidenciaS);
			if (idFactura == null || idFactura.equals(""))
				throw new EmptyStackException();
			if (idFactura.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		logger.debug("Parámetros correctos.");

		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, idIncidencia, idFactura, comentarios, uploadedInputStream, fileDetail);
	}

	/**
	 * Modifica os parámetros de unha factura existente. Devolve a factura
	 * modificada
	 * 
	 * @param idFactura
	 *            - OBLIGATORIO
	 * @param comentarios
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
	public JSONObject<String, Object> modificar(@FormDataParam("id_factura") String idFactura,
			@FormDataParam("comentarios") String comentarios, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método modificar() de factura.");

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado, convertindo os string en int en caso de ser necesario
		try {
			if (idFactura == null || idFactura.equals(""))
				throw new EmptyStackException();
			if (idFactura.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		logger.debug("Parámetros correctos.");

		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.modificar(user, idFactura, comentarios, uploadedInputStream, fileDetail);
	}

	/**
	 * Obten unha factura
	 * 
	 * @param idFactura
	 * @return
	 */
	@Path("/obter")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> obter(@QueryParam("id_factura") String idFactura) {

		logger.debug("Invocouse o método obter() de factura.");

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado.
		try {
			if (idFactura == null || idFactura.equals(""))
				throw new EmptyStackException();
			if (idFactura.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			return Error.ERRORPARAM.toJSONError();
		} catch (EmptyStackException e) {
			return Error.FALTANPARAM.toJSONError();
		}
		logger.debug("Parámetros correctos.");

		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obter(user, idFactura);
	}

	/**
	 * Descarga o ficheiro da factura. Non devolve un JSON, so o estado da resposta
	 * e o ficheiro
	 * 
	 * @param idFactura
	 * @return
	 */
	@Path("/obterFicheiro")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterFicheiro(@QueryParam("id_factura") String idFactura) {

		logger.debug("Invocouse o método obterFicheiro() de factura.");

		// Compróbase que os parámetros obligatorios se pasaron e que están no formato
		// adecuado.
		try {
			if (idFactura == null || idFactura.equals(""))
				throw new EmptyStackException();
			if (idFactura.equals("-1"))
				throw new NumberFormatException();
		} catch (NumberFormatException | EmptyStackException e) {
			return Response.status(Status.BAD_REQUEST).entity("").build();
		}
		logger.debug("Parámetros correctos.");

		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterFicheiro(user, idFactura);
	}

}
