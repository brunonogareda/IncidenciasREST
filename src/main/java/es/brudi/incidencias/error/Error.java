package es.brudi.incidencias.error;

import es.brudi.incidencias.util.JSONObject;

/**
 * 
 * Tipo Enum con unha lista de erros posibles do sistema, formados por un número de error e un texto que explica o erro de forma global.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public enum Error {
	
	DATABASE(1, "Non se puido establecer conexión coa base de datos."),
	ERRORPARAM(60, "O formato dos parámetros introducidos é incorrecto."),
	FALTANPARAM(60, "Faltan parámetros que son obligatorios."),
	LOGIN_SENPARAMETROS(100, "Debe introducir o usuario e o contrasinal para iniciar sesión."),
	LOGIN_USER(101, "Usuario ou contrasinal erroneos."),
	USER_NOLOGIN(110, "Para executar esta acción o usuario debe estar logueado."),
	USER_NOPERMISOS(111, "Non ten permisos suficientes para executar esta acción."),
	CHANGEMAIL_SENPARAMETROS(120, "Debe introducir o novo email."),
	CHANGEMAIL_ERROR(121, "Non se puido cambiar o email do usuario."),
	CHANGEMAIL_ERRORPARAMETROS(122, "O formato de email intrododucido non é o correcto."),
	CHANGEPASS_SENPARAMETROANT(130, "Debe introducir o contrasinal antigo para poder mudalo."),
	CHANGEPASS_SENPARAMETROPASS(131, "Debe introducir o novo contrasinal por duplicalo."),
	CHANGEPASS_ERRORPASS(132, "O contrasinal introducido é incorrecto."),
	CHANGEPASS_DIFERENTES(133, "Os contrasinais introducidos deben ser idénticos."),
	CHANGEPASS_ERROR(134, "Non se puido cambiar o contrasinal do usuario."),
	GETCLIENTES_ERRORDB(150, "Erro obtendo os clientes."),
	GETCLIENTES_SENCLIENTES(151, "Non se encontraron clientes."),
	GETINSTALACIONS_ERRORPARAMNUM(160, "O id de cliente debe ser un número maior ou igual que 0."),
	GETINSTALACIONS_ERRORDB(161, "Erro obtendo as instalacións."),
	GETINSTALACIONS_SENINSTALACIONS(162, "Non se encontraron instalacións de este cliente."),
	GETINSTALACIONS_SENPERMISOS(163, "Non ten permisos suficientes para consultar as instalacións de este cliente."),
	CREATEINCIDENCIA_NONEXISTEINST(170, "Non se encontra a instalación solicitada."),
	CREATEINCIDENCIA_INSTPERMISOS(171, "Non ten permisos para crear unha incidencia en esta instalación."),
	CREATEINCIDENCIA_ERRORCREANDO(172, "Problemas ao insertar a incidencia na base de datos"),
	CREATEINCIDENCIA_COMENTARIO(173, "Erro insertando o comentario de creación de incidencia."),
	OBTERINCIDENCIA_NONEXISTE(180, "A incidencia solicitada non se encontra."),
	OBTERINCIDENCIA_SENPERMISOS(181, "Non ten permisos suficientes para ver esta incidencia"),
	OBTERINCIDENCIAS_NONEXISTEN(190, "Non existe ningunha incidencia cos parámetros indicados"),
	OBTERINCIDENCIAS_ERRORDB(191, "Erro obtendo as incidencias na base de datos."),
	OBTERINCIDENCIAS_PARAMETRODATAERROR(192, "A data introducida non está no formato adecuado"),
	BORRARINCIDENCIA_SENPERMISOS1(200, "Non ten privilexios suficientes para eliminar esta incidencia."),
	BORRARINCIDENCIA_SENPERMISOS2(201, "Non ten privilexios para eliminar incidencias."),
	BORRARINCIDENCIA_ERRORDB(202, "Error eliminando a incidencia da base de datos."),
	MODIFESTADOINCIDENCIA_SENPERMISOS1(210, "Non ten privilexios para cambiar o estado das incidencias."),
	MODIFESTADOINCIDENCIA_SENPERMISOS2(212, "Non ten privilexios para cambiar o estado de esta incidencia."),
	MODIFESTADOINCIDENCIA_ERRORDB(212, "Error modificando o estado da incidencia na base de datos."),
	CREARFACTURA_SENPERMISOS(220, "Non ten privilexios suficientes para engadir unha factura a esta incidencia."),
	CREARFACTURA_ERRORESTADO(221, "O estado da incidencia debe ser Pentende de Facturar para poden engadir factura."),
	CREARFACTURA_DUPLICADA(222, "Non se puido crear a factura. Xa existe unha factura con ese identificador."),
	CREARFACTURA_ERRORDB(223, "Erro insertando a nova factura na base de datos."),
	CREARFACTURA_EXISTE(224, "A incidencia indicada xa ten unha factura asociada, non se pode asociar outra."),
	CREARFACTURA_FICHEIRO(225 , "Erro subindo o ficheiro factura. Insertase na base de datos correctamente."),
	OBTERFACTURA_NONEXISTE(230, "A Factura solicitada non existe."),
	MODIFICARFACTURA_SENPERMISOS1(240, "Non ten privilexios suficientes para editar esta factura."),
	MODIFICARFACTURA_SENPERMISOS2(241, "Non ten privilexios suficientes para editar facturas."),
	MODIFICARFACTURA_ERRORDB(242, "Erro modificando a factura na base de datos."),
	INSERTARCOMENTARIO_ERROR(300, "Error insertando un comentario."),
	
	DEFAULT(-1, "Erro descoñecido.");
	
	private final int code;
	private final String description;

	private Error(int code, String description) {
	  this.code = code;
	  this.description = description;
	}

	public String getDescription() {
	   return description;
	}

	public int getCode() {
	   return code;
	}

	@Override
	public String toString() {
	  return code + ": " + description;
	}
	
	public JSONObject<String, Object> toJSONError() {
		JSONObject<String, Object> ret = new JSONObject<String, Object>();
		
		ret.put("errno", this.code);
		ret.put("message", description);
		
		return ret;
	}
	
}
