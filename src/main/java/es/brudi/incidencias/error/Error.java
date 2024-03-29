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
	GETINSTALACIONS_SENINSTALACIONSXEST(164, "Non se encontraron instalacións xestionadas."),
	CREATEINCIDENCIA_NONEXISTEINST(170, "Non se encontra a instalación solicitada."),
	CREATEINCIDENCIA_INSTPERMISOS(171, "Non ten permisos para crear unha incidencia en esta instalación."),
	CREATEINCIDENCIA_ERRORCREANDO(172, "Problemas ao insertar a incidencia na base de datos"),
	CREATEINCIDENCIA_COMENTARIO(173, "Erro insertando o comentario de creación de incidencia."),
	OBTERINCIDENCIA_NONEXISTE(180, "A incidencia solicitada non se encontra."),
	OBTERINCIDENCIA_SENPERMISOS(181, "Non ten permisos suficientes para ver esta incidencia"),
	OBTERINCIDENCIAS_NONEXISTEN(190, "Non existe ningunha incidencia cos parámetros indicados"),
	OBTERINCIDENCIAS_ERRORDB(191, "Erro obtendo as incidencias na base de datos."),
	OBTERINCIDENCIAS_PARAMETRODATAERROR(192, "A data introducida non está no formato adecuado"),
	BORRARINCIDENCIA_SENPERMISOS(201, "Non ten privilexios para eliminar incidencias."),
	BORRARINCIDENCIA_ERRORDB(202, "Error eliminando a incidencia da base de datos."),
	MODIFESTADOINCIDENCIA_SENPERMISOS(210, "Non ten privilexios para cambiar o estado das incidencias."),
	MODIFESTADOINCIDENCIA_ERRORDB(212, "Error modificando o estado da incidencia na base de datos."),
	MODIFESTADOINCIDENCIA_ESTADOFAIL(213, "Non é posible establecer este estado."),
	CREARFACTURA_SENPERMISOS(220, "Non ten privilexios suficientes para engadir unha factura."),
	CREARFACTURA_ERRORESTADO(221, "O estado da incidencia debe ser Pentende de Facturar para poden engadir factura."),
	CREARFACTURA_DUPLICADA(222, "Non se puido crear a factura. Xa existe unha factura con ese identificador."),
	CREARFACTURA_ERRORDB(223, "Erro insertando a nova factura na base de datos."),
	CREARFACTURA_EXISTE(224, "A incidencia indicada xa ten unha factura asociada, non se pode asociar outra."),
	CREARFACTURA_FICHEIRO(225 , "Erro subindo o ficheiro factura. Insertase na base de datos correctamente."),
	OBTERFACTURA_NONEXISTE(230, "A Factura solicitada non existe."),
	OBTERFACTURA_SENPERMISOS(232, "Non ten privilexios suficientes para ver facturas."),
	OBTERFACTURA_ERRORDB(233, "Erro obtendo a factura da base de datos."),
	MODIFICARFACTURA_SENPERMISOS(241, "Non ten privilexios suficientes para editar facturas."),
	MODIFICARFACTURA_ERRORDB(242, "Erro modificando a factura na base de datos."),
	CREARPRESUPOSTO_SENPERMISOS(250, "Non ten privilexios suficientes para engadir un presuposto a esta incidencia."),
	CREARPRESUPOSTO_ERRORESTADO(251, "O estado da incidencia debe ser Pentende de Presupostar para poden engadir un presuposto."),
	CREARPRESUPOSTO_DUPLICADO(252, "Non se puido crear o presuposto. Xa existe un presuposto con ese identificador."),
	CREARPRESUPOSTO_ERRORDB(253, "Erro insertando o novo presuposto na base de datos."),
	CREARPRESUPOSTO_EXISTE(254, "A incidencia indicada xa ten un presuposto asociado, non se pode asociar outro."),
	CREARPRESUPOSTO_FICHEIRO(255 , "Erro subindo o ficheiro presuposto. Insertase na base de datos correctamente."),
	OBTERPRESUPOSTO_NONEXISTE(260, "O presuposto solicitado non existe."),
	OBTERPRESUPOSTO_SERPERMISOS1(261, "Non ten privilexios suficientes para ver este presuposto."),
	OBTERPRESUPOSTO_SENPERMISOS2(262, "Non ten privilexios suficientes para ver presupostos."),
	OBTERPRESUPOSTO_ERRORDB(263, "Erro obtendo o presuposto da base de datos."),
	MODIFICARPRESUPOSTO_SENPERMISOS(271, "Non ten privilexios suficientes para editar presupostos."),
	MODIFICARPRESUPOSTO_ERRORDB(272, "Erro modificando o presuposto na base de datos."),
	ACEPTARPRESUPOSTO_SENPERMISOS(280, "Non ten privilexios suficientes para marcar un presuposto como aceptado."),
	CREARIMAXE_SENPERMISOS(290, "Non ten privilexios suficientes para engadir imaxes."),
	CREARIMAXE_ERRODB(291, "Erro insertando a imaxe na base de datos."),
	CREARIMAXE_FICHEIRO(292, "Erro subindo o ficheiro imaxe. Insertouse a imaxe correctamente na base de datos."),
	OBTERIMAXE_NONEXISTE(300, "A imaxe solicitada non existe."),
	OBTERIMAXE_SENPERMISOS(302, "Non ten privilexios suficientes para ver imaxes."),
	OBTERIMAXE_ERRORDB(303, "Erro obtendo a imaxe da base de datos."),
	OBTERIMAXE_NONEXISTENAINC(304, "Esta incidencia non ten imaxes asociadas."),
	MODIFICARIMAXE_SENPERMISOS(311, "Non ten privilexios suficientes para editar imaxes."),
	MODIFICARIMAXE_ERRORDB(312, "Erro modificando a imaxe na base de datos."),
	INSERTARCOMENTARIO_ERROR(320, "Error insertando un comentario."),
	INSERTARCOMENTARIO_SENPERMISOS(322, "Non ten privilexios suficientes para insertar comentarios."),
	INSERTARCOMENTARIO_ERRORDB(323, "Erro insertando o comentario na base de datos."),
	OBTERCOMENTARIO_SENPERMISOS1(330, "Non ten privilexios suficientes para ver os comentarios de esta incidencia."),
	OBTERCOMENTARIO_SENPERMISOS2(331, "Non ten privilexios suficientes para ver comentarios."),
	OBTERCOMENTARIO_ERRORDB(332, "Erro obtendo os comentarios da base de datos."),
	OBTERCOMENTARIO_NONEXISTENAINC(333, "Esta incidencia non ten comentarios asociados."),
	CREARALBARAN_SENPERMISOS(340, "Non ten privilexios suficientes para engadir albaráns."),
	CREARALBARAN_ERRODB(341, "Erro insertando o albarán na base de datos."),
	CREARALBARAN_FICHEIRO(342, "Erro subindo o ficheiro albarán. Insertouse o albarán correctamente na base de datos."),
	OBTERALBARAN_NONEXISTE(350, "O albaran solicitado non existe."),
	OBTERALBARAN_SENPERMISOS(352, "Non ten privilexios suficientes para ver albaráns."),
	OBTERALBARAN_ERRORDB(353, "Erro obtendo o albarán da base de datos."),
	OBTERALBARAN_NONEXISTENAINC(354, "Esta incidencia non ten albaráns asociados."),
	MODIFICARALBARAN_SENPERMISOS(360, "Non ten privilexios suficientes para editar albaráns."),
	MODIFICARALBARAN_ERRORDB(362, "Erro modificando o albarán na base de datos."),

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
		JSONObject<String, Object> ret = new JSONObject<>();
		
		ret.put("errno", this.code);
		ret.put("message", description);
		
		return ret;
	}
	
}
