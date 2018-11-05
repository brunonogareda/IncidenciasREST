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

	public enum Tipo {
		ANTES,
		DESPOIS;
		
		public boolean getBoolTipo() {
			return (this.equals(DESPOIS));
		}
		
		public static Tipo getTipoFromBool(boolean tipo) {
			if(tipo)
				return DESPOIS;
			else
				return ANTES;
		}
		
		public static Tipo getTipoFromString(String tipo) {
			if("despois".equalsIgnoreCase(tipo) || "despues".equalsIgnoreCase(tipo) || "después".equalsIgnoreCase(tipo))
				return DESPOIS;
			else
				return ANTES;
		}
	}
	
	private int id;
	private int idIncidencia;
	private String nome;
	private Tipo tipo; //False -> Antes, True -> Despois
	private String rutaFicheiro;
	private String tipoFicheiro;
	private String comentarios;
	private int idInstalacion; //Instalación a que pertence a Incidencia asociada a imaxe.
	
	public static final String JSON_TITLE 	= "Imaxe";
	public static final String JSON_TITLE2	= "Imaxes";
	
	public Imaxe() {}
	
	public Imaxe(int id, int idIncidencia, String nome, Tipo tipo, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		super();
		this.id = id;
		this.idIncidencia = idIncidencia;
		this.nome = nome;
		this.tipo = tipo;
		this.rutaFicheiro = rutaFicheiro;
		this.tipoFicheiro = tipoFicheiro;
		this.comentarios = comentarios;
	}
	
	public Imaxe(ResultSet res) throws SQLException {
		this.id = res.getInt("Id");
		this.idIncidencia = res.getInt("Id_incidencia");
		this.nome = res.getString("Nome");
		this.tipo = Tipo.getTipoFromBool(res.getBoolean("Antes_despois"));
		this.rutaFicheiro = res.getString("Ruta_imaxe");
		this.tipoFicheiro = res.getString("Tipo_imaxe");
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
	 * @return the idIncidencia
	 */
	public int getIdIncidencia() {
		return idIncidencia;
	}

	/**
	 * @param idIncidencia the idIncidencia to set
	 */
	public void setIdncidencia(int idIncidencia) {
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
	 * @return tipo
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
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
	 * @return the idInstalacion
	 */
	public int getIdInstalacion() {
		return idInstalacion;
	}

	/**
	 * @param idInstalacion the idInstalacion to set
	 */
	public void setIdInstalacion(int idInstalacion) {
		this.idInstalacion = idInstalacion;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", id);
		ret.put("idIncidencia", idIncidencia);
		ret.put("nome", nome);
		ret.put("tipo", tipo);
		ret.put("tipoFicheiro", tipoFicheiro);
		ret.put("comentarios", comentarios);
		return ret;
		
	}
	
}
