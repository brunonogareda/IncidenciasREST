package es.brudi.incidencias.presupostos;

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;

import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.db.dao.ComentarioDAO;
import es.brudi.incidencias.db.dao.IncidenciaDAO;
import es.brudi.incidencias.db.dao.PresupostoDAO;
import es.brudi.incidencias.documentos.XestionFicheiros;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.estados.Estado;
import es.brudi.incidencias.util.Util;

/**
 * 
 * Clase que xestiona as funcións relacionados coas facturas.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionPresupostos {
	
	private static Logger logger = Logger.getLogger(XestionPresupostos.class);
	
	/**
	 * Crea un novo presuposto na base de datos. E garda o ficheiro en local se existe.
	 * @param user
	 * @param id_incidencia
	 * @param id_presuposto
	 * @param comentarios
	 * @param aceptado
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int id_incidencia, String id_presuposto, String comentarios, boolean aceptado, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		String ruta_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirPresuposto()) {
			return Error.USER_NOPERMISOS.toJSONError();
		}
		Incidencia inc = IncidenciaDAO.getIncidenciaById(id_incidencia);
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREARPRESUPOSTO_SENPERMISOS.toJSONError();
		}
		if(aceptado && !user.podeAceptarPresuposto()) {
			return Error.ACEPTARPRESUPOSTO_SENPERMISOS.toJSONError();
		}
		if(inc.getPresuposto() != null) {//A incidencia xa ten presuposto
			return Error.CREARPRESUPOSTO_EXISTE.toJSONError();
		}
		//comprobamos se xa existe algun presuposto con ese Id.
		Presuposto presu = PresupostoDAO.getById(id_presuposto);
		if(presu != null) {
			return Error.CREARPRESUPOSTO_DUPLICADO.toJSONError();
		}
		if(!inc.getEstado().equals(Estado.PENDENTE_P)) { //Comprobamos que o estado da incidencia é o correcto para engadir a factura.
			return Error.CREARPRESUPOSTO_ERRORESTADO.toJSONError();
		}
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipo_ficheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nome_ficheiro = id_presuposto.replaceAll("/", "-")+'.'+tipo_ficheiro;
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_PRESUPOSTOS, inc.getData());
			ruta_ficheiro = dir_ficheiro+'/'+nome_ficheiro;
		}
		
		boolean Pret = PresupostoDAO.crear(id_presuposto, ruta_ficheiro, tipo_ficheiro, comentarios, aceptado); //Creamos o presuposto na táboa.
		if(!Pret) {
			return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARPRESUPOSTO_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Presuposto presuposto = PresupostoDAO.getById(id_presuposto); //Buscamos o presuposto que acabamos de insertar
		if(presuposto == null) {
			return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
		}
		//Engadese o id de presuposto na incidencia correspondente e cambiase o estado
		if(!IncidenciaDAO.modifcarPresupostoEstado(id_incidencia, id_presuposto, Estado.PENDENTE_R.getEstado())) { 
			return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
		}
		
		logger.debug("Creouse o presuposto correctamente: "+id_presuposto);

		Timestamp data = Util.obterTimestampActual();
		//Engadimos o comentario de que se engadiu un presuposto
		ComentarioDAO.crear(id_incidencia, user.getNome(), Comentario.ACCION_INSERTAR_PRESUPOSTO, Comentario.COMENTARIO_ADMINISTRACION, id_presuposto, data);
		
		if(!errFile)
			ret = Mensaxe.CREARPRESUPOSTO_OK.toJSONMensaxe();
		
		ret.put("Presuposto", presuposto.toJson());
		
		return ret;
	}

	/**
	 * Modifica un presuposto existente.
	 * @param user
	 * @param id_presuposto
	 * @param comentarios
	 * @param aceptado
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> modificar(Usuario user, String id_presuposto, String comentarios, String aceptado,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();

		String nome_ficheiro = null;
		String tipo_ficheiro = null;
		String dir_ficheiro = null;
		String ruta_ficheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarPresuposto() && (comentarios != null || uploadedInputStream != null) ) {
			return Error.MODIFICARPRESUPOSTO_SENPERMISOS2.toJSONError();
		}
		if(aceptado!=null && !user.podeAceptarPresuposto()) {
			return Error.ACEPTARPRESUPOSTO_SENPERMISOS.toJSONError();
		}
		ArrayList<Incidencia> incL = IncidenciaDAO.get(0, 0, 0, null, null, null, null, null, id_presuposto, null, null, null, null, 0, 0);
		if(incL.size()!=1) {
			return Error.OBTERPRESUPOSTO_NONEXISTE.toJSONError();
		}
		Incidencia inc = incL.get(0);
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.MODIFICARPRESUPOSTO_SENPERMISOS1.toJSONError();
		}
		Presuposto presu = PresupostoDAO.getById(id_presuposto);
		if(presu == null) {
			return Error.OBTERPRESUPOSTO_NONEXISTE.toJSONError();
		}
			
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipo_ficheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nome_ficheiro = id_presuposto.replaceAll("/", "-")+'.'+tipo_ficheiro;
			dir_ficheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.RUTA_PRESUPOSTOS, inc.getData());
			ruta_ficheiro = dir_ficheiro+'/'+nome_ficheiro;
		}
		
		boolean pret = PresupostoDAO.modificar(id_presuposto, ruta_ficheiro, tipo_ficheiro, comentarios, aceptado); //modificamos o presuposto na bd.
		if(!pret) {
			return Error.MODIFICARPRESUPOSTO_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(presu.getRuta_ficheiro() != null && !presu.getRuta_ficheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(presu.getRuta_ficheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+presu.getRuta_ficheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dir_ficheiro, nome_ficheiro);
			if(path == null ) {
				ret = Error.CREARPRESUPOSTO_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		Presuposto presuposto = PresupostoDAO.getById(id_presuposto); //Buscamos o presuposto que acabamos de modificar
		if(presuposto == null) {
			return Error.MODIFICARPRESUPOSTO_ERRORDB.toJSONError();
		}
		
		logger.debug("Modificado o presuposto correctamente: "+id_presuposto);

		Timestamp data = Util.obterTimestampActual();
		//Engadimos o comentario de que se modificou o presuposto
		ComentarioDAO.crear(inc.getId(), user.getNome(), Comentario.ACCION_MODIFICAR_PRESUPOSTO, Comentario.COMENTARIO_ADMINISTRACION, id_presuposto, data);
		
		if(!errFile)
			ret = Mensaxe.MODIFICARPRESUPOSTO_OK.toJSONMensaxe();
		
		ret.put("Presuposto", presuposto.toJson());
		
		return ret;
	}
	
	/**
	 * Obten os datos de un presuposto.
	 * @param user
	 * @param id_presuposto
	 * @return
	 */
	public JSONObject<String, Object> obter(Usuario user, String id_presuposto) {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		if(!user.podeVerPresuposto()) {
			return Error.OBTERPRESUPOSTO_SENPERMISOS2.toJSONError();
		}
		
		ArrayList<Incidencia> incL = IncidenciaDAO.get(0, 0, 0, null, null, null, null, null, id_presuposto, null, null, null, null, 0, 0);
		if(incL.size()!=1) {
			return Error.OBTERPRESUPOSTO_NONEXISTE.toJSONError();
		}
		Incidencia inc = incL.get(0);
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
				return Error.OBTERPRESUPOSTO_SENPERMISOS2.toJSONError();
		}
		Presuposto presuposto = PresupostoDAO.getById(id_presuposto);
		if(presuposto == null) {
			return Error.OBTERPRESUPOSTO_NONEXISTE.toJSONError();
		}
				
		logger.debug("Obtivose o presuposto correctamente: "+id_presuposto);

		ret = Mensaxe.OBTERPRESUPOSTO_OK.toJSONMensaxe();
		ret.put("Presuposto", presuposto.toJson());
		
		return ret;
	}

	/**
	 * Obten o ficheiro local.
	 * @param user
	 * @param id_presuposto
	 * @return
	 */
	public Response obterFicheiro(Usuario user, String id_presuposto) {

		if(!user.podeVerPresuposto()) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		ArrayList<Incidencia> incL = IncidenciaDAO.get(0, 0, 0, null, null, null, null, null, id_presuposto, null, null, null, null, 0, 0);
		if(incL.size()!=1) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Incidencia inc = incL.get(0);
		if(inc.getInstalacion().getCliente().getCod_cliente() != user.getCliente().getCod_cliente() &&
		user.getCliente().getCod_cliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Response.status(Status.FORBIDDEN).build();
		}
		Presuposto presuposto = PresupostoDAO.getById(id_presuposto);
		if(presuposto == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		File file = XestionFicheiros.obterFicheiro(presuposto.getRuta_ficheiro());
		if(file == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		
		logger.debug("Obtivose o presuposto correctamente: "+id_presuposto);

		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
	    
		return response.build();
	}
	
}
