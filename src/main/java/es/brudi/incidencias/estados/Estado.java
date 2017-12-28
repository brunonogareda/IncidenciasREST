package es.brudi.incidencias.estados;

/**
 * 
 * Tipo Enum co un grupo de estados posibles de unha incidencia
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public enum Estado {

	PENDENTE_R("Pendente de Realizar"),
	PENDENTE_P("Pendente de Presupostar"),
	PENDENTE_F("Pendente de Faturar"),
	REALIZADO("Realizado"),
	PECHADO("Pechado"),
	DEFAULT("Outro");
	
	private final String estado;
	
	private Estado(String estado) {
		this.estado = estado;
	}
	
	public String getEstado() {
		return estado;
	}
	
}
