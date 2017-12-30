package es.brudi.incidencias.usuarios;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import es.brudi.incidencias.clientes.Cliente;
import es.brudi.incidencias.grupos.Grupo;
import es.brudi.incidencias.permisos.Permiso;

/**
 * 
 * Clase do obxecto usuario
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Usuario {
	
	private int id;
	private String nome;
	private Cliente cliente;
	private Grupo grupo;
	private String email;
	private String nome_completo;
    private String permisos;
    
	private static Logger logger = Logger.getLogger(Usuario.class);
    
	public Usuario() {
	}
	
	public Usuario(int id, String nome, Cliente cliente, Grupo grupo, String email, String nome_completo, String permisos) {
		super();
		this.id = id;
		this.nome = nome;
		this.cliente = cliente;
		this.grupo = grupo;
		this.email = email;
		this.nome_completo = nome_completo;
		this.permisos = permisos;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + nome + ", cliente=" + cliente.toString() + ", grupo=" + grupo.toString() + ", email=" + email
				+ ", nome_completo=" + nome_completo + ", permisos=" + permisos + "]";
	}
	/**
	 * @return Devolve un obxecto json con datos básicos do usuario
	 */
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		
		ret.put("id", id);
        ret.put("nome", nome);
        ret.put("nome_completo", nome_completo);
        
        return ret;
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
	 * @return the grupo
	 */
	public Grupo getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo the grupo to set
	 */
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the nome_completo
	 */
	public String getNome_completo() {
		return nome_completo;
	}

	/**
	 * @param nome_completo the nome_completo to set
	 */
	public void setNome_completo(String nome_completo) {
		this.nome_completo = nome_completo;
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
	 * Retora el valor máximo de los permisos de un tipo en concreto del usuario y el grupo.
	 * @param tipo - Posición del permiso en el string de permisos.
	 * @return
	 */
	public int getPermisoByType(int tipo) {
		int permisoU = 0;
		int permisoG = 0;
		
		try {
			permisoU = Character.getNumericValue(permisos.charAt(tipo));
			permisoG = Character.getNumericValue(this.grupo.getPermisos().charAt(tipo));
		}
		catch(Exception e) {
			logger.error("Error obtendo os permisos do usuario.");
			logger.error("Exception "+ e);
		}
		
		return Math.max(permisoU, permisoG);
	}
	
	/**
	 * @return String de permisos finais do usuario, máximo entre os permisos do usuario e do grupo.
	 */
	public String getPermisosFinales() {
		String permisosg = this.grupo.getPermisos();
		int numpermisos = Math.min(permisos.length(), permisosg.length());
		String permisosf = "";
		for(int i=0; i<numpermisos; i++) {
			permisosf += getPermisoByType(i);
		}
		return permisosf;
	}
	
	//***************************************************************
	// Métodos para comprobar os permisos de un usuario.
	//***************************************************************
	
	public boolean podeCrearIncidencia() {
		int p = getPermisoByType(Permiso.POS_INCIDENCIA);
		return (p >= Permiso.CREAR_INCIDENCIA);
	}
	
	public boolean podeMarcarSolPresuposto() {
		int p = getPermisoByType(Permiso.POS_SOL_PRESUPOSTO);
		return (p >= Permiso.MARCAR_SOL_PRESUPOSTO);
	}
	
	public boolean podeVerIncidencia() {
		int p = getPermisoByType(Permiso.POS_VER_INCIDENCIA);
		return (p >= Permiso.VER_INCIDENCIA);
	}
	
	public boolean podeVerIncidenciaPropia() {
		int p = getPermisoByType(Permiso.POS_VER_INCIDENCIA);
		return (p >= Permiso.VER_INCIDENCIA_PROPIA);
	}
	
}
