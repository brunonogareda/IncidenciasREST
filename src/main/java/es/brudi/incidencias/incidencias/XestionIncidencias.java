package es.brudi.incidencias.incidencias;

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
	 * 
	 * @param user
	 *            - Usuario que realiza a petición.
	 * @param codParte
	 * @param ot
	 * @param idInstalacion
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param solPresuposto
	 * @return
	 */
	public JSONObject<String, Object> crear(Usuario user, int codParte, int ot, int idInstalacion,
			String zonaApartamento, String descripcionCurta, String observacions, boolean solPresuposto) {
		JSONObject<String, Object> ret;

		// Comprobamos permisos de creación e se pode marcar solicitar presuposto
		if (!user.podeCrearIncidencia() || (!user.podeMarcarSolPresuposto() && solPresuposto))
			return Error.USER_NOPERMISOS.toJSONError();

		Estado estado = Estado.PENDENTE_R;
		int autor = user.getId();

		Instalacion instalacion = InstalacionAccessor.getInstalacionById(idInstalacion);
		if (instalacion == null)// Comprobamos se existe a instalación
			return Error.CREATEINCIDENCIA_NONEXISTEINST.toJSONError();
		if (!user.xestionaInstalacion(instalacion.getId())) // Comprobamos que a instalación pertence o usuario que crea
															// a incidencia ou é 0.
			return Error.CREATEINCIDENCIA_INSTPERMISOS.toJSONError();

		if (solPresuposto) // En caso de que se solicite presuposto, o estado inicial é diferente.
			estado = Estado.PENDENTE_P;

		Incidencia inc = IncidenciaAccessor.crear(codParte, ot, instalacion, zonaApartamento, descripcionCurta,
				observacions, estado, solPresuposto, autor);

		if (inc == null) {
			logger.error("Erro creadon a incidencia.");
			return Error.CREATEINCIDENCIA_ERRORCREANDO.toJSONError();
		}

		logger.debug("Creouse a incidencia correctamente: " + inc.getId());

		ComentarioAccessor.crear(inc.getId(), autor, Comentario.ACCION_CREAR_INCIDENCIA,
				Comentario.MODIFICACION_PUBLICA, "");

		ret = Mensaxe.DEFAULT.toJSONMensaxe();
		ret.put("Incidencia", inc.toJson());

		return ret;
	}

	/**
	 * Obten unha incidencia utilizando o identificiador.
	 * 
	 * @param user
	 *            - Usuario que solicita a petición.
	 * @param id
	 *            - Identificación da incidencia que se desexa obter.
	 * @return
	 */
	public JSONObject<String, Object> obterPorId(Usuario user, int id) {
		JSONObject<String, Object> ret;

		if (!user.podeVerIncidencia())
			return Error.USER_NOPERMISOS.toJSONError();

		Incidencia inc = IncidenciaAccessor.obterPorId(id, user);
		if (inc == null)
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();

		logger.debug("Obtense a incidencia: " + id);
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
	public JSONObject<String, Object> obter(Usuario user, int codParte, int ot, int idInstalacion,
			String zonaApartamento, String descripcionCurta, String observacions, List<String> estados,
			String solPresuposto, String factura, String presuposto, Calendar dataMenor, Calendar dataMaior, int autor,
			int ver) {
		JSONObject<String, Object> ret;
		JSONArray<Object> jsonIncidencias = new JSONArray<>();

		if (!user.podeVerIncidencia()) {
			if (!user.podeVerIncidenciaPropia()) // Se non pode ver incidencias propias devolvemos error de permisos
				return Error.USER_NOPERMISOS.toJSONError();
			else // En caso de que só poida ver as incidencias propias, poñemolo como autor na búsqueda
				autor = user.getId();
		}

		List<Incidencia> incidencias = IncidenciaAccessor.obter(codParte, ot, idInstalacion, zonaApartamento,
				descripcionCurta, observacions, estados, solPresuposto, presuposto, factura, dataMenor, dataMaior,
				autor, ver, user);

		if (incidencias == null)
			return Error.OBTERINCIDENCIAS_ERRORDB.toJSONError();
		
		if (incidencias.isEmpty())
			return Error.OBTERINCIDENCIAS_NONEXISTEN.toJSONError();
		
		logger.debug("Obtivérons " + incidencias.size() + " incidencias.");
		ret = Mensaxe.GETINCIDENCIAS_OK.toJSONMensaxe();
		for (Incidencia inc : incidencias)
			jsonIncidencias.add(inc.toJson());
		ret.put("incidencias", jsonIncidencias);

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
		JSONObject<String, Object> ret;

		if (!user.podeCambiarEstadoIncidencia())
			return Error.MODIFESTADOINCIDENCIA_SENPERMISOS.toJSONError();

		Incidencia inc = IncidenciaAccessor.obterPorId(id, user);
		if (inc == null)
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();

		Estado estado = Estado.getByString(estadoS);

		if (!inc.getEstado().estadoSegPosible(estado)) 
			return Error.MODIFESTADOINCIDENCIA_ESTADOFAIL.toJSONError();

		boolean modif = IncidenciaAccessor.modificarEstado(id, estado.getEstado());

		if (!modif)
			return Error.MODIFESTADOINCIDENCIA_ERRORDB.toJSONError();

		ComentarioAccessor.crear(id, user.getId(), Comentario.ACCION_CAMBIOESTADO, Comentario.MODIFICACION_PUBLICA,
				estado.getEstado());

		logger.debug("Cambiouse o estado da incidencia corectamente: " + inc.getId());
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
	public JSONObject<String, Object> borrar(Usuario user, int id) {
		JSONObject<String, Object> ret;

		if (!user.podeBorrarIncidencia())
			return Error.BORRARINCIDENCIA_SENPERMISOS.toJSONError();

		Incidencia inc = IncidenciaAccessor.obterPorId(id, user);
		if (inc == null)
			return Error.OBTERINCIDENCIA_NONEXISTE.toJSONError();

		boolean borrado = IncidenciaAccessor.delete(id);
		if (!borrado)
			return Error.BORRARINCIDENCIA_ERRORDB.toJSONError();

		logger.debug("Borrouse a incidencia: " + inc.getId());
		ret = Mensaxe.BORRARINCIDENCIA_OK.toJSONMensaxe();

		return ret;
	}
}
