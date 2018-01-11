package es.brudi.incidencias.albarans;

import java.sql.ResultSet;
import java.sql.SQLException;

import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Clase do obxecto albaran
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class Albaran {

	private int id;
	private int id_incidencia;
	private String nome;
	private String proveedor;
	private String num_albaran;
	private String comentarios;
	private String ruta_ficheiro;
	private String tipo_ficheiro;
	
	
	public Albaran() {
	}
	
	public Albaran(int id, int id_incidencia, String nome, String proveedor, String num_albaran, String comentarios,
			String ruta_ficheiro, String tipo_ficheiro) {
		super();
		this.id = id;
		this.id_incidencia = id_incidencia;
		this.nome = nome;
		this.proveedor = proveedor;
		this.num_albaran = num_albaran;
		this.comentarios = comentarios;
		this.ruta_ficheiro = ruta_ficheiro;
		this.tipo_ficheiro = tipo_ficheiro;
	}

	public Albaran(ResultSet res) throws SQLException {
		id = res.getInt("Id");
		id_incidencia = res.getInt("Id_incidencia");
		nome = res.getString("Nome");
		proveedor = res.getString("Proveedor");
		num_albaran = res.getString("Num_albaran");
		comentarios = res.getString("Comentarios");
		ruta_ficheiro = res.getString("Ruta_ficheiro");
		tipo_ficheiro = res.getString("Tipo_ficheiro");
	}
	
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> json = new JSONObject<String, Object>();
		json.put("id_incidencia", id_incidencia);
		json.put("nome", nome);
		json.put("proveedor", proveedor);
		json.put("num_albaran", num_albaran);
		json.put("comentarios", comentarios);
		json.put("tipo_ficheiro", tipo_ficheiro);
		return json;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id_incidencia
	 */
	public int getId_incidencia() {
		return id_incidencia;
	}

	/**
	 * @param id_incidencia the id_incidencia to set
	 */
	public void setId_incidencia(int id_incidencia) {
		this.id_incidencia = id_incidencia;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the proveedor
	 */
	public String getProveedor() {
		return proveedor;
	}

	/**
	 * @param proveedor the proveedor to set
	 */
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	/**
	 * @return the num_albaran
	 */
	public String getNum_albaran() {
		return num_albaran;
	}

	/**
	 * @param num_albaran the num_albaran to set
	 */
	public void setNum_albaran(String num_albaran) {
		this.num_albaran = num_albaran;
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
	
	
	
	
}
