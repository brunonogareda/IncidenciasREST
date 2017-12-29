package es.brudi.incidencias.comentarios;

import java.util.Date;

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
	
	public final static int CREACION = 1;
	public final static int MODIFICACION_PUBLICA = 2;
	public final static int MODIFICACION_ADMINISTRACION = 3;
	public final static int COMENTARIO_PUBLICO = 4;
	public final static int COMENTARIO_TECNICOS = 5;
	public final static int COMENTARIO_ADMINISTRACION = 6;
	
	public final static String ACCION_CREAR = "creou";
	public final static String ACCION_CAMBIOESTADO = "modificou";
	public final static String ACCION_COMENTAR = "comentou";
	public final static String ACCION_BORRAR = "eliminiou";
	
	private int Id;
	private String Autor;
	private String Accion;
	private int Tipo;
	private String Texto;
	private Date Data;
	
	public Comentario() {
	}

	public Comentario(int id, String autor, String accion, int tipo, String texto, Date data) {
		super();
		Id = id;
		Autor = autor;
		Accion = accion;
		Tipo = tipo;
		Texto = texto;
		Data = data;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * @return the autor
	 */
	public String getAutor() {
		return Autor;
	}

	/**
	 * @return the accion
	 */
	public String getAccion() {
		return Accion;
	}

	/**
	 * @return the tipo
	 */
	public int getTipo() {
		return Tipo;
	}

	/**
	 * @return the texto
	 */
	public String getTexto() {
		return Texto;
	}

	/**
	 * @return the data
	 */
	public Date getData() {
		return Data;
	}
}
