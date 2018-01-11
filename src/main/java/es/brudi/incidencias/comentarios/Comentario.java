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
	
	public final static int CREACION_INCIDENCIA = 1;
	public final static int MODIFICACION_PUBLICA = 2;
	public final static int MODIFICACION_TECNICOS = 3;
	public final static int MODIFICACION_ADMINISTRACION = 4;
	public final static int MODIFICACION_ADMINISTRACION_BRUDI = 5;
	public final static int TIPO_COMENTARIOS_MIN = 10;
	public final static int COMENTARIO_PUBLICO = 10;
	public final static int COMENTARIO_TECNICOS = 11;
	public final static int COMENTARIO_ADMINISTRACION = 12;
	public final static int COMENTARIO_ADMINISTRACION_BRUDI = 13;
	public final static int TIPO_COMENTARIO_MAX = 13;
	
	public final static String ACCION_CREAR_INCIDENCIA = "creou a incidencia.";
	public final static String ACCION_CAMBIOESTADO = "cambiou o estado da incidencia a";
	public final static String ACCION_BORRAR_INCIDENCIA = "eliminiou a incidencia.";
	public final static String ACCION_COMENTAR = "comentou a incidencia";
	public final static String ACCION_INSERTAR_FACTURA = "engadiu a factura";
	public final static String ACCION_MODIFICAR_FACTURA = "modificou a factura";
	public final static String ACCION_INSERTAR_PRESUPOSTO = "engadiu o presuposto";
	public final static String ACCION_MODIFICAR_PRESUPOSTO = "modificou o presuposto";
	public final static String ACCION_INSERTAR_IMAXE = "engadiu a imaxe";
	public final static String ACCION_MODIFICAR_IMAXE = "modificou a imaxe";
	public final static String ACCION_INSERTAR_ALBARAN = "engadiu o albarán";
	public final static String ACCION_MODIFICAR_ALBARAN = "modificou o albarán";
	
	private int id;
	private int id_incidencia;
	private String autor;
	private String accion;
	private int tipo;
	private String texto;
	private Calendar data = Calendar.getInstance();
	
	public Comentario() {
	}

	public Comentario(int id, int id_incidencia, String autor, String accion, int tipo, String texto, Calendar data) {
		super();
		this.id = id;
		this.id_incidencia = id_incidencia;
		this.autor = autor;
		this.accion = accion;
		this.tipo = tipo;
		this.texto = texto;
		this.data = data;
	}
	
	public Comentario(int id, int id_incidencia, String autor, String accion, int tipo, String texto, Date data) {
		super();
		this.id = id;
		this.id_incidencia = id_incidencia;
		this.autor = autor;
		this.accion = accion;
		this.tipo = tipo;
		this.texto = texto;
		this.data.setTime(data);
	}

	
	public Comentario(ResultSet res) throws SQLException {
		this.id = res.getInt("Id");
		this.id_incidencia = res.getInt("Id_incidencia");
		this.autor = res.getString("Autor");
		this.accion = res.getString("Accion");
		this.tipo = res.getInt("Tipo");
		this.texto = res.getString("Texto");
		this.data.setTime(res.getTimestamp("Data"));
	}
	
	public JSONObject<String, Object> toJson() {
		JSONObject<String, Object> json = new JSONObject<String, Object>();
		json.put("id_incidencia", id_incidencia);
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
	 * @return the id_incidencia
	 */
	public int getId_incidencia() {
		return id_incidencia;
	}

	/**
	 * @param id_incidencia the id_incidencia to set
	 */
	public void setId_incidencia(int id_incidencia) {
		this.id_incidencia = id_incidencia;
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
