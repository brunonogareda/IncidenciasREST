package es.brudi.incidencias.presupostos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * 
 * Clase do obxecto Presuposto
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Presuposto {

	private String id;
	private boolean aceptado;
	private String rutaFicheiro;
	private String tipoFicheiro;
	private String comentarios;
	private List<Integer> idIncidencias = new ArrayList<>();
	
	public static final String JSON_TITLE 	= "Presuposto";
	public static final String JSON_TITLE2	= "Presupostos";
	
	public Presuposto() {}
	
	public Presuposto(String id, boolean aceptado, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		super();
		this.id = id;
		this.aceptado = aceptado;
		this.rutaFicheiro = rutaFicheiro;
		this.tipoFicheiro = tipoFicheiro;
		this.comentarios = comentarios;
	}
	
	public Presuposto(ResultSet res) throws SQLException {
		this.id = res.getString("Id");
		this.rutaFicheiro = res.getString("Ruta_ficheiro");
		this.tipoFicheiro = res.getString("Tipo_ficheiro");
		this.comentarios = res.getString("Comentarios");
		this.aceptado = res.getBoolean("Aceptado");
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the aceptado
	 */
	public boolean isAceptado() {
		return aceptado;
	}
	/**
	 * @param aceptado the aceptado to set
	 */
	public void setAceptado(boolean aceptado) {
		this.aceptado = aceptado;
	}
	/**
	 * @return the comentarios
	 */
	public String getComentarios() {
		return comentarios;
	}
	/**
	 * @return the rutaFicheiro
	 */
	public String getRutaFicheiro() {
		return rutaFicheiro;
	}

	/**
	 * @param rutaFicheiro the rutaFicheiro to set
	 */
	public void setRutaFicheiro(String rutaFicheiro) {
		this.rutaFicheiro = rutaFicheiro;
	}

	/**
	 * @return the tipoFicheiro
	 */
	public String getTipoFicheiro() {
		return tipoFicheiro;
	}

	/**
	 * @param tipoFicheiro the tipoFicheiro to set
	 */
	public void setTipoFicheiro(String tipoFicheiro) {
		this.tipoFicheiro = tipoFicheiro;
	}

	/**
	 * @param comentarios the comentarios to set
	 */
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	/**
	 * @return the idIncidencias
	 */
	public List<Integer> getIdIncidencias() {
		return idIncidencias;
	}

	/**
	 * @param idIncidencias the idIncidencias to set
	 */
	public void setIdIncidencias(List<Integer> idIncidencias) {
		this.idIncidencias = idIncidencias;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", id);
		ret.put("aceptado", aceptado);
		ret.put("tipoFicheiro", tipoFicheiro);
		ret.put("comentarios", comentarios);
		return ret;
		
	}
	
}
