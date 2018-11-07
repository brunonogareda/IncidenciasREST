package es.brudi.incidencias.presupostos;

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
import es.brudi.incidencias.presupostos.db.PresupostoAccessor;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
import es.brudi.incidencias.incidencias.estados.Estado;

/**
 * 
 * Clase que xestiona as funcións relacionados cos presupostos.
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
	 * @param idIncidencia
	 * @param idPresuposto
	 * @param comentarios
	 * @param aceptado
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int idIncidencia, String idPresuposto, String comentarios, boolean aceptado, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEngadirPresuposto())
			return Error.USER_NOPERMISOS.toJSONError();
		
		if(aceptado && !user.podeAceptarPresuposto())
			return Error.ACEPTARPRESUPOSTO_SENPERMISOS.toJSONError();

		Incidencia inc = IncidenciaAccessor.obterPorId(idIncidencia, user);
		if(inc == null)
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();

		if(inc.getPresuposto() != null)//A incidencia xa ten presuposto
			return Error.CREARPRESUPOSTO_EXISTE.toJSONError();

		//comprobamos se xa existe algun presuposto con ese Id.
		Presuposto presu = PresupostoAccessor.obterPorId(idPresuposto);
		if(presu != null)
			return Error.CREARPRESUPOSTO_DUPLICADO.toJSONError();

		if(!inc.getEstado().equals(Estado.PENDENTE_P)) //Comprobamos que o estado da incidencia é o correcto para engadir a factura.
			return Error.CREARPRESUPOSTO_ERRORESTADO.toJSONError();
		
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nomeFicheiro = idPresuposto.replaceAll("/", "-")+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaPresupostos());
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
		
		Presuposto presuposto = PresupostoAccessor.crear(idPresuposto, rutaFicheiro, tipoFicheiro, comentarios, aceptado); //Creamos o presuposto na táboa.
		if(presuposto == null)
			return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
		
		//Engadese o id de presuposto na incidencia correspondente e cambiase o estado
		if(aceptado) {
			if(!IncidenciaAccessor.modifcarPresupostoEstado(idIncidencia, idPresuposto, Estado.PENDENTE_R.getEstado()))
				return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
		}
		else {
			if(!IncidenciaAccessor.modifcarPresupostoEstado(idIncidencia, idPresuposto, Estado.PENDENTE_A.getEstado()))
				return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARPRESUPOSTO_FICHEIRO.toJSONError();
				errFile = true;
			}
		}

		logger.debug("Creouse o presuposto correctamente: "+idPresuposto);

		ComentarioAccessor.crear(idIncidencia, user.getId(), Comentario.ACCION_INSERTAR_PRESUPOSTO, Comentario.MODIFICACION_ADMINISTRACION, idPresuposto);
		
		if(!errFile)
			ret = Mensaxe.CREARPRESUPOSTO_OK.toJSONMensaxe();
		
		ret.put(Presuposto.JSON_TITLE, presuposto.toJson());
		return ret;
	}

	/**
	 * Modifica un presuposto existente.
	 * @param user
	 * @param idPresuposto
	 * @param comentarios
	 * @param aceptado
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	public JSONObject<String, Object> modificar(Usuario user, String idPresuposto, String comentarios, String aceptado,
			InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		JSONObject<String, Object> ret = new JSONObject<>();

		String nomeFicheiro = null;
		String tipoFicheiro = null;
		String dirFicheiro = null;
		String rutaFicheiro = null;
		boolean errFile = false;
		
		if(!user.podeEditarPresuposto() && (comentarios != null || uploadedInputStream != null) )
			return Error.MODIFICARPRESUPOSTO_SENPERMISOS.toJSONError();

		if(aceptado!=null && !user.podeAceptarPresuposto())
			return Error.ACEPTARPRESUPOSTO_SENPERMISOS.toJSONError();

		Presuposto presuOld = PresupostoAccessor.obterPorId(idPresuposto, user);
		if(presuOld == null)
			return Error.OBTERPRESUPOSTO_NONEXISTE.toJSONError();
			
		if(uploadedInputStream != null && fileDetail != null) { //Se existe, obtemos os datos para o ficheiro
			tipoFicheiro = FilenameUtils.getExtension(fileDetail.getFileName());
			nomeFicheiro = idPresuposto.replaceAll("/", "-")+"."+tipoFicheiro;
			dirFicheiro = XestionFicheiros.getRuteToFile(XestionFicheiros.getRutaPresupostos());
			rutaFicheiro = dirFicheiro+"/"+nomeFicheiro;
		}
		
		Presuposto presuposto = PresupostoAccessor.modificar(idPresuposto, rutaFicheiro, tipoFicheiro, comentarios, aceptado, presuOld.isAceptado()); //modificamos o presuposto na bd.
		if(presuposto == null)
			return Error.MODIFICARPRESUPOSTO_ERRORDB.toJSONError();
		
		if(aceptado != null && aceptado.equalsIgnoreCase("true") && !presuOld.isAceptado())  {
			for(int inc : presuOld.getIdIncidencias()) {
				if(!IncidenciaAccessor.modifcarPresupostoEstado(inc, idPresuposto, Estado.PENDENTE_R.getEstado()))
					return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
			}
		}
		else if(aceptado != null && aceptado.equals("false") && presuOld.isAceptado()) {
			for(int inc : presuOld.getIdIncidencias()) {
				if(!IncidenciaAccessor.modifcarPresupostoEstado(inc, idPresuposto, Estado.PENDENTE_A.getEstado())) 
					return Error.CREARPRESUPOSTO_ERRORDB.toJSONError();
			}
		}
		
		if(uploadedInputStream != null) {//Se existe, garda o ficheiro en local.
			if(presuOld.getRutaFicheiro() != null && !presuOld.getRutaFicheiro().equals("")) {//Se existe boramos o ficheiro antigo.
				if(!XestionFicheiros.borrar(presuOld.getRutaFicheiro())) {
					logger.error("Erro o eliminar o ficheiro: "+presuOld.getRutaFicheiro());
				}
			}
			
			String path = XestionFicheiros.subirFicheiroEGardar(uploadedInputStream, dirFicheiro, nomeFicheiro);
			if(path == null ) {
				ret = Error.CREARPRESUPOSTO_FICHEIRO.toJSONError();
				errFile = true;
			}
		}
		
		logger.debug("Modificado o presuposto correctamente: "+idPresuposto);
		//Engadimos o comentario de que se modificou o presuposto para cada incidencia.
		for(int inc : presuOld.getIdIncidencias())
			ComentarioAccessor.crear(inc, user.getId(), Comentario.ACCION_MODIFICAR_PRESUPOSTO, Comentario.MODIFICACION_ADMINISTRACION, idPresuposto);
		
		if(!errFile)
			ret = Mensaxe.MODIFICARPRESUPOSTO_OK.toJSONMensaxe();
		
		ret.put(Presuposto.JSON_TITLE, presuposto.toJson());
		return ret;
	}
	
	/**
	 * Obten os datos de un presuposto.
	 * @param user
	 * @param idPresuposto
	 * @return
	 */
	public JSONObject<String, Object> obter(Usuario user, String idPresuposto) {
		JSONObject<String, Object> ret;
		
		if(!user.podeVerPresuposto())
			return Error.OBTERPRESUPOSTO_SENPERMISOS2.toJSONError();
		
		logger.debug("Buscando presuposto con ID: "+idPresuposto);
		Presuposto presuposto = PresupostoAccessor.obterPorId(idPresuposto, user);
		if(presuposto == null)
			return Error.OBTERPRESUPOSTO_NONEXISTE.toJSONError();
				
		logger.debug("Obtivose o presuposto correctamente: "+idPresuposto);
		ret = Mensaxe.OBTERPRESUPOSTO_OK.toJSONMensaxe();
		ret.put(Presuposto.JSON_TITLE, presuposto.toJson());
		
		return ret;
	}

	/**
	 * Obten o ficheiro local.
	 * @param user
	 * @param idpresuposto
	 * @return
	 */
	public Response obterFicheiro(Usuario user, String idPresuposto) {

		if(!user.podeVerPresuposto())
			return Response.status(Status.FORBIDDEN).build();
		
		logger.debug("Buscando ficheiro de presuposto con ID: "+idPresuposto);
		Presuposto presuposto = PresupostoAccessor.obterPorId(idPresuposto, user);
		if(presuposto == null || presuposto.getRutaFicheiro() == null)
			return Response.status(Status.NOT_FOUND).build();
		
		File file = XestionFicheiros.obterFicheiro(presuposto.getRutaFicheiro());
		if(file == null)
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		
		logger.debug("Obtivose o presuposto correctamente: "+idPresuposto);
		ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition", "attachment; filename="+file.getName());
		return response.build();
	}
	
}
