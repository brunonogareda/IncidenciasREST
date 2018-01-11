package es.brudi.incidencias.incidencias.estados;


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
	
	PECHADO("Pechado", new Estado[]{}),
	FACTURADO("Facturado", new Estado[]{Estado.PECHADO}),
	PENDENTE_F("Pendente de Facturar", new Estado[]{Estado.FACTURADO}),
	REALIZADO("Realizado", new Estado[]{Estado.PENDENTE_F}),
	PENDENTE_REV("Pendente de Revisar", new Estado[]{Estado.REALIZADO}),
	PENDENTE_R("Pendente de Realizar", new Estado[]{Estado.PENDENTE_REV, Estado.REALIZADO}),
	PENDENTE_A("Pendente de Aceptar", new Estado[]{Estado.PENDENTE_R}),
	PENDENTE_P("Pendente de Presupostar", new Estado[]{Estado.PENDENTE_A}),
	DEFAULT("Outro", new Estado[]{});
	
	private final String estado;
	private final Estado[] estados_seg; 
	
	private Estado(String estado, Estado[] estados_seg) {
		this.estado = estado;
		this.estados_seg = estados_seg;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public static Estado getByString(String estado) {
		
		Estado[] est = Estado.values();
		
		for(Estado e : est) {
			if(e.getEstado().equals(estado))
				return e;
		}
		
		return Estado.DEFAULT;

	}
	
	public boolean estadoSegPosible(Estado estado) {
		for(int i=0; i < this.estados_seg.length; i++) {
			if (this.estados_seg[i].equals(estado))
				return true;
		}
		return false;
	}
	
	
	public boolean equals(Estado estado) {
		return (this.getEstado().equals(estado.getEstado()));
	}
}
