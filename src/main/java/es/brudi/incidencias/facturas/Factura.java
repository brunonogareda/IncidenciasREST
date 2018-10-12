package es.brudi.incidencias.facturas;

import java.sql.ResultSet;
import java.sql.SQLException;

import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Clase do obxecto factura
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Factura {

	private String id;
	private String rutaFicheiro;
	private String tipoFicheiro;
	private String comentarios;
	
	public Factura() {
	}
	
	public Factura(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		this.id = id;
		this.rutaFicheiro = rutaFicheiro;
		this.tipoFicheiro = tipoFicheiro;
		this.comentarios = comentarios;
	}
	
	public Factura(ResultSet res) throws SQLException {
		this.id = res.getString("Id");
		this.rutaFicheiro = res.getString("Ruta_ficheiro");
		this.tipoFicheiro = res.getString("Tipo_ficheiro");
		this.comentarios = res.getString("Comentarios");
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
	
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> ret = new JSONObject<>();
		ret.put("id", id);
		ret.put("tipoFicheiro", tipoFicheiro);
		ret.put("comentarios", comentarios);
		return ret;
	}	
}
