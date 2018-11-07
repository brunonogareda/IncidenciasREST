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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.presupostos.XestionPresupostos;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;

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
	 * @param idIncidencia - Identificador da incidencia a que corresponde o presuposto. OBLIGATORIO
	 * @param idPresuposto - Identificador do presuposto. OBLIGATORIO
	 * @param comentarios
	 * @param aceptado Se o presuposto está aceptado ou non ("true" -> true / En outrocaso false)
	 * @param uploadedInputStream (file)
	 * @param fileDetail (file)
	 * @return
	 */
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JSONObject<String, Object> insertar(@DefaultValue("-1") @FormDataParam("idIncidencia") int idIncidencia,
											   @FormDataParam("idPresuposto") String idPresuposto,
											   @FormDataParam("comentarios") String comentarios,
											   @FormDataParam("aceptado") boolean aceptado, 
											   @FormDataParam("file") InputStream uploadedInputStream,
											   @FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método insertar() de presuposto.");
		// Compróbase que os parámetros obligatorios
		if(idIncidencia == -1 || idPresuposto == null || idPresuposto.isEmpty() || idPresuposto.equals("-1"))
			return Error.FALTANPARAM.toJSONError();

		logger.debug("Parámetros correctos.");
		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, idIncidencia, idPresuposto, comentarios, aceptado, uploadedInputStream, fileDetail);
	}

	/**
	 * Modifica os parámetros de un presuposto existente. Devolve o presuposto
	 * modificada
	 * 
	 * @param id - OBLIGATORIO
	 * @param comentarios
	 * @param aceptado
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
												@FormDataParam("aceptado") String aceptado,
												@FormDataParam("file") InputStream uploadedInputStream,
												@FormDataParam("file") FormDataContentDisposition fileDetail) {

		logger.debug("Invocouse o método modificar() de presuposto.");

		// Compróbase que os parámetros obligatorios se pasaron
		if (id == null || id.isEmpty())
			return Error.FALTANPARAM.toJSONError();
		
		if (aceptado != null && // Se aceptado non é nulo, true ou false, devolvemos un erro.
			!aceptado.equalsIgnoreCase("true") && !aceptado.equalsIgnoreCase("false"))
				return Error.ERRORPARAM.toJSONError();

		logger.debug("Parámetros correctos.");
		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.modificar(user, id, comentarios, aceptado, uploadedInputStream, fileDetail);
	}

	/**
	 * Obten un presuposto
	 * 
	 * @param id
	 * @return
	 */
	@Path("{id : .+}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject<String, Object> obter(@PathParam("id") String id) {

		logger.debug("Invocouse o método obter() de presuposto.");
		// Compróbase que os parámetros obligatorios se pasaron
		if (id == null || id.equals(""))
			return Error.FALTANPARAM.toJSONError();

		logger.debug("Parámetros correctos.");
		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obter(user, id);
	}

	/**
	 * Descarga o ficheiro do presuposto. Non devolve un JSON, so o estado da
	 * resposta e o ficheiro
	 * 
	 * @param idPresuposto
	 * @return
	 */
	@Path("{id : .+}/ficheiro")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterFicheiro(@PathParam("id") String id) {

		logger.debug("Invocouse o método obterFicheiro() de presuposto.");
		// Compróbase que os parámetros obligatorios se pasaron.
		if (id == null || id.equals(""))
			return Response.status(Status.BAD_REQUEST).entity("").build();

		logger.debug("Parámetros correctos.");
		XestionPresupostos xest = new XestionPresupostos();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterFicheiro(user, id);
	}

}
