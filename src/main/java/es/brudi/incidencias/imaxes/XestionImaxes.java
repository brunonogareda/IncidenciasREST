package es.brudi.incidencias.imaxes;

import java.io.File;
import java.io.InputStream;
import java.util.List;

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
import es.brudi.incidencias.util.JSONArray;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.imaxes.Imaxe.Tipo;
import es.brudi.incidencias.imaxes.db.ImaxeAccessor;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;

/**
 * 
 * Clase que xestiona as funcións relacionados coas imaxes.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionImaxes {
	
	private static Logger logger = Logger.getLogger(XestionImaxes.class);
	
	/**
	 * Crea unha nova imaxe na base de datos. E garda o ficheiro en local se existe.
	 * @param user
	 * @param idIncidencia
	 * @param nome
	 * @param comentarios
	 * @param antesDespois
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int idIncidencia, String nome, String comentarios, boolean antesDespois, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirImaxe()) {
			return Error.CREARIMAXE_SENPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARIMAXE_SENPERMISOS.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			int nextId = ImaxeAccessor.getNextId();
			nomeFicheiro = nextId+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaImaxes(), inc.getData())+"/"+inc.getId();
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
		Tipo tipo = Tipo.getTipoFromBool(antesDespois);
		int idI = ImaxeAccessor.crear(idIncidencia, rutaFicheiro, tipoFicheiro, nome, comentarios, tipo); //Creamos o presuposto na táboa.
		if(idI <= 0) {
			return Error.CREARIMAXE_ERRODB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARIMAXE_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Imaxe imaxe = new Imaxe(idI, idIncidencia, nome, tipo, rutaFicheiro, tipoFicheiro, comentarios);
				
		logger.debug("Creouse a imaxe correctamente: "+idI);

		//Engadimos o comentario de que se engadiu un presuposto
		ComentarioAccessor.crear(idIncidencia, user.getNome(), Comentario.ACCION_INSERTAR_IMAXE, Comentario.MODIFICACION_PUBLICA, String.valueOf(idI));
		
		if(!errFile)
			ret = Mensaxe.CREARIMAXE_OK.toJSONMensaxe();
		
		ret.put("Presuposto", imaxe.toJson());
		
		return ret;
	}

	/**
	 * Modifica unha imaxe existente.
	 * @param user
	 * @param id
	 * @param nome
	 * @param comentarios
	 * @param antes_despois
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> modificar(Usuario user, int id, String nome, String comentarios, String antesDespoisS,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarImaxe()) {
			return Error.MODIFICARIMAXE_SENPERMISOS2.toJSONError();
		}

		Imaxe imx = ImaxeAccessor.getById(id);
		if(imx == null) {
			return Error.OBTERIMAXE_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(imx.getIdIncidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARIMAXE_SENPERMISOS1.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nomeFicheiro = id+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaImaxes(), inc.getData())+"/"+inc.getId();
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
				
		boolean iret = ImaxeAccessor.modificar(id, rutaFicheiro, tipoFicheiro, nome, comentarios, antesDespoisS); //modificamos o presuposto na bd.
		if(!iret) {
			return Error.MODIFICARIMAXE_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(imx.getRutaFicheiro() != null && !imx.getRutaFicheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(imx.getRutaFicheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+imx.getRutaFicheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARIMAXE_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Imaxe imaxe = new Imaxe(id, imx.getIdIncidencia(), nome, Tipo.getTipoFromString(antesDespoisS), rutaFicheiro, tipoFicheiro, comentarios);
		
		logger.debug("Modificada a imaxe correctamente: "+id);

		//Engadimos o comentario de que se engadiu a imaxe
		ComentarioAccessor.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_IMAXE, Comentario.MODIFICACION_PUBLICA, String.valueOf(id));
		
		if(!errFile)
			ret = Mensaxe.MODIFICARIMAXE_OK.toJSONMensaxe();
		
		ret.put("Imaxe", imaxe.toJson());
		
		return ret;
	}
	
	/**
	 * Obten os datos de unha imaxe.
	 * @param user
	 * @param id
	 * @return
	 */
	public JSONObject<String, Object> obter(Usuario user, int id) {
		JSONObject<String, Object> ret;
		
		if(!user.podeVerImaxe()) {
			return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
		
		Imaxe imaxe = ImaxeAccessor.getById(id);
		if(imaxe == null) {
			return Error.OBTERIMAXE_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(imaxe.getIdIncidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
				
		logger.debug("Obtivose a imaxe correctamente: "+id);

		ret = Mensaxe.OBTERIMAXE_OK.toJSONMensaxe();
		ret.put("Imaxe", imaxe.toJson());
		
		return ret;
	}
	
	/**
	 * Obten os datos das imaxes mediante o id da incidencia
	 * @param user
	 * @param idIncidencia
	 * @return
	 */
	public JSONObject<String, Object> obterXIncidencia(Usuario user, int idIncidencia) {
		JSONObject<String, Object> ret;
		JSONArray<Object> jsonImaxes = new JSONArray<>();

		
		if(!user.podeVerImaxe()) {
			return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
		
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
		
		List<Imaxe> imaxes = ImaxeAccessor.getByIdIncidencia(idIncidencia);
		if(imaxes == null) {
			return Error.OBTERIMAXE_ERRORDB.toJSONError();
		}
		if(imaxes.isEmpty()) {
			return Error.OBTERIMAXE_NONEXISTENAINC.toJSONError();
		}
				
		logger.debug("Obtiveronse "+imaxes.size()+" imaxes na incidencia: "+idIncidencia);

		for(Imaxe i : imaxes)
			jsonImaxes.add(i.toJson());
		
		ret = Mensaxe.OBTERIMAXESINC_OK.toJSONMensaxe();
		ret.put("Imaxes", jsonImaxes);
		
		return ret;
	}

	/**
	 * Obten o ficheiro local.
	 * @param user
	 * @param id
	 * @return
	 */
	public Response obterFicheiro(Usuario user, int id) {

		if(!user.podeVerImaxe()) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Imaxe imaxe = ImaxeAccessor.getById(id);
		if(imaxe == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(imaxe.getIdIncidencia());
		if(inc == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Response.status(Status.FORBIDDEN).build();
		}
		
		File file = XestionFicheiros.obterFicheiro(imaxe.getRutaFicheiro());
		if(file == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		logger.debug("Obtivose a imaxe correctamente: "+id);

		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
	    
		return response.build();
	}
	
}
