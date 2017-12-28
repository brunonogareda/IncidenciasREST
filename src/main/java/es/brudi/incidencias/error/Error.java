package es.brudi.incidencias.error;

import org.json.simple.JSONObject;

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
	CREATEINCIDENCIA_ERRORPARAM(170, "O formato dos parámetros introducidos é incorrecto."),
	CREATEINCIDENCIA_FALTANPARAM(172, "Faltan parámetros que son obligatorios."),
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
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONError() {
		JSONObject ret = new JSONObject();
		
		ret.put("  errno", this.code);
		ret.put(" message", description);
		
		return ret;
	}
	
}
