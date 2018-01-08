package es.brudi.incidencias.imaxes;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

/**
 * 
 * Clase do obxecto Imaxe
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class Imaxe {

	private int id;
	private int id_incidencia;
	private String nome;
	private boolean antes_despois; //False -> Antes, True -> Despois
	private String ruta_ficheiro;
	private String tipo_ficheiro;
	private String comentarios;
	
	public Imaxe() {}
	
	public Imaxe(int id, int id_incidencia, String nome, boolean antes_despois, String ruta_ficheiro, String tipo_ficheiro, String comentarios) {
		super();
		this.id = id;
		this.id_incidencia = id_incidencia;
		this.nome = nome;
		this.antes_despois = antes_despois;
		this.ruta_ficheiro = ruta_ficheiro;
		this.tipo_ficheiro = tipo_ficheiro;
		this.comentarios = comentarios;
	}
	
	public Imaxe(ResultSet res) throws SQLException {
		this.id = res.getInt("Id");
		this.id_incidencia = res.getInt("Id_incidencia");
		this.nome = res.getString("Nome");
		this.antes_despois = res.getBoolean("Antes_despois");
		this.ruta_ficheiro = res.getString("Ruta_ficheiro");
		this.tipo_ficheiro = res.getString("Tipo_ficheiro");
		this.comentarios = res.getString("Comentarios");
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
	 * @return the antes_despois
	 */
	public boolean isAntes_despois() {
		return antes_despois;
	}

	/**
	 * @param antes_despois the antes_despois to set
	 */
	public void setAntes_despois(boolean antes_despois) {
		this.antes_despois = antes_despois;
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
		ret.put("id_incidencia", id_incidencia);
		ret.put("nome", nome);
		ret.put("antes_despois", antes_despois);
		ret.put("tipo_ficheiro", tipo_ficheiro);
		ret.put("comentarios", comentarios);
		return ret;
		
	}
	
}
