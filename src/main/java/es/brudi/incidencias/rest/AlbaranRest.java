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

import es.brudi.incidencias.albarans.XestionAlbarans;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.rest.util.Checkdb;
import es.brudi.incidencias.rest.util.Secured;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.util.Util;

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
	@Path("/insertar")
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public JSONObject<String, Object> insertar(@FormDataParam("id_incidencia") String idIncidenciaS,
    										   @FormDataParam("nome") String nome,
    										   @FormDataParam("proveedor") String proveedor,
    										   @FormDataParam("num_albaran") String numAlbaran,
    										   @FormDataParam("comentarios") String comentarios,
    										   @FormDataParam("file") InputStream uploadedInputStream,
    										   @FormDataParam("file") FormDataContentDisposition fileDetail) {	
		
        logger.debug("Invocouse o método insertar() de albarans.");
        
        int idIncidencia;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
      	try {
      		idIncidencia = Util.stringToInt(false, idIncidenciaS);
      		if(proveedor == null || proveedor.equals("")) {
      			throw new EmptyStackException();
      		}
      		if(uploadedInputStream == null || fileDetail == null) {
      			throw new EmptyStackException();
      		}
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      		
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
	@Path("/modificar")
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public JSONObject<String, Object> modificar(@FormDataParam("id") String idS,
    										    @FormDataParam("nome") String nome,
    										    @FormDataParam("num_albaran") String numAlbaran,
								    		    @FormDataParam("comentarios") String comentarios,
								    		    @FormDataParam("file") InputStream uploadedInputStream,
								   			    @FormDataParam("file") FormDataContentDisposition fileDetail) { 
		
        logger.debug("Invocouse o método modificar() de albaran.");
        
        int id;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
      	try {
      		id = Util.stringToInt(false, idS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	
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
	@Path("/obter")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> obter(@QueryParam("id") String idS) { 

        logger.debug("Invocouse o método obter() de albaran.");
                
        int id;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	try {
      		id = Util.stringToInt(false, idS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
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
	@Path("/obterXIncidencia")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject<String, Object> obterXIncidencia(@QueryParam("id_incidencia") String idIncidenciaS) { 

		logger.debug("Invocouse o método obterXIncidencia() de albarán.");
                
        int idIncidencia;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	try {
      		idIncidencia = Util.stringToInt(false, idIncidenciaS);
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
         
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterXIncidencia(user, idIncidencia);
    } 
	
	
	/**
	 * Descarga o ficheiro do albarán. Non devolve un JSON, so o estado da resposta e o ficheiro
	 * @param id
	 * @return
	 */
	@Path("/obterFicheiro")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterFicheiro(@QueryParam("id") String idS) {
		
		logger.debug("Invocouse o método obterFicheiro() de albarán.");
                
		int id;
		
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado.
      	try {
      		id = Util.stringToInt(false, idS);
      	}
      	catch(NumberFormatException | EmptyStackException e) {
      		return Response.status(Status.BAD_REQUEST).entity("").build();
      	}
      	logger.debug("Parámetros correctos.");
         
		XestionAlbarans xest = new XestionAlbarans();
		Usuario user = XestionUsuarios.getUsuario(securityContext);
		return xest.obterFicheiro(user, id);
	}
	
}
