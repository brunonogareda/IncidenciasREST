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
	private int idIncidencia;
	private String nome;
	private String proveedor;
	private String numAlbaran;
	private String comentarios;
	private String rutaFicheiro;
	private String tipoFicheiro;
	private int instalacion; //Instalación a que pertence a Incidencia asociada o albarán.
	
	public Albaran() {
	}
	
	public Albaran(int id, int idIncidencia, String nome, String proveedor, String numAlbaran, String comentarios,
			String rutaFicheiro, String tipoFicheiro) {
		super();
		this.id = id;
		this.idIncidencia = idIncidencia;
		this.nome = nome;
		this.proveedor = proveedor;
		this.numAlbaran = numAlbaran;
		this.comentarios = comentarios;
		this.rutaFicheiro = rutaFicheiro;
		this.tipoFicheiro = tipoFicheiro;
	}
	
	public Albaran(int id, int idIncidencia, String nome, String proveedor, String numAlbaran, String comentarios,
			String rutaFicheiro, String tipoFicheiro, int instalacion) {
		new Albaran(id, idIncidencia, nome, proveedor, numAlbaran, comentarios, rutaFicheiro, tipoFicheiro);
		this.instalacion = instalacion;
	}

	public Albaran(ResultSet res) throws SQLException {
		id = res.getInt("Id");
		idIncidencia = res.getInt("Id_incidencia");
		nome = res.getString("Nome");
		proveedor = res.getString("Proveedor");
		numAlbaran = res.getString("Num_albaran");
		comentarios = res.getString("Comentarios");
		rutaFicheiro = res.getString("Ruta_ficheiro");
		tipoFicheiro = res.getString("Tipo_ficheiro");
	}
	
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> json = new JSONObject<>();
		json.put("id", id);
		json.put("idIncidencia", idIncidencia);
		json.put("nome", nome);
		json.put("proveedor", proveedor);
		json.put("numAlbaran", numAlbaran);
		json.put("comentarios", comentarios);
		json.put("tipoFicheiro", tipoFicheiro);
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
	 * @return the idIncidencia
	 */
	public int getIdIncidencia() {
		return idIncidencia;
	}

	/**
	 * @param idIncidencia the idIncidencia to set
	 */
	public void setIdIncidencia(int idIncidencia) {
		this.idIncidencia = idIncidencia;
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
	 * @return the numAlbaran
	 */
	public String getNumAlbaran() {
		return numAlbaran;
	}

	/**
	 * @param numAlbaran the numAlbaran to set
	 */
	public void setNumAlbaran(String numAlbaran) {
		this.numAlbaran = numAlbaran;
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
	 * @return the instalacion
	 */
	public int getInstalacion() {
		return instalacion;
	}

	/**
	 * @param instalacion the instalacion to set
	 */
	public void setInstalacion(int instalacion) {
		this.instalacion = instalacion;
	}
}
