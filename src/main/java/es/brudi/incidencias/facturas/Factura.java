package es.brudi.incidencias.facturas;

//import java.net.URL;
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
	private String ruta_ficheiro;
	private String tipo_ficheiro;
	private String comentarios;
	
	public Factura() {
	}
	
	public Factura(String id, String ruta_ficheiro, String tipo_ficheiro, String comentarios) {
		this.id = id;
		this.ruta_ficheiro = ruta_ficheiro;
		this.tipo_ficheiro = tipo_ficheiro;
		this.comentarios = comentarios;
	}
	
	public Factura(ResultSet res) throws SQLException {
		this.id = res.getString("Id");
		this.ruta_ficheiro = res.getString("Ruta_ficheiro");
		this.tipo_ficheiro = res.getString("Tipo_ficheiro");
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
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		ret.put("id", id);
		ret.put("tipo_ficheiro", tipo_ficheiro);
		ret.put("comentarios", comentarios);
		return ret;
	}	
}
