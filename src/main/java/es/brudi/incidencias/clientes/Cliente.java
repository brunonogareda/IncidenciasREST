package es.brudi.incidencias.clientes;

import java.sql.ResultSet;
import java.sql.SQLException;

import es.brudi.incidencias.util.JSONObject;

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
	
	private int codCliente;
	private String nome;
	private String nomeCurto;
	private String nif;
	private boolean codPartePropio;
	
	
	public Cliente() {}


	/**
	 * 
	 * Constructor principal da clase Cliente
	 * @param codCliente
	 * @param nome
	 * @param nomeCurto
	 * @param nif
	 * @param codPartePropio
	 */
	public Cliente(int codCliente, String nome, String nomeCurto, String nif, boolean codPartePropio) {
		super();
		this.codCliente = codCliente;
		this.nome = nome;
		this.nomeCurto = nomeCurto;
		this.nif = nif;
		this.codPartePropio = codPartePropio;
	}
	
	/**
	 * Constructor da clase Cliente que reciben un obxecto byte, en caso de 1 pon a true en caso de 0 pon a false.
	 * @param codCliente
	 * @param nome
	 * @param nomeCurto
	 * @param nif
	 * @param codPartePropio
	 */
	public Cliente(int codCliente, String nome, String nomeCurto, String nif, byte codPartePropio) {		
		this(codCliente, nome, nomeCurto, nif, true);
		if(codPartePropio==0) {
			this.codPartePropio = false;
		}
	}

	public Cliente(ResultSet res)  throws SQLException {
		this.codCliente = res.getInt("Cod_cliente");
		this.nome = res.getString("Nome");
		this.nomeCurto = res.getString("Nome_curto");
		this.nif = res.getString("NIF");
		this.codPartePropio = res.getBoolean("Cod_parte_propio");
	}
	
	@Override
	public String toString() {
		return "Cliente [cod_cliente=" + codCliente + ", nome=" + nome + ", nome_curto=" + nomeCurto + ", nif=" + nif
				+ ", cod_parte_propio=" + codPartePropio + "]";
	}

	/**
	 * @return Devolve un obxecto json cos parámetros do cliente.Devolve un obxecto json cos parámetros do cliente.
	 */
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> ret = new JSONObject<>();
		ret.put("cod_cliente", codCliente);
		ret.put("nome", nome);
		ret.put("nome_curto", nomeCurto);
		ret.put("nif", nif);
		ret.put("cod_parte_propio", codPartePropio);
		return ret;
	}


	/**
	 * @return the codCliente
	 */
	public int getCodCliente() {
		return codCliente;
	}


	/**
	 * @param codCliente the codCliente to set
	 */
	public void setCodCliente(int codCliente) {
		this.codCliente = codCliente;
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
	 * @return the nomeCurto
	 */
	public String getNomeCurto() {
		return nomeCurto;
	}


	/**
	 * @param nomeCurto the nomeCurto to set
	 */
	public void setNomeCurto(String nomeCurto) {
		this.nomeCurto = nomeCurto;
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
	 * @return the codPartePropio
	 */
	public boolean isCodPartePropio() {
		return codPartePropio;
	}


	/**
	 * @param codPartePropio the codPartePropio to set
	 */
	public void setCodPartePropio(boolean codPartePropio) {
		this.codPartePropio = codPartePropio;
	}
	
	
}
