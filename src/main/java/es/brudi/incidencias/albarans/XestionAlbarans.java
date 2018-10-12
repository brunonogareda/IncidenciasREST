package es.brudi.incidencias.albarans;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import es.brudi.incidencias.albarans.db.AlbaranAccessor;
import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.comentarios.db.ComentarioAccessor;
import es.brudi.incidencias.documentos.XestionFicheiros;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONArray;
import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Clase que xestiona as funcións relacionados cos albarans.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionAlbarans {
	
	private static Logger logger = Logger.getLogger(XestionAlbarans.class);
	
	/**
	 * Crea un novo albarán na base de datos. E garda o ficheiro en local se existe.
	 * @param user
	 * @param idIncidencia
	 * @param nome
	 * @param proveedor
	 * @param numAlbaran
	 * @param comentarios
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int idIncidencia, String nome, String proveedor, String numAlbaran, String comentarios, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirAlbaran()) {
			return Error.CREARALBARAN_SENPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARALBARAN_SENPERMISOS.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			int nextId = AlbaranAccessor.getNextId();
			nomeFicheiro = nextId+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaAlbarans(), inc.getData())+"/"+inc.getId();
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
		
		int idA = AlbaranAccessor.crear(idIncidencia, rutaFicheiro, tipoFicheiro, nome, proveedor, numAlbaran, comentarios); //Creamos o albarán na táboa.
		if(idA <= 0) {
			return Error.CREARALBARAN_ERRODB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARALBARAN_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Albaran albaran = new Albaran(idA, idIncidencia, nome, proveedor, numAlbaran, comentarios, rutaFicheiro, tipoFicheiro);
				
		logger.debug("Creouse o albarán correctamente: "+idA);

		//Engadimos o comentario de que se engadiu un albarán
		ComentarioAccessor.crear(idIncidencia, user.getNome(), Comentario.ACCION_INSERTAR_ALBARAN, Comentario.MODIFICACION_TECNICOS, String.valueOf(idA));
		
		if(!errFile)
			ret = Mensaxe.CREARALBARAN_OK.toJSONMensaxe();
		
		ret.put("Albaran", albaran.toJson());
		
		return ret;
	}

	/**
	 * Modifica un albarán existente.
	 * @param user
	 * @param id
	 * @param nome
	 * @param numAlbaran
	 * @param comentarios
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> modificar(Usuario user, int id, String nome, String numAlbaran, String comentarios,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarAlbaran()) {
			return Error.MODIFICARALBARAN_SENPERMISOS2.toJSONError();
		}

		Albaran alb = AlbaranAccessor.getById(id);
		if(alb == null) {
			return Error.OBTERALBARAN_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(alb.getIdIncidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARALBARAN_SENPERMISOS1.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nomeFicheiro = id+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaAlbarans(), inc.getData())+"/"+inc.getId();
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
				
		boolean aret = AlbaranAccessor.modificar(id, rutaFicheiro, tipoFicheiro, nome, null, numAlbaran, comentarios); //modificamos o presuposto na bd.
		if(!aret) {
			return Error.MODIFICARALBARAN_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(alb.getRutaFicheiro() != null && !alb.getRutaFicheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(alb.getRutaFicheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+alb.getRutaFicheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARALBARAN_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
				
		Albaran albaran = new Albaran(id, alb.getIdIncidencia(), nome, alb.getProveedor(), numAlbaran, comentarios, rutaFicheiro, tipoFicheiro);
		
		logger.debug("Modificad o albarán correctamente: "+id);

		//Engadimos o comentario de que se modificou o albarán
		ComentarioAccessor.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_ALBARAN, Comentario.MODIFICACION_PUBLICA, String.valueOf(id));
		
		if(!errFile)
			ret = Mensaxe.MODIFICARALBARAN_OK.toJSONMensaxe();
		
		ret.put("Albaran", albaran.toJson());
		
		return ret;
	}
	
	/**
	 * Obten os datos de un albarán.
	 * @param user
	 * @param id
	 * @return
	 */
	public JSONObject<String, Object> obter(Usuario user, int id) {
		JSONObject<String, Object> ret;
		
		if(!user.podeVerAlbaran()) {
			return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
		
		Albaran albaran = AlbaranAccessor.getById(id);
		if(albaran == null) {
			return Error.OBTERALBARAN_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(albaran.getIdIncidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
				
		logger.debug("Obtivose o albarán correctamente: "+id);

		ret = Mensaxe.OBTERALBARAN_OK.toJSONMensaxe();
		ret.put("Albaran", albaran.toJson());
		
		return ret;
	}
	
	/**
	 * Obten os datos dos albaráns mediante o id da incidencia
	 * @param user
	 * @param idIncidencia
	 * @return
	 */
	public JSONObject<String, Object> obterXIncidencia(Usuario user, int idIncidencia) {
		JSONObject<String, Object> ret;
		JSONArray<Object> jsonAlbarans = new JSONArray<>();

		
		if(!user.podeVerAlbaran()) {
			return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
		
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(idIncidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
		
		List<Albaran> albarans = AlbaranAccessor.getByIdIncidencia(idIncidencia);
		if(albarans == null) {
			return Error.OBTERALBARAN_ERRORDB.toJSONError();
		}
		if(albarans.isEmpty()) {
			return Error.OBTERALBARAN_NONEXISTENAINC.toJSONError();
		}
				
		logger.debug("Obtiveronse "+albarans.size()+" albarans na incidencia: "+idIncidencia);

		for(Albaran i : albarans)
			jsonAlbarans.add(i.toJson());
		
		ret = Mensaxe.OBTERALBARANSINC_OK.toJSONMensaxe();
		ret.put("Albarans", jsonAlbarans);
		
		return ret;
	}

	/**
	 * Obten o ficheiro local.
	 * @param user
	 * @param id
	 * @return
	 */
	public Response obterFicheiro(Usuario user, int id) {

		if(!user.podeVerAlbaran()) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Albaran albaran = AlbaranAccessor.getById(id);
		if(albaran == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(albaran.getIdIncidencia());
		if(inc == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Response.status(Status.FORBIDDEN).build();
		}
		
		File file = XestionFicheiros.obterFicheiro(albaran.getRutaFicheiro());
		if(file == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		logger.debug("Obtivose o albaran correctamente: "+id);

		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
	    
		return response.build();
	}
	
}
