package es.brudi.incidencias.imaxes;

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
import es.brudi.incidencias.db.dao.ComentarioDAO;
import es.brudi.incidencias.db.dao.ImaxeDAO;
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
	 * @param id_incidencia
	 * @param nome
	 * @param comentarios
	 * @param antes_despois
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int id_incidencia, String nome, String comentarios, boolean antes_despois, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		String ruta_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirImaxe()) {
			return Error.CREARIMAXE_SENPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARIMAXE_SENPERMISOS.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipo_ficheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			int nextId = ImaxeDAO.getNextId();
			nome_ficheiro = nextId+"."+tipo_ficheiro;
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_IMAXES, inc.getData())+"/"+inc.getId();
			ruta_ficheiro = dir_ficheiro+"/"+nome_ficheiro;
		}
		
		int idI = ImaxeDAO.crear(id_incidencia, ruta_ficheiro, tipo_ficheiro, nome, comentarios, antes_despois); //Creamos o presuposto na táboa.
		if(idI <= 0) {
			return Error.CREARIMAXE_ERRODB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARIMAXE_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Imaxe imaxe = new Imaxe(idI, id_incidencia, nome, antes_despois, ruta_ficheiro, tipo_ficheiro, comentarios);
				
		logger.debug("Creouse a imaxe correctamente: "+idI);

		//Engadimos o comentario de que se engadiu un presuposto
		ComentarioDAO.crear(id_incidencia, user.getNome(), Comentario.ACCION_INSERTAR_IMAXE, Comentario.MODIFICACION_PUBLICA, String.valueOf(idI));
		
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
	public JSONObject<String, Object> modificar(Usuario user, int id, String nome, String comentarios, String antes_despoisS,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		String ruta_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarImaxe()) {
			return Error.MODIFICARIMAXE_SENPERMISOS2.toJSONError();
		}

		Imaxe imx = ImaxeDAO.getById(id);
		if(imx == null) {
			return Error.OBTERIMAXE_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(imx.getId_incidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARIMAXE_SENPERMISOS1.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipo_ficheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nome_ficheiro = id+"."+tipo_ficheiro;
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_IMAXES, inc.getData())+"/"+inc.getId();
			ruta_ficheiro = dir_ficheiro+"/"+nome_ficheiro;
			System.out.println(id);
			System.out.println(nome_ficheiro);
		}
				
		boolean iret = ImaxeDAO.modificar(id, ruta_ficheiro, tipo_ficheiro, nome, comentarios, antes_despoisS); //modificamos o presuposto na bd.
		if(!iret) {
			return Error.MODIFICARIMAXE_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(imx.getRuta_ficheiro() != null && !imx.getRuta_ficheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(imx.getRuta_ficheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+imx.getRuta_ficheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARIMAXE_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		boolean antes_despois = imx.isAntes_despois();
		if("antes".equalsIgnoreCase(antes_despoisS))
			antes_despois = false;
		if("despois".equalsIgnoreCase(antes_despoisS) || "despues".equalsIgnoreCase(antes_despoisS) || "después".equalsIgnoreCase(antes_despoisS))
			antes_despois = true;
		
		Imaxe imaxe = new Imaxe(id, imx.getId_incidencia(), nome, antes_despois, ruta_ficheiro, tipo_ficheiro, comentarios);
		
		logger.debug("Modificada a imaxe correctamente: "+id);

		//Engadimos o comentario de que se engadiu a imaxe
		ComentarioDAO.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_IMAXE, Comentario.MODIFICACION_PUBLICA, String.valueOf(id));
		
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
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		if(!user.podeVerImaxe()) {
			return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
		
		Imaxe imaxe = ImaxeDAO.getById(id);
		if(imaxe == null) {
			return Error.OBTERIMAXE_NONEXISTE.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(imaxe.getId_incidencia());
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
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
	 * @param id_incidencia
	 * @return
	 */
	public JSONObject<String, Object> obterXIncidencia(Usuario user, int id_incidencia) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		JSONArray<Object> jsonImaxes = new JSONArray<Object>();

		
		if(!user.podeVerImaxe()) {
			return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
		
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERIMAXE_SENPERMISOS2.toJSONError();
		}
		
		ArrayList<Imaxe> Imaxes = ImaxeDAO.getByIdIncidencia(id_incidencia);
		if(Imaxes == null) {
			return Error.OBTERIMAXE_ERRORDB.toJSONError();
		}
		if(Imaxes.size() <= 0) {
			return Error.OBTERIMAXE_NONEXISTENAINC.toJSONError();
		}
				
		logger.debug("Obtiveronse "+Imaxes.size()+" imaxes na incidencia: "+id_incidencia);

		for(Imaxe i : Imaxes)
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
		
		Imaxe imaxe = ImaxeDAO.getById(id);
		if(imaxe == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(imaxe.getId_incidencia());
		if(inc == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Response.status(Status.FORBIDDEN).build();
		}
		
		File file = XestionFicheiros.obterFicheiro(imaxe.getRuta_ficheiro());
		if(file == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		logger.debug("Obtivose a imaxe correctamente: "+id);

		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
	    
		return response.build();
	}
	
}
