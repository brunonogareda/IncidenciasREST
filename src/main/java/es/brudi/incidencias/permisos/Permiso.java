package es.brudi.incidencias.permisos;

/**
 * 
 * Diferentes niveles de permisos e as posicións.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public abstract class Permiso {

	public static final int POS_VER_INCIDENCIA = 0;
	public static final int POS_INCIDENCIA = 1;
	public static final int POS_SOL_PRESUPOSTO = 2;
	public static final int POS_PRESUPOSTO_ACEPTADO = 3;
	public static final int POS_PRESUPOSTO = 4;
	public static final int POS_FACTURA = 5;
	public static final int POS_IMAXES = 6;
	public static final int POS_ALBARANS = 7;
	public static final int POS_COMENTARIOS = 8;
	
	public static final int VER_INCIDENCIA_PROPIA = 1;
	public static final int VER_INCIDENCIA = 2;
	public static final int CREAR_INCIDENCIA = 1;
	public static final int MODIFICAR_ESTADO_INCIDENCIA = 2;
	public static final int MODIFICAR_INCIDENCIA = 3;
	public static final int BORRAR_INCIDENCIA = 4;
	public static final int MARCAR_SOL_PRESUPOSTO = 1;
	public static final int MARCAR_PRESUPOSTO_ACEPTADO = 1;
	public static final int VER = 1;
	public static final int COMENTAR = 2;
	public static final int SUBIR = 3;
	public static final int MODIFICAR = 4;

	
	
	//Administrador 		2310444440
	//Técnico				2200004320
	//Cliente_creacion 		1100003010
	//Clientes_facturación	2111212020
}