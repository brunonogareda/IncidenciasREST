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

import es.brudi.incidencias.albarans.XestionAlbarans;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Servlet coas diferentes accións relacionadas con albarans, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a conexión coa base de datos é correcta,
 * e que o usuario que fai a petición teña a sesión aberta. E que os parámetros necesarios estean na petición.
 * Todos devolven un Http response con un json.
 * Excepto  o método de obterImaxe.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
@Path("/albaran")
@Checkdb
public class AlbaranRest {
	
	private Logger logger = Logger.getLogger(AlbaranRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	@Context private SecurityContext securityContext;

	/**
	 * Crea un novo albaran na base de datos. En caso de recibir un ficheiro gardao en local.
	 * Devolve os parámetros do albarán creada
	 * 
	 * @param idIncidenciaS - Identificador da incidencia a que corresponde co albarán. OBLIGATORIO
	 * @param nome - Nome que se lle quere dar o albaran
	 * @param proveedor - Proveedor do albarán. OBLIGATORIO
	 * @param numAlbaran
	 * @param comentarios
	 * @param uploadedInputStream (file)
	 * @param fileDetail (file)
	 * @return
	 */
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public JSONObject<String, Object> insertar(@DefaultValue("-1") @FormDataParam("idIncidencia") int idIncidencia,
    										   @FormDataParam("nome") String nome,
    										   @FormDataParam("proveedor") String proveedor,
    										   @FormDataParam("numAlbaran") String numAlbaran,
    										   @FormDataParam("comentarios") String comentarios,
    										   @FormDataParam("file") InputStream uploadedInputStream,
    										   @FormDataParam("file") FormDataContentDisposition fileDetail) {	
		
        logger.debug("Invocouse o método insertar() de albarans.");
		// Compróbase que os parámetros obligatorios se enviaron.
		if (proveedor == null || proveedor.equals("") || idIncidencia == -1 || uploadedInputStream == null
				|| fileDetail == null)
			return Error.FALTANPARAM.toJSONError();

      	logger.debug("Parámetros correctos.");
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.crear(user, idIncidencia, nome, proveedor, numAlbaran, comentarios, uploadedInputStream, fileDetail);
    } 

	/**
	 * Modifica os parámetros de un albaran existente. Devolve o imaxe modificada
	 * 
	 * @param id - OBLIGATORIO
	 * @param nome
	 * @param numAlbaran
	 * @param comentarios
	 * @param uploadedInputStream (file)
	 * @param fileDetail (file)
	 * @return
	 */
	@Path("{id}")
	@PUT
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public JSONObject<String, Object> modificar(@DefaultValue("-1") @PathParam("id") int id,
    										    @FormDataParam("nome") String nome,
    										    @FormDataParam("numAlbaran") String numAlbaran,
								    		    @FormDataParam("comentarios") String comentarios,
								    		    @FormDataParam("file") InputStream uploadedInputStream,
								   			    @FormDataParam("file") FormDataContentDisposition fileDetail) { 
		
        logger.debug("Invocouse o método modificar() de albaran.");
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
        if(id == -1)
        	return Error.FALTANPARAM.toJSONError();
      	
      	logger.debug("Parámetros correctos.");
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.modificar(user, id, nome, numAlbaran, comentarios, uploadedInputStream, fileDetail);
    } 
	
	/**
	 * Obten un albaran
	 * 
	 * @param id
	 * @return
	 */
	@Path("{id}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> obter(@DefaultValue("-1") @PathParam("id") int id) { 
        logger.debug("Invocouse o método obter() de albaran.");
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
        if(id == -1)
        	return Error.FALTANPARAM.toJSONError();

        logger.debug("Parámetros correctos.");
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obter(user, id);
    } 

	/**
	 * Obtén albráns mediante o id da incidencia
	 * 
	 * @param id
	 * @return
	 */
	@Path("/incidencia/{id}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> obterPorIncidencia(@DefaultValue("-1") @PathParam("id") int idIncidencia) { 

		logger.debug("Invocouse o método obterXIncidencia() de albarán.");
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	if(idIncidencia == -1)
      		return Error.FALTANPARAM.toJSONError();

      	logger.debug("Parámetros correctos.");
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterPorIncidencia(user, idIncidencia);
    } 
	
	
	/**
	 * Descarga o ficheiro do albarán. Non devolve un JSON, so o estado da resposta e o ficheiro
	 * @param id
	 * @return
	 */
	@Path("/{id}/ficheiro")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterFicheiro(@DefaultValue("-1") @PathParam("id") int id) {
		
		logger.debug("Invocouse o método obterFicheiro() de albarán.");
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	if(id == -1)
      		return Response.status(Status.BAD_REQUEST).entity("").build();

      	logger.debug("Parámetros correctos.");
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterFicheiro(user, id);
	}
	
}
