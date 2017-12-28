package es.brudi.incidencias.clientes;

import org.json.simple.JSONObject;

/**
 * 
 * Clase do obxecto cliente
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Cliente {
	
	public int cod_cliente;
	public String nome;
	public String nome_curto;
	public String nif;
	public boolean cod_parte_propio;
	
	
	public Cliente() {}


	/**
	 * 
	 * Constructor principal da clase Cliente
	 * @param cod_cliente
	 * @param nome
	 * @param nome_curto
	 * @param nif
	 * @param cod_parte_propio
	 */
	public Cliente(int cod_cliente, String nome, String nome_curto, String nif, boolean cod_parte_propio) {
		super();
		this.cod_cliente = cod_cliente;
		this.nome = nome;
		this.nome_curto = nome_curto;
		this.nif = nif;
		this.cod_parte_propio = cod_parte_propio;
	}
	
	/**
	 * Constructor da clase Cliente que reciben un obxecto byte, en caso de 1 pon a true en caso de 0 pon a false.
	 * @param cod_cliente
	 * @param nome
	 * @param nome_curto
	 * @param nif
	 * @param cod_parte_propio
	 */
	public Cliente(int cod_cliente, String nome, String nome_curto, String nif, byte cod_parte_propio) {		
		this(cod_cliente, nome, nome_curto, nif, true);
		if(cod_parte_propio==0) {
			this.cod_parte_propio = false;
		}
	}


	@Override
	public String toString() {
		return "Cliente [cod_cliente=" + cod_cliente + ", nome=" + nome + ", nome_curto=" + nome_curto + ", nif=" + nif
				+ ", cod_parte_propio=" + cod_parte_propio + "]";
	}

	/**
	 * @return Devolve un obxecto json cos parámetros do cliente.Devolve un obxecto json cos parámetros do cliente.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("cod_cliente", cod_cliente);
		ret.put("nome", nome);
		ret.put("nome_curto", nome_curto);
		ret.put("nif", nif);
		ret.put("cod_parte_propio", cod_parte_propio);
		return ret;
	}


	/**
	 * @return the cod_cliente
	 */
	public int getCod_cliente() {
		return cod_cliente;
	}


	/**
	 * @param cod_cliente the cod_cliente to set
	 */
	public void setCod_cliente(int cod_cliente) {
		this.cod_cliente = cod_cliente;
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
	 * @return the nome_curto
	 */
	public String getNome_curto() {
		return nome_curto;
	}


	/**
	 * @param nome_curto the nome_curto to set
	 */
	public void setNome_curto(String nome_curto) {
		this.nome_curto = nome_curto;
	}


	/**
	 * @return the nif
	 */
	public String getNif() {
		return nif;
	}


	/**
	 * @param nif the nif to set
	 */
	public void setNif(String nif) {
		this.nif = nif;
	}


	/**
	 * @return the cod_parte_propio
	 */
	public boolean isCod_parte_propio() {
		return cod_parte_propio;
	}


	/**
	 * @param cod_parte_propio the cod_parte_propio to set
	 */
	public void setCod_parte_propio(boolean cod_parte_propio) {
		this.cod_parte_propio = cod_parte_propio;
	}
	
	
}
