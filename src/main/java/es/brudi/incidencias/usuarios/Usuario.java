package es.brudi.incidencias.usuarios;

import java.util.ArrayList;
import java.util.List;

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
	private String nomeCompleto;
    private String permisos;
    private List<Integer> instalacionsXestionadas = new ArrayList<>();
    
	private static Logger logger = Logger.getLogger(Usuario.class);
    
	public Usuario() {
	}
	
	public Usuario(int id, String nome, Cliente cliente, Grupo grupo, String email, String nomeCompleto, String permisos, List<Integer> instalacionsXestionadas) {
		super();
		this.id = id;
		this.nome = nome;
		this.cliente = cliente;
		this.grupo = grupo;
		this.email = email;
		this.nomeCompleto = nomeCompleto;
		this.permisos = permisos;
		this.instalacionsXestionadas = instalacionsXestionadas;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + nome + ", cliente=" + cliente.toString() + ", grupo=" + grupo.toString() + ", email=" + email
				+ ", nome_completo=" + nomeCompleto + ", permisos=" + permisos + "]";
	}
	/**
	 * @return Devolve un obxecto json con datos básicos do usuario
	 */
	
	public JSONObject<String, Object> toJSON() {
		JSONObject<String, Object> ret = new JSONObject<>();
		
		ret.put("id", id);
        ret.put("nome", nome);
        ret.put("nomeCompleto", nomeCompleto);
        
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
	 * @return the nomeCompleto
	 */
	public String getNomeCompleto() {
		return nomeCompleto;
	}

	/**
	 * @param nomeCompleto the nomeCompleto to set
	 */
	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
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
		JSONObject<String, Object> ret = new JSONObject<>();
		ret.put("crearIncidencia", podeCrearIncidencia());
		ret.put("marcarSolPresuposto", podeMarcarSolPresuposto());
		ret.put("verIncidencia", podeVerIncidencia());
		ret.put("verIncidenciaPropia", podeVerIncidenciaPropia());
		ret.put("borrarIncidencia", podeBorrarIncidencia());
		ret.put("cambiarEstadoIncidencia", podeCambiarEstadoIncidencia());
		ret.put("engadirFactura", podeEngadirFactura());
		ret.put("editarFactura", podeEditarFactura());
		ret.put("verFactura", podeVerFactura());
		ret.put("engadirPresuposto", podeEngadirPresuposto());
		ret.put("editarPresuposto", podeEditarPresuposto());
		ret.put("verPresuposto", podeVerPresuposto());
		ret.put("aceptarPresuposto", podeAceptarPresuposto());
		ret.put("engadirImaxe", podeEngadirImaxe());
		ret.put("editarImaxe", podeEditarImaxe());
		ret.put("verImaxe", podeVerImaxe());
		ret.put("engadirComentario", podeEngadirComentario());
		ret.put("verComentario", podeVerComentario());

		return ret;
	}
	
	
	
	/**
	 * @return String de permisos finais do usuario, máximo entre os permisos do usuario e do grupo.
	 */
	public String getPermisosFinales() {
		String permisosg = this.grupo.getPermisos();
		int numpermisos = Math.min(permisos.length(), permisosg.length());
		StringBuilder bld = new StringBuilder();
		for(int i=0; i<numpermisos; i++) {
			bld.append(i);
		}
		return bld.toString();
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
				|| tipo <= Comentario.MODIFICACION_ADMINISTRACION_BRUDI) && this.cliente.getCodCliente() == 0 && this.grupo.getNome().equals("Administrador"))
			return true;
		
		//Se o usuario e un cliente de facturación de calquera cliente pode ver este tipo de comentarios.
		if((( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_ADMINISTRACION )
				|| tipo <= Comentario.MODIFICACION_ADMINISTRACION)	&& this.grupo.getNome().equals("Cliente_facturación"))
			return true;
		
		//Se o usuario e un técnico de Brudi pode ver este tipo de comentarios.
		if((( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_TECNICOS )
				|| tipo <= Comentario.MODIFICACION_TECNICOS) && this.cliente.getCodCliente() == 0 && this.grupo.getNome().equals("Técnico"))
			return true;
		
		//Calquera usuario pode ver os comentarios públicos.
		if(( Comentario.TIPO_COMENTARIOS_MIN <= tipo && tipo <= Comentario.COMENTARIO_PUBLICO ) || tipo <= Comentario.MODIFICACION_PUBLICA)
			return true;
		
		
		return false;
	}

	/**
	 * Comproba se o usuario xestiona a instalación directamente ou pertence a un grupo que a xestione.
	 * @param instalacion
	 * @return
	 */
	public boolean xestionaInstalacion(int instalacion) {
		return (this.instalacionsXestionadas.contains(instalacion) || this.grupo.getInstalacionsXestionadas().contains(instalacion));
	}
	
}
