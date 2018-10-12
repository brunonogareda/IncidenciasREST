package es.brudi.incidencias.grupos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	private List<Integer> instalacionsXestionadas = new ArrayList<>();
	
	public Grupo() {}

	public Grupo(int id, String nome, String permisos, List<Integer> instalacionsXestionadas) {
		super();
		this.id = id;
		this.nome = nome;
		this.permisos = permisos;
		this.instalacionsXestionadas = instalacionsXestionadas;
	}
	
	public Grupo(ResultSet res) throws SQLException {
		super();
		while(res.next()) {
			instalacionsXestionadas.add(res.getInt("Instalacion"));
			if(res.isLast()) {
				this.id = res.getInt("Id");
				this.nome = res.getString("Nome");
				this.permisos = res.getString("Permisos");
			}
		}
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

	/**
	 * @return the instalacionsXestionadas
	 */
	public List<Integer> getInstalacionsXestionadas() {
		return instalacionsXestionadas;
	}

	/**
	 * @param instalacionsXestionadas the instalacionsXestionadas to set
	 */
	public void setInstalacionsXestionadas(List<Integer> instalacionsXestionadas) {
		this.instalacionsXestionadas = instalacionsXestionadas;
	}

	@Override
	public String toString() {
		return "Grupo [id=" + id + ", nome=" + nome + ", permisos=" + permisos + ", instalacionsXestionadas="
				+ instalacionsXestionadas + "]";
	}

	
}
