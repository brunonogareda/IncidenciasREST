package es.brudi.incidencias.facturas;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.comentarios.db.ComentarioAccessor;
import es.brudi.incidencias.documentos.XestionFicheiros;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.facturas.db.FacturaAccessor;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
import es.brudi.incidencias.incidencias.estados.Estado;

/**
 * 
 * Clase que xestiona as funcións relacionados coas facturas.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionFacturas {
	
	private static Logger logger = Logger.getLogger(XestionFacturas.class);
	
	/**
	 * Crea unha nova factura na base de datos. E garda o ficheiro en local se existe.
	 * @param user
	 * @param idIncidencia
	 * @param idFactura
	 * @param comentarios
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int idIncidencia, String idFactura, String comentarios, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirFactura()) {
			return Error.USER_NOPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARFACTURA_SENPERMISOS.toJSONError();
		}
		if(inc.getFactura() != null) {//A incidencia xa ten factura.
			return Error.CREARFACTURA_EXISTE.toJSONError();
		}
		//comprobamos se xa existe algunha factura con ese Id.
		Factura fact = FacturaAccessor.getById(idFactura);
		if(fact != null) {
			return Error.CREARFACTURA_DUPLICADA.toJSONError();
		}
		if(!inc.getEstado().equals(Estado.PENDENTE_F)) { //Comprobamos que o estado da incidencia é o correcto para engadir a factura.
			return Error.CREARFACTURA_ERRORESTADO.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nomeFicheiro = idFactura.replaceAll("/", "-")+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaFacturas(), inc.getData());
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
		
		boolean fret = FacturaAccessor.crear(idFactura, rutaFicheiro, tipoFicheiro, comentarios); //Creamos a factura na táboa.
		if(!fret) {
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARFACTURA_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Factura factura = FacturaAccessor.getById(idFactura); //Buscamos a factura que acabamos de insertar
		if(factura == null) {
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		//Engadese o id de factura na incidencia correspondente e cambiase o estado
		if(!IncidenciaAccessor.modifcarFacturaEstado(idIncidencia, idFactura, Estado.FACTURADO.getEstado())) { 
			return Error.CREARFACTURA_ERRORDB.toJSONError();
		}
		
		logger.debug("Creouse a factura correctamente: "+idFactura);

		//Engadimos o comentario de que se engadiu unha factura
		ComentarioAccessor.crear(idIncidencia, user.getNome(), Comentario.ACCION_INSERTAR_FACTURA, Comentario.MODIFICACION_ADMINISTRACION_BRUDI, idFactura);
		
		if(!errFile)
			ret = Mensaxe.CREARFACTURA_OK.toJSONMensaxe();
		
		ret.put("Factura", factura.toJson());
		
		return ret;
	}

	/**
	 * Modifica unha factura existente.
	 * @param user
	 * @param idFactura
	 * @param comentarios
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> modificar(Usuario user, String idFactura, String comentarios,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarFactura()) {
			return Error.MODIFICARFACTURA_SENPERMISOS2.toJSONError();
		}
		
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorFactura(idFactura);
		if(inc == null) {
			return Error.OBTERFACTURA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARFACTURA_SENPERMISOS1.toJSONError();
		}
		Factura fact = FacturaAccessor.getById(idFactura);
		if(fact == null) {
			return Error.OBTERFACTURA_NONEXISTE.toJSONError();
		}
			
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nomeFicheiro = idFactura.replaceAll("/", "-")+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaFacturas(), inc.getData());
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
		
		boolean fret = FacturaAccessor.modificar(idFactura, rutaFicheiro, tipoFicheiro, comentarios); //Creamos a factura na táboa.
		if(!fret) {
			return Error.MODIFICARFACTURA_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(fact.getRutaFicheiro() != null && !fact.getRutaFicheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(fact.getRutaFicheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+fact.getRutaFicheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARFACTURA_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Factura factura = FacturaAccessor.getById(idFactura); //Buscamos a factura que acabamos de insertar
		if(factura == null) {
			return Error.MODIFICARFACTURA_ERRORDB.toJSONError();
		}
		
		logger.debug("Modificada a factura correctamente: "+idFactura);

		//Engadimos o comentario de que se engadiu unha factura
		ComentarioAccessor.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_FACTURA, Comentario.MODIFICACION_ADMINISTRACION_BRUDI, idFactura);
		
		if(!errFile)
			ret = Mensaxe.MODIFICARFACTURA_OK.toJSONMensaxe();
		
		ret.put("Factura", factura.toJson());
		
		return ret;
	}
	
	/**
	 * Obten os datos de unha factura.
	 * @param user
	 * @param idFactura
	 * @return
	 */
	public JSONObject<String, Object> obter(Usuario user, String idFactura) {
		JSONObject<String, Object> ret;
		
		if(!user.podeVerFactura()) {
			return Error.OBTERFACTURA_SENPERMISOS2.toJSONError();
		}
		
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorFactura(idFactura);
		if(inc == null) {
			return Error.OBTERFACTURA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARFACTURA_SENPERMISOS1.toJSONError();
		}
		Factura factura = FacturaAccessor.getById(idFactura);
		if(factura == null) {
			return Error.OBTERFACTURA_NONEXISTE.toJSONError();
		}
				
		logger.debug("Obtivose a factura correctamente: "+idFactura);

		ret = Mensaxe.OBTERFACTURA_OK.toJSONMensaxe();
		ret.put("Factura", factura.toJson());
		
		return ret;
	}

	/**
	 * Obten o ficheiro local.
	 * @param user
	 * @param idFactura
	 * @return
	 */
	public Response obterFicheiro(Usuario user, String idFactura) {

		if(!user.podeVerFactura()) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorFactura(idFactura);
		if(inc == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Response.status(Status.FORBIDDEN).build();
		}
		Factura factura = FacturaAccessor.getById(idFactura);
		if(factura == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		File file = XestionFicheiros.obterFicheiro(factura.getRutaFicheiro());
		if(file == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		
		logger.debug("Obtivose a factura correctamente: "+idFactura);

		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
	    
		return response.build();
	}
	
}
