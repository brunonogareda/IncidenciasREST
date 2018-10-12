package es.brudi.incidencias.instalacions;

import java.sql.ResultSet;
import java.sql.SQLException;

import es.brudi.incidencias.util.JSONObject;
import es.brudi.incidencias.clientes.Cliente;
import es.brudi.incidencias.clientes.db.ClienteAccessor;

/**
 * 
 * Clase do obxecto instalacion
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Instalacion {

	private int id;
	private String nome;
	private Cliente cliente;
	private String direccion;
	private String datos;
	

	public Instalacion() {
	}
	
	public Instalacion(ResultSet res) throws SQLException {
		id = res.getInt("Id");
		nome = res.getString("Nome");
		cliente = ClienteAccessor.getClienteById(res.getInt("Cod_cliente"));
		direccion = res.getString("Direccion");
		datos = res.getString("Datos");
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
	 * @return the cliente
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * @param cliente the cliente to set
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * @return the direccion
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * @param direccion the direccion to set
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * @return the datos
	 */
	public String getDatos() {
		return datos;
	}

	/**
	 * @param datos the datos to set
	 */
	public void setDatos(String datos) {
		this.datos = datos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Instalacion [id=" + id + ", nome=" + nome + ", cliente=" + cliente + ", direccion=" + direccion
				+ ", datos=" + datos + "]";
	}
	
	
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> json = new JSONObject<>();
		json.put("id", id);
		json.put("nome", nome);
		json.put("idcliente", cliente.getNome());
		json.put("direccion", direccion);
		return json;
	}
	
	
}
