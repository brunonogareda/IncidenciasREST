package es.brudi.incidencias.mensaxes;

import es.brudi.incidencias.util.JSONObject;


public enum Mensaxe
 {
  LOGIN_OK(0, "Sesión iniciada correctamente."), 
  LOGOUT_OK(0, "Sesión pechada correctamente."), 
  CHANGEMAIL_OK(0, "O Email do usuario modificouse correctamente."), 
  CHANGEPASS_OK(0, "O contrasinal do usuario modificouse correctamente."), 
  GETCLIENTES_OK(0, "Os clientes obtiveronse correctamente."), 
  GETINSTALACIONS_OK(0, "As instalacións obtiveronse correctamente."), 
  CREATEINCIDENCIA_OK(0, "Creouse correctamente a incidencia."),
  GETINCIDENCIA_ID_OK(0, "Obtivose a incidencia correctamente."),
  GETINCIDENCIAS_OK(0, "As incidencias obtiveronse correctamente."),
  BORRARINCIDENCIA_OK(0, "A incidencia eliminouse correctamente"),
  MODIFESTADOINCIDENCIA_OK(0, "O estado da incidencia modificouse correctamente."),
  CREARFACTURA_OK(0, "A factura engadiuse correctamente."),
  OBTERFACTURA_OK(0, "Obtivose a factura correctamente."),
  MODIFICARFACTURA_OK(0, "A factura foi modificada correctamente."),
  CREARPRESUPOSTO_OK(0, "O presuposto engadiuse correctamente."),
  OBTERPRESUPOSTO_OK(0, "Obtivose o presuposto correctamente."),
  MODIFICARPRESUPOSTO_OK(0, "O presuposto foi modificado correctamente."),
  CREARIMAXE_OK(0, "Insertouse a imaxe correctamente."),
  OBTERIMAXE_OK(0, "Obtivose a imaxe correctamente."),
  OBTERIMAXESINC_OK(0, "Obtiveronse as imaxes da incidencia correctamente"),
  MODIFICARIMAXE_OK(0, "A imaxe foi modificada correctamente."),
  INSERTARCOMENTARIO_OK(0, "O comentario engadiuse correctamente."),
  OBTERCOMENTARIOSNC_OK(0, "Obtiveronse os comentarios da incidencia correctamente"),
  DEFAULT(0, "Ok.");
  
  private final int code;
  private final String description;
  
  private Mensaxe(int code, String description) {
    this.code = code;
    this.description = description;
  }
  
  public String getDescription() {
    return description;
  }
  
  public int getCode() {
    return code;
  }
  
  public String toString() {
    return code + ": " + description;
  }

  
  public JSONObject<String, Object> toJSONMensaxe() {
	JSONObject<String, Object> ret = new JSONObject<String, Object>();
    
    ret.put("errno", Integer.valueOf(code));
    ret.put("message", description);
    
    return ret;
  }
  
}