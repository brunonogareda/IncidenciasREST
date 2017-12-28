package es.brudi.incidencias.mensaxes;

import org.json.simple.JSONObject;


public enum Mensaxe
 {
  LOGIN_OK(0, "Sesión iniciada correctamente."), 
  LOGOUT_OK(0, "Sesión pechada correctamente."), 
  CHANGEMAIL_OK(0, "O Email do usuario modificouse correctamente."), 
  CHANGEPASS_OK(0, "O contrasinal do usuario modificouse correctamente."), 
  GETCLIENTES_OK(0, "Os clientes obtiveronse correctamente."), 
  GETINSTALACIONS_OK(0, "As instalacións obtiveronse correctamente."), 
  CREATEINCIDENCIA_OK(0, "Creouse correctamente a incidencia."), 
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

  
  @SuppressWarnings("unchecked")
  public JSONObject toJSONMensaxe() {
	JSONObject ret = new JSONObject();
    
    ret.put("  errno", Integer.valueOf(code));
    ret.put(" message", description);
    
    return ret;
  }
  
}