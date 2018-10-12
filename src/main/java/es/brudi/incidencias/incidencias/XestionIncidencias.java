package es.brudi.incidencias.incidencias;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import es.brudi.incidencias.util.JSONArray;
import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.comentarios.db.ComentarioAccessor;
import es.brudi.incidencias.error.Error;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
import es.brudi.incidencias.incidencias.estados.Estado;
import es.brudi.incidencias.mensaxes.Mensaxe;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.Util;
import es.brudi.incidencias.instalacions.Instalacion;
import es.brudi.incidencias.instalacions.db.InstalacionAccessor;

/**
 * 
 * Clase que xestiona as funcións relacionados coas incidencias.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class XestionIncidencias {
	
	private static Logger logger = Logger.getLogger(XestionIncidencias.class);
	
	/**
	 * Crea unha nova incidencia.
	 * @param user - Usuario que realiza a petición.
	 * @param codParte
	 * @param ot
	 * @param idInstalacion
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param solPresuposto
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int codParte, int ot, int idInstalacion, String zonaApartamento,
			String descripcionCurta, String observacions, boolean solPresuposto) {
		JSONObject<String, Object> ret;
		
		Timestamp data = Util.obterTimestampActual();
		String estado = Estado.PENDENTE_R.getEstado();
		String autor = user.getNome();
		
		Instalacion inst = InstalacionAccessor.getInstalacionById(idInstalacion);
		
		if(inst == null) { //Comprobamos se existe a instalación
			return Error.CREATEINCIDENCIA_NONEXISTEINST.toJSONError();
		}
		if(inst.getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
		  user.getCliente().getCodCliente() != 0) {  //Comprobamos que a instalación pertence o usuario que crea a incidencia ou é 0.
			return Error.CREATEINCIDENCIA_INSTPERMISOS.toJSONError();
		}
		
		if(!user.podeMarcarSolPresuposto() && solPresuposto) { //Comprobamos permisos de solicitar presuposto.
			return Error.USER_NOPERMISOS.toJSONError();
		}
		if(!user.podeCrearIncidencia()) { //Comprobamos permisos de creación
			return Error.USER_NOPERMISOS.toJSONError();
		}
		
		if(solPresuposto) //En caso de que se solicite presuposto, o estado inicial é diferente.
			estado = Estado.PENDENTE_P.getEstado();
		
		int id = IncidenciaAccessor.crear(codParte, ot, idInstalacion, zonaApartamento,
				descripcionCurta, observacions, estado, solPresuposto, data, autor);
		
		if(id < 0) {
			return Error.CREATEINCIDENCIA_ERRORCREANDO.toJSONError();
		}

		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(id);
		
		if(inc == null ) {
			logger.error("Creouse a incidencia, pero occoreu un erro o recuperala da base de datos.");
			return Error.CREATEINCIDENCIA_ERRORCREANDO.toJSONError();
		}
		
		logger.debug("Creouse a incidencia correctamente: "+id);
		
		int idC = ComentarioAccessor.crear(id, autor, Comentario.ACCION_CREAR_INCIDENCIA, Comentario.MODIFICACION_PUBLICA, "");
		
		if(idC < 0) {
			return Error.INSERTARCOMENTARIO_ERROR.toJSONError();
		}
			
		ret = Mensaxe.DEFAULT.toJSONMensaxe();
		ret.put("Incidencia", inc.toJson());
		
		return ret;
	}

	/**
	 * Obten unha incidencia utilizando o identificiador.
	 * @param user - Usuario que solicita a petición.
	 * @param id - Identificación da incidencia que se desexa obter.
	 * @return
	 */
	public JSONObject<String, Object> getById(Usuario user, int id) {
		JSONObject<String,Object> ret;
						
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(id);
		
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
				  user.getCliente().getCodCliente() != 0) { //Comprobamos se a incidencia é do usuario ou se o usuario é o cliente 0.
			return Error.OBTERINCIDENCIA_SENPERMISOS.toJSONError();
		}
		
		if(!user.podeVerIncidencia()) { //Comprobamos se o usuario ten permisos para ver incidencias.
			if(!(user.podeVerIncidenciaPropia() && inc.getAutor() == user.getNome())) {
				return Error.USER_NOPERMISOS.toJSONError();
			}
		}
		
		logger.debug("Obtense a incidencia: "+id);
		
		ret = Mensaxe.GETINCIDENCIA_ID_OK.toJSONMensaxe();
		ret.put("Incidencia", inc.toJson());

		return ret;
	}

	/**
	 * Obten as incidencia que coincidan cos parámetros indicados.
	 * 
	 * @param user
	 * @param codParte
	 * @param ot
	 * @param idInstalacion
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param estado
	 * @param solPresuposto
	 * @param factura
	 * @param presuposto
	 * @param dataMenorC
	 * @param dataMaiorC
	 * @param autor
	 * @param codCliente
	 * @param ver
	 * @return
	 */
	public JSONObject<String, Object> get(Usuario user, int codParte, int ot, int idInstalacion,
			String zonaApartamento, String descripcionCurta, String observacions, List<String> estados, String solPresuposto,
			String factura, String presuposto, Calendar dataMenorC, Calendar dataMaiorC, String autor, int codCliente, int ver) {
		JSONObject<String,Object> ret;
		JSONArray<Object> jsonIncidencias = new JSONArray<>();
				
		//En caso de existir, os parámetros de datas, convértense a un Timestamp para comprobalo na base de datos.
		Timestamp dataMenor = null;
		Timestamp dataMaior = null;
		if(dataMenorC != null)
			dataMenor = new Timestamp(dataMenorC.getTimeInMillis());
		if(dataMaiorC != null)
			dataMaior = new Timestamp(dataMaiorC.getTimeInMillis());
		
		if(user.getCliente().getCodCliente() != 0) { //En caso de que o usuario non sexa o cliente 0, ponse o cod_cliente o mesmo que o usuario.
			codCliente = user.getCliente().getCodCliente();
		}
		if(!user.podeVerIncidencia()) {
			if(!user.podeVerIncidenciaPropia()) { //Se non pode ver incidencias propias devolvemos error de permisos
				return Error.USER_NOPERMISOS.toJSONError();
			}
			else { //En caso de que só poida ver as incidencias propias, poñemolo como autor na búsqueda
				autor = user.getNome();
			}
		}
				
		List<Incidencia> incidencias = IncidenciaAccessor.obter(codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions, estados, solPresuposto, presuposto, factura, dataMenor, dataMaior, autor, codCliente, ver);
				
		if(incidencias != null) {
			if(!incidencias.isEmpty()) {
				logger.debug("Obtivérons "+incidencias.size()+" incidencias.");
				ret = Mensaxe.GETINCIDENCIAS_OK.toJSONMensaxe();
				for(Incidencia inc : incidencias) {
					jsonIncidencias.add(inc.toJson());
				}
				ret.put("incidencias", jsonIncidencias);
			}
			else {
				ret = Error.OBTERINCIDENCIAS_NONEXISTEN.toJSONError();
			}
		}
		else {
			ret = Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		}
		
		return ret;
	}
	
	/**
	 * Cambia o estado de unha incidencia.
	 * 
	 * @param user
	 * @param id
	 * @param estadoS
	 * @return
	 */
	public JSONObject<String, Object> cambiarEstado(Usuario user, int id, String estadoS) {
		JSONObject<String,Object> ret;
		
		if(!user.podeCambiarEstadoIncidencia()) {
			return Error.MODIFESTADOINCIDENCIA_SENPERMISOS1.toJSONError();
		}
		
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(id);
		
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
				  user.getCliente().getCodCliente() != 0) { //Comprobamos se a incidencia é do usuario ou se o usuario é o cliente 0.
			return Error.MODIFESTADOINCIDENCIA_SENPERMISOS2.toJSONError();
		}
		
		if(!user.podeVerIncidencia()) { //Comprobamos se o usuario ten permisos para ver incidencias.
			if(!(user.podeVerIncidenciaPropia() && inc.getAutor() == user.getNome())) {
				return Error.MODIFESTADOINCIDENCIA_SENPERMISOS2.toJSONError();
			}
		}

		Estado estado = Estado.getByString(estadoS);

		if(!inc.getEstado().estadoSegPosible(estado)) {
			return Error.MODIFESTADOINCIDENCIA_ESTADOFAIL.toJSONError();
		}
		
		if(estado.equals(Estado.FACTURADO) || estado.equals(Estado.PENDENTE_A) || estado.equals(Estado.PENDENTE_P) || estado.equals(Estado.PENDENTE_R)) {
			return Error.MODIFESTADOINCIDENCIA_ESTADOFAIL.toJSONError();
		}
		
		boolean modif = IncidenciaAccessor.modificarEstado(id, estado.getEstado());
		
		if(!modif) {
			return Error.MODIFESTADOINCIDENCIA_ERRORDB.toJSONError();
		}
				
		int idC = ComentarioAccessor.crear(id, user.getNome(), Comentario.ACCION_CAMBIOESTADO, Comentario.MODIFICACION_PUBLICA, estado.getEstado());

		if(idC < 0) {
			return Error.INSERTARCOMENTARIO_ERROR.toJSONError();
		}
		
		logger.debug("Cambiouse o estado da incidencia corectamente: "+inc.getId());
		inc.setEstado(estado);
		ret = Mensaxe.MODIFESTADOINCIDENCIA_OK.toJSONMensaxe();
		ret.put("Incidencia", inc.toJson());
		
		return ret;
	}
	
	/**
	 * Borra a incidencia que corresponda co Id que se lle proporciona.
	 * 
	 * @param user
	 * @param id
	 * @return
	 */
	public JSONObject<String, Object> borrar (Usuario user, int id) {
		JSONObject<String,Object> ret;
		
		if(!user.podeBorrarIncidencia()) {
			return Error.BORRARINCIDENCIA_SENPERMISOS2.toJSONError();
		}
				
		Incidencia inc = IncidenciaAccessor.obterIncidenciaPorId(id);
		
		if(inc == null) {
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();
		}
		
		if(inc.getInstalacion().getCliente().getCodCliente() != user.getCliente().getCodCliente() &&
				  user.getCliente().getCodCliente() != 0) { //Comprobamos se a incidencia é do usuario ou se o usuario é o cliente 0.
			return Error.BORRARINCIDENCIA_SENPERMISOS1.toJSONError();
		}
		
		if(!user.podeVerIncidencia()) { //Comprobamos se o usuario ten permisos para ver incidencias.
			if(!(user.podeVerIncidenciaPropia() && inc.getAutor() == user.getNome())) {
				return Error.BORRARINCIDENCIA_SENPERMISOS1.toJSONError();
			}
		}

		boolean borrado = IncidenciaAccessor.delete(id);
		
		if(!borrado) {
			return Error.BORRARINCIDENCIA_ERRORDB.toJSONError();
		}
	
		logger.debug("Borrouse a incidencia: "+inc.getId());
		
		ret = Mensaxe.BORRARINCIDENCIA_OK.toJSONMensaxe();
		
		return ret;
	}
}
