package es.brudi.incidencias.grupos;

/**
 * 
 * Clase do obxecto grupo
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Grupo {

	private int id;
	private String nome;
	private String permisos;
	
	public Grupo() {}

	public Grupo(int id, String nome, String permisos) {
		super();
		this.id = id;
		this.nome = nome;
		this.permisos = permisos;
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
	 * @return the permisos
	 */
	public String getPermisos() {
		return permisos;
	}

	/**
	 * @param permisos the permisos to set
	 */
	public void setPermisos(String permisos) {
		this.permisos = permisos;
	}

	@Override
	public String toString() {
		return "Grupo [id=" + id + ", nome=" + nome + ", permisos=" + permisos + "]";
	}
	
	
	
}
