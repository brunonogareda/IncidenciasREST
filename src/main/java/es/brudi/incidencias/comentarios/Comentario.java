package es.brudi.incidencias.comentarios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Clase do obxecto comentario
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Comentario {
	
	public static final int CREACION_INCIDENCIA = 1;
	public static final int MODIFICACION_PUBLICA = 2;
	public static final int MODIFICACION_TECNICOS = 3;
	public static final int MODIFICACION_ADMINISTRACION = 4;
	public static final int MODIFICACION_ADMINISTRACION_BRUDI = 5;
	public static final int TIPO_COMENTARIOS_MIN = 10;
	public static final int COMENTARIO_PUBLICO = 10;
	public static final int COMENTARIO_TECNICOS = 11;
	public static final int COMENTARIO_ADMINISTRACION = 12;
	public static final int COMENTARIO_ADMINISTRACION_BRUDI = 13;
	public static final int TIPO_COMENTARIO_MAX = 13;
	
	public static final String ACCION_CREAR_INCIDENCIA = "creou a incidencia.";
	public static final String ACCION_CAMBIOESTADO = "cambiou o estado da incidencia a";
	public static final String ACCION_BORRAR_INCIDENCIA = "eliminiou a incidencia.";
	public static final String ACCION_COMENTAR = "comentou a incidencia";
	public static final String ACCION_INSERTAR_FACTURA = "engadiu a factura";
	public static final String ACCION_MODIFICAR_FACTURA = "modificou a factura";
	public static final String ACCION_INSERTAR_PRESUPOSTO = "engadiu o presuposto";
	public static final String ACCION_MODIFICAR_PRESUPOSTO = "modificou o presuposto";
	public static final String ACCION_INSERTAR_IMAXE = "engadiu a imaxe";
	public static final String ACCION_MODIFICAR_IMAXE = "modificou a imaxe";
	public static final String ACCION_INSERTAR_ALBARAN = "engadiu o albarán";
	public static final String ACCION_MODIFICAR_ALBARAN = "modificou o albarán";
	
	private int id;
	private int idIncidencia;
	private String autor;
	private String accion;
	private int tipo;
	private String texto;
	private Calendar data = Calendar.getInstance();
	
	public Comentario() {
	}

	public Comentario(int id, int idIncidencia, String autor, String accion, int tipo, String texto, Calendar data) {
		super();
		this.id = id;
		this.idIncidencia = idIncidencia;
		this.autor = autor;
		this.accion = accion;
		this.tipo = tipo;
		this.texto = texto;
		this.data = data;
	}
	
	public Comentario(int id, int idIncidencia, String autor, String accion, int tipo, String texto, Date data) {
		super();
		this.id = id;
		this.idIncidencia = idIncidencia;
		this.autor = autor;
		this.accion = accion;
		this.tipo = tipo;
		this.texto = texto;
		this.data.setTime(data);
	}

	
	public Comentario(ResultSet res) throws SQLException {
		this.id = res.getInt("Id");
		this.idIncidencia = res.getInt("Id_incidencia");
		this.autor = res.getString("Autor");
		this.accion = res.getString("Accion");
		this.tipo = res.getInt("Tipo");
		this.texto = res.getString("Texto");
		this.data.setTime(res.getTimestamp("Data"));
	}
	
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> json = new JSONObject<>();
		json.put("id_incidencia", idIncidencia);
		json.put("autor", autor);
		json.put("titulo", obterTitulo());
		json.put("texto", obterTexto());
		json.put("data", data.getTimeInMillis());
		return json;
	}
	
	/**
	 * Devolve un titulo para poñer na cabeceira da incidencia.
	 * En caso de ser un comentario de modificación, engadese o texto no título.
	 * @return
	 */
	public String obterTitulo() {
		String titulo = "";
		titulo += accion;
		if(tipo < Comentario.TIPO_COMENTARIOS_MIN)
			titulo += " "+texto;
		return titulo;
	}
	
	/**
	 * En caso de que no sea un comentario de usuario, si no que un comentario generado por algún cambio en la incidencia,
	 * se devuelve el texto nulo.
	 * @return
	 */
	public String obterTexto() {
		if(tipo < Comentario.TIPO_COMENTARIOS_MIN)
			return null;
		else
			return texto;
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
	public void setIdIncidencia(int idIncidencia) {
		this.idIncidencia = idIncidencia;
	}

	/**
	 * @return the autor
	 */
	public String getAutor() {
		return autor;
	}

	/**
	 * @param autor the autor to set
	 */
	public void setAutor(String autor) {
		this.autor = autor;
	}

	/**
	 * @return the accion
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * @param accion the accion to set
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * @return the tipo
	 */
	public int getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the texto
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * @param texto the texto to set
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

	/**
	 * @return the data
	 */
	public Calendar getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Calendar data) {
		this.data = data;
	}

	
	
}
