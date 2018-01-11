package es.brudi.incidencias.usuarios;

import org.apache.log4j.Logger;

import es.brudi.incidencias.clientes.Cliente;
import es.brudi.incidencias.comentarios.Comentario;
import es.brudi.incidencias.grupos.Grupo;
import es.brudi.incidencias.permisos.Permiso;
import es.brudi.incidencias.util.JSONObject;

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
	
	public JSONObject<String, Object> toJSON() {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
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
			logger.error("Exception ", e);
		}
		
		return Math.max(permisoU, permisoG);
	}
	

	/**
	 * Retorna un Obxecto JSON, con varios boolean cos diferentes permisos posibles.
	 * @return
	 */
	public Object getPermisosFinalesJSON() {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		ret.put("CrearIncidencia", podeCrearIncidencia());
		ret.put("MarcarSolPresuposto", podeMarcarSolPresuposto());
		ret.put("VerIncidencia", podeVerIncidencia());
		ret.put("VerIncidenciaPropia", podeVerIncidenciaPropia());
		ret.put("BorrarIncidencia", podeBorrarIncidencia());
		ret.put("CambiarEstadoIncidencia", podeCambiarEstadoIncidencia());
		ret.put("EngadirFactura", podeEngadirFactura());
		ret.put("EditarFactura", podeEditarFactura());
		ret.put("EngadirFactura", podeEngadirFactura());
		ret.put("VerFactura", podeVerFactura());
		ret.put("EngadirPresuposto", podeEngadirPresuposto());
		ret.put("EditarPresuposto", podeEditarPresuposto());
		ret.put("VerPresuposto", podeVerPresuposto());
		ret.put("AceptarPresuposto", podeAceptarPresuposto());
		ret.put("EngadirImaxe", podeEngadirImaxe());
		ret.put("EditarImaxe", podeEditarImaxe());
		ret.put("VerImaxe", podeVerImaxe());
		ret.put("EngadirComentario", podeEngadirComentario());
		ret.put("VerComentario", podeVerComentario());

		return ret;
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
	
	public boolean podeBorrarIncidencia() {
		int p = getPermisoByType(Permiso.POS_INCIDENCIA);
		return (p >= Permiso.BORRAR_INCIDENCIA);
	}
	
	public boolean podeCambiarEstadoIncidencia() {
		int p = getPermisoByType(Permiso.POS_INCIDENCIA);
		return (p >= Permiso.MODIFICAR_ESTADO_INCIDENCIA);
	}
	
	public boolean podeEngadirFactura() {
		int p = getPermisoByType(Permiso.POS_FACTURA);
		return (p >= Permiso.SUBIR);
	}
	
	public boolean podeEditarFactura() {
		int p = getPermisoByType(Permiso.POS_FACTURA);
		return (p >= Permiso.MODIFICAR);
	}
	
	public boolean podeVerFactura() {
		int p = getPermisoByType(Permiso.POS_FACTURA);
		return (p >= Permiso.VER);
	}
	
	public boolean podeEngadirPresuposto() {
		int p = getPermisoByType(Permiso.POS_PRESUPOSTO);
		return (p >= Permiso.SUBIR);
	}
	
	public boolean podeEditarPresuposto() {
		int p = getPermisoByType(Permiso.POS_PRESUPOSTO);
		return (p >= Permiso.MODIFICAR);
	}
	
	public boolean podeVerPresuposto() {
		int p = getPermisoByType(Permiso.POS_PRESUPOSTO);
		return (p >= Permiso.VER);
	}
	
	public boolean podeAceptarPresuposto() {
		int p = getPermisoByType(Permiso.POS_PRESUPOSTO_ACEPTADO);
		return (p >= Permiso.MARCAR_PRESUPOSTO_ACEPTADO);
	}
	
	public boolean podeEngadirImaxe() {
		int p = getPermisoByType(Permiso.POS_IMAXES);
		return (p >= Permiso.SUBIR);
	}
	
	public boolean podeEditarImaxe() {
		int p = getPermisoByType(Permiso.POS_IMAXES);
		return (p >= Permiso.MODIFICAR);
	}
	
	public boolean podeVerImaxe() {
		int p = getPermisoByType(Permiso.POS_IMAXES);
		return (p >= Permiso.VER);
	}
	
	public boolean podeEngadirAlbaran() {
		int p = getPermisoByType(Permiso.POS_ALBARANS);
		return (p >= Permiso.SUBIR);
	}
	
	public boolean podeEditarAlbaran() {
		int p = getPermisoByType(Permiso.POS_ALBARANS);
		return (p >= Permiso.MODIFICAR);
	}
	
	public boolean podeVerAlbaran() {
		int p = getPermisoByType(Permiso.POS_ALBARANS);
		return (p >= Permiso.VER);
	}
	
	public boolean podeEngadirComentario() {
		int p = getPermisoByType(Permiso.POS_COMENTARIOS);
		return (p >= Permiso.COMENTAR);
	}
	
	public boolean podeVerComentario() {
		int p = getPermisoByType(Permiso.POS_COMENTARIOS);
		return (p >= Permiso.VER);
	}

	/**
	 * Comproba se o usuario pode ver un tipo de comentario en concreto
	 * Sería conveniente no futuro mellorar este sistema.
	 * @param tipo
	 * @return
	 */
	public boolean podeVerComentarioTipo(int tipo) {
		//Se o usuario e un administrador de brudi pode ver este tipo de comentarios
		if((( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_ADMINISTRACION_BRUDI )
				|| tipo <= Comentario.MODIFICACION_ADMINISTRACION_BRUDI) && this.cliente.getCod_cliente() == 0 && this.grupo.getNome().equals("Administrador"))
			return true;
		
		//Se o usuario e un cliente de facturación de calquera cliente pode ver este tipo de comentarios.
		if((( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_ADMINISTRACION )
				|| tipo <= Comentario.MODIFICACION_ADMINISTRACION)	&& this.grupo.getNome().equals("Cliente_facturación"))
			return true;
		
		//Se o usuario e un técnico de Brudi pode ver este tipo de comentarios.
		if((( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_TECNICOS )
				|| tipo <= Comentario.MODIFICACION_TECNICOS) && this.cliente.getCod_cliente() == 0 && this.grupo.getNome().equals("Técnico"))
			return true;
		
		//Calquera usuario pode ver os comentarios públicos.
		if(( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_PUBLICO ) || tipo <= Comentario.MODIFICACION_PUBLICA)
			return true;
		
		
		return false;
	}
	
}
