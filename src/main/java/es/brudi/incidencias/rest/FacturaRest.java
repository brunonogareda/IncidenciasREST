package es.brudi.incidencias.rest;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JSONObject<String, Object> insertar(@DefaultValue("-1") @FormDataParam("idIncidencia") int idIncidencia,
											   @FormDataParam("idFactura") String idFactura, 
											   @FormDataParam("comentarios") String comentarios,
											   @FormDataParam("file") InputStream uploadedInputStream,
											   @FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método insertar() de factura.");

		// Compróbase que os parámetros obligatorios se pasaron 
		if(idIncidencia == -1 || idFactura == null || idFactura.isEmpty() || idFactura.equals("-1"))
			return Error.FALTANPARAM.toJSONError();

		logger.debug("Parámetros correctos.");
		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, idIncidencia, idFactura, comentarios, uploadedInputStream, fileDetail);
	}

	/**
	 * Modifica os parámetros de unha factura existente. Devolve a factura
	 * modificada
	 * 
	 * @param idFactura - OBLIGATORIO
	 * @param comentarios
	 * @param uploadedInputStream (file)
	 * @param fileDetail (file)
	 * @return
	 */
	@Path("{id : .+}")
	@PUT
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JSONObject<String, Object> modificar(@PathParam("id") String id,
												@FormDataParam("comentarios") String comentarios,
												@FormDataParam("file") InputStream uploadedInputStream,
												@FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método modificar() de factura.");

		// Compróbase que os parámetros obligatorios se pasaron.
		if (id == null || id.equals(""))
			return Error.FALTANPARAM.toJSONError();
		
		logger.debug("Parámetros correctos.");
		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.modificar(user, id, comentarios, uploadedInputStream, fileDetail);
	}

	/**
	 * Obten unha factura
	 * 
	 * @param idFactura
	 * @return
	 */
	@Path("{id : .+}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> obter(@PathParam("id") String idFactura) {
		logger.debug("Invocouse o método obter() de factura.");
		// Compróbase que os parámetros obligatorios se pasaron.
		if (idFactura == null || idFactura.equals(""))
			return Error.FALTANPARAM.toJSONError();

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
	@Path("{id : .+}/ficheiro")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterFicheiro(@PathParam("id") String idFactura) {
		
		logger.debug("Invocouse o método obterFicheiro() de factura.");
		// Compróbase que os parámetros obligatorios se pasaron
		if (idFactura == null || idFactura.equals(""))
			return Response.status(Status.BAD_REQUEST).entity("").build();

		logger.debug("Parámetros correctos.");
		XestionFacturas xest = new XestionFacturas();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterFicheiro(user, idFactura);
	}

}
