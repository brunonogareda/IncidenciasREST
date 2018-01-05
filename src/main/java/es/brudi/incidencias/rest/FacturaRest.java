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

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.facturas.XestionFacturas;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionUsuarios;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.util.Util;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * 
 * Servlet coas diferentes accións relacionadas con facturas, que pode realizar o usuario mediante REST.
 * 
 * Todos estes métodos comproban en primeiro lugar que a conexión coa base de datos é correcta,
 * e que o usuario que fai a petición teña a sesión aberta. E que os parámetros necesarios estean na petición.
 * Todos devolven un Http response con un json.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 201
 * 
 */
@Path("/factura")
public class FacturaRest {
	
	private Logger logger = Logger.getLogger(FacturaRest.class);
	
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;

	@Path("/insertar")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public JSONObject<String, Object> insertar(@FormDataParam("id_incidencia") String id_incidenciaS,
 								    		   @FormDataParam("id_factura") String id_factura,
								    		   @FormDataParam("comentarios") String comentarios,
								    		   @FormDataParam("file") InputStream uploadedInputStream,
								   			   @FormDataParam("file") FormDataContentDisposition fileDetail) { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();

        logger.debug("Invocouse o método insertar() de factura.");
        
        int id_incidencia = -1;
        
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
      	try {
      		id_incidencia = Util.stringToInt(false, id_incidenciaS);
      		if(id_factura == null || id_factura.equals(""))
      			throw new EmptyStackException();
      		if(id_factura.equals("-1"))
      			throw new NumberFormatException();
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionFacturas xest = new XestionFacturas();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	Usuario user = xestu.getUsuario(req);
	        	json = xest.crear(user, id_incidencia, id_factura, comentarios, uploadedInputStream, fileDetail);
	        }
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    } 

	@Path("/modificar")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public JSONObject<String, Object> modificar(@FormDataParam("id_factura") String id_factura,
								    		    @FormDataParam("comentarios") String comentarios,
								    		    @FormDataParam("file") InputStream uploadedInputStream,
								   			    @FormDataParam("file") FormDataContentDisposition fileDetail) { 
		
		JSONObject<String, Object> json = new JSONObject<String, Object>();

        logger.debug("Invocouse o método modificar() de factura.");
                
        //Compróbase que os parámetros obligatorios se pasaron e que están no formato adecuado, convertindo os string en int en caso de ser necesario
      	try {
      		if(id_factura == null || id_factura.equals(""))
      			throw new EmptyStackException();
      		if(id_factura.equals("-1"))
      			throw new NumberFormatException();
      	}
      	catch(NumberFormatException e) {
      		return Error.ERRORPARAM.toJSONError();
      	}
      	catch(EmptyStackException e) {
      		return Error.FALTANPARAM.toJSONError();
      	}
      	logger.debug("Parámetros correctos.");
        if(DBConnectionManager.getConnection() != null ) {
         
        	XestionFacturas xest = new XestionFacturas();
        	XestionUsuarios xestu = new XestionUsuarios();
        	
        	json = xestu.checkLogin(req);
	        if (json == null) {
	        	Usuario user = xestu.getUsuario(req);
	        	json = xest.modificar(user, id_factura, comentarios, uploadedInputStream, fileDetail);
	        }
        }
        else {
        	logger.warn("Non existe conexión coa base de datos.");
        	json = Error.DATABASE.toJSONError();
        }
        
        return json;
    } 
	
}
