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
	
	private final String estadoS;
	private final Estado[] estadosSeg; 
	
	private Estado(String estado, Estado[] estadosSeg) {
		this.estadoS = estado;
		this.estadosSeg = estadosSeg;
	}
	
	public String getEstado() {
		return estadoS;
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
		//Se o estado é un dos seguintes non se pode cambiar de forma manual.
		if (estado.equals(Estado.FACTURADO) || estado.equals(Estado.PENDENTE_A) ||
				estado.equals(Estado.PENDENTE_P) || estado.equals(Estado.PENDENTE_R))
			return false;
		
		for(int i=0; i < this.estadosSeg.length; i++) {
			if (this.estadosSeg[i].equals(estado))
				return true;
		}
		return false;
	}
	
}
