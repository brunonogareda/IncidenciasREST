package es.brudi.incidencias.presupostos;

import java.sql.ResultSet;
import java.sql.SQLException;

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
	private String ruta_ficheiro;
	private String tipo_ficheiro;
	private String comentarios;
	
	public Presuposto() {}
	
	public Presuposto(String id, boolean aceptado, String ruta_ficheiro, String tipo_ficheiro, String comentarios) {
		super();
		this.id = id;
		this.aceptado = aceptado;
		this.ruta_ficheiro = ruta_ficheiro;
		this.tipo_ficheiro = tipo_ficheiro;
		this.comentarios = comentarios;
	}
	
	public Presuposto(ResultSet res) throws SQLException {
		this.id = res.getString("Id");
		this.ruta_ficheiro = res.getString("Ruta_ficheiro");
		this.tipo_ficheiro = res.getString("Tipo_ficheiro");
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
	 * @return the ruta_ficheiro
	 */
	public String getRuta_ficheiro() {
		return ruta_ficheiro;
	}

	/**
	 * @param ruta_ficheiro the ruta_ficheiro to set
	 */
	public void setRuta_ficheiro(String ruta_ficheiro) {
		this.ruta_ficheiro = ruta_ficheiro;
	}

	/**
	 * @return the tipo_ficheiro
	 */
	public String getTipo_ficheiro() {
		return tipo_ficheiro;
	}

	/**
	 * @param tipo_ficheiro the tipo_ficheiro to set
	 */
	public void setTipo_ficheiro(String tipo_ficheiro) {
		this.tipo_ficheiro = tipo_ficheiro;
	}

	/**
	 * @param comentarios the comentarios to set
	 */
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", id);
		ret.put("aceptado", aceptado);
		ret.put("tipo_ficheiro", tipo_ficheiro);
		ret.put("comentarios", comentarios);
		return ret;
		
	}
	
}
