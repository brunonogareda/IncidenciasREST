package es.brudi.incidencias.albarans;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;

import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.db.dao.AlbaranDAO;
import es.brudi.incidencias.db.dao.ComentarioDAO;
import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.documentos.XestionFicheiros;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONArray;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;

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
	 * @param id_incidencia
	 * @param nome
	 * @param proveedor
	 * @param num_albaran
	 * @param comentarios
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int id_incidencia, String nome, String proveedor, String num_albaran, String comentarios, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		String ruta_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirAlbaran()) {
			return Error.CREARALBARAN_SENPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARALBARAN_SENPERMISOS.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipo_ficheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			int nextId = AlbaranDAO.getNextId();
			nome_ficheiro = nextId+"."+tipo_ficheiro;
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_ALBARANS, inc.getData())+"/"+inc.getId();
			ruta_ficheiro = dir_ficheiro+"/"+nome_ficheiro;
		}
		
		int idA = AlbaranDAO.crear(id_incidencia, ruta_ficheiro, tipo_ficheiro, nome, proveedor, num_albaran, comentarios); //Creamos o albarán na táboa.
		if(idA <= 0) {
			return Error.CREARALBARAN_ERRODB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARALBARAN_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Albaran albaran = new Albaran(idA, id_incidencia, nome, proveedor, num_albaran, comentarios, ruta_ficheiro, tipo_ficheiro);
				
		logger.debug("Creouse o albarán correctamente: "+idA);

		//Engadimos o comentario de que se engadiu un albarán
		ComentarioDAO.crear(id_incidencia, user.getNome(), Comentario.ACCION_INSERTAR_ALBARAN, Comentario.MODIFICACION_TECNICOS, String.valueOf(idA));
		
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
	 * @param comentarios
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> modificar(Usuario user, int id, String nome, String num_albaran, String comentarios,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		String ruta_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarAlbaran()) {
			return Error.MODIFICARALBARAN_SENPERMISOS2.toJSONError();
		}

		Albaran alb = AlbaranDAO.getById(id);
		if(alb == null) {
			return Error.OBTERALBARAN_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(alb.getId_incidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARALBARAN_SENPERMISOS1.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipo_ficheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nome_ficheiro = id+"."+tipo_ficheiro;
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_ALBARANS, inc.getData())+"/"+inc.getId();
			ruta_ficheiro = dir_ficheiro+"/"+nome_ficheiro;
		}
				
		boolean aret = AlbaranDAO.modificar(id, ruta_ficheiro, tipo_ficheiro, nome, null, num_albaran, comentarios); //modificamos o presuposto na bd.
		if(!aret) {
			return Error.MODIFICARALBARAN_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(alb.getRuta_ficheiro() != null && !alb.getRuta_ficheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(alb.getRuta_ficheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+alb.getRuta_ficheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARALBARAN_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
				
		Albaran albaran = new Albaran(id, alb.getId_incidencia(), nome, alb.getProveedor(), num_albaran, comentarios, ruta_ficheiro, tipo_ficheiro);
		
		logger.debug("Modificad o albarán correctamente: "+id);

		//Engadimos o comentario de que se modificou o albarán
		ComentarioDAO.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_ALBARAN, Comentario.MODIFICACION_PUBLICA, String.valueOf(id));
		
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
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		if(!user.podeVerAlbaran()) {
			return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
		
		Albaran albaran = AlbaranDAO.getById(id);
		if(albaran == null) {
			return Error.OBTERALBARAN_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(albaran.getId_incidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
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
	 * @param id_incidencia
	 * @return
	 */
	public JSONObject<String, Object> obterXIncidencia(Usuario user, int id_incidencia) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		JSONArray<Object> jsonAlbarans = new JSONArray<Object>();

		
		if(!user.podeVerAlbaran()) {
			return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
		
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERALBARAN_SENPERMISOS2.toJSONError();
		}
		
		ArrayList<Albaran> Albarans = AlbaranDAO.getByIdIncidencia(id_incidencia);
		if(Albarans == null) {
			return Error.OBTERALBARAN_ERRORDB.toJSONError();
		}
		if(Albarans.size() <= 0) {
			return Error.OBTERALBARAN_NONEXISTENAINC.toJSONError();
		}
				
		logger.debug("Obtiveronse "+Albarans.size()+" albarans na incidencia: "+id_incidencia);

		for(Albaran i : Albarans)
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
		
		Albaran albaran = AlbaranDAO.getById(id);
		if(albaran == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(albaran.getId_incidencia());
		if(inc == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Response.status(Status.FORBIDDEN).build();
		}
		
		File file = XestionFicheiros.obterFicheiro(albaran.getRuta_ficheiro());
		if(file == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		logger.debug("Obtivose o albaran correctamente: "+id);

		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
	    
		return response.build();
	}
	
}
