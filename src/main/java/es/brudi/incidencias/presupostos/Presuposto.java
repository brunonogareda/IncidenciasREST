package es.brudi.incidencias.presupostos;

import java.net.URL;

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
	private URL url;
	private String comentarios;
	
	public Presuposto() {}
	
	public Presuposto(String id, boolean aceptado, URL url, String comentarios) {
		super();
		this.id = id;
		this.aceptado = aceptado;
		this.url = url;
		this.comentarios = comentarios;
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
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
	/**
	 * @return the comentarios
	 */
	public String getComentarios() {
		return comentarios;
	}
	/**
	 * @param comentarios the comentarios to set
	 */
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Presuposto [id=" + id + ", aceptado=" + aceptado + ", url=" + url + ", comentarios=" + comentarios
				+ "]";
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", id);
		ret.put("aceptado", aceptado);
		ret.put("url", url);
		ret.put("comentarios", comentarios);
		return ret;
		
	}
	
}
