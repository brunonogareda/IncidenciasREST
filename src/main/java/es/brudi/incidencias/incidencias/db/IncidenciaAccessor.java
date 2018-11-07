package es.brudi.incidencias.incidencias.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.estados.Estado;
import es.brudi.incidencias.instalacions.Instalacion;
import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.util.Util;

public class IncidenciaAccessor {

	private IncidenciaAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @param id de Incidencia
	 * @return Devolve o obxecto incidencia que corresponde co Id que se lle pasou.
	 */
	public static Incidencia obterPorId(int id) {
		return IncidenciaDAO.obterPorId(id);
	}
	
	/**
	 * 
	 * @param id
	 * @param user
	 * @return Devolve o obxecto incidencia que corresponde co Id que se lle pasou, sempre que pertenza a unha instalación que xestione o usuario.
	 */
	public static Incidencia obterPorId(int id, Usuario user) {
		Incidencia inc = IncidenciaDAO.obterPorId(id);
		if(inc != null && !user.xestionaInstalacion(inc.getInstalacion().getId()))
			inc = null;
		
		return inc;
	}
	
	/**
	 * @param id de Factura
	 * @return Devolve unha lista de incidencias que conteñan a factura correspondente.
	 */
	public static List<Incidencia> obterPorFactura(String factura) {
		return IncidenciaDAO.obterPorFactura(factura);
	}

	/**
	 * @param id de Presuposto
	 * @return Devolve unha lista de incidencias que que conteña o presuposto correspondente.
	 */
	public static List<Incidencia> obterPorPresuposto(String presuposto) {
		return IncidenciaDAO.obterIncidenciaPorPresuposto(presuposto);
	}
	
	

	/**
	 * Inserta unha nova incidencia na base de datos.
	 * 
	 * @param codParte - Se ven un 0, establecese na base de datos a null.
	 * @param ot - Se ven un 0, establecese na base de datos a null.
	 * @param idInstalacion
	 * @param zonaApartamento
	 * @param descripcionCurta
	 * @param observacions
	 * @param estado
	 * @param solPresuposto
	 * @param data
	 * @param autor
	 * @return - Un obxecto coa nova incidencia. Null se ocorreu un erro.
	 */
	public static Incidencia crear(int codParte, int ot, Instalacion instalacion, String zonaApartamento,
			String descripcionCurta, String observacions, Estado estado, boolean solPresuposto, int autor) {
		Incidencia ret = null;
		Calendar data = Util.obterCalendarActual();
		int id = IncidenciaDAO.crear(codParte, ot, instalacion.getId(), zonaApartamento, descripcionCurta, observacions,
				estado.getEstado(), solPresuposto, data, autor);
		if (id >= 0)
			ret = new Incidencia(id, codParte, ot, instalacion, zonaApartamento, descripcionCurta, observacions, estado,
					solPresuposto, null, null, data, autor);

		return ret;
	}

	/**
	 * Busca na base de datos as incidencias que coincidan cos parámetros.
	 * 
	 * @param codParte - Cod_parte a buscar. 0: busca calquera cod_parte. -1: busca os cod_parte nulos.
	 * @param ot - Orden_traballo a buscar. 0: busca calquera ot. -1: busca os ot nulos.
	 * @param idInstalacion - Id_instalacions a buscar. 0: busca calquera instalación.
	 * @param zonaApartamento - Zona_apartamento a buscar. NULL: busca calquera zona_apartamento.
	 * @param descripcionCurta - Descripción_curta a buscar. NULL: busca calquera descripcion_curta.
	 * @param observacions - Observacions a buscar. NULL: busca calquera observacions.
	 * @param estado - Estado a buscar. NULL: busca calquera estado.
	 * @param solPresuposto - sol_presuposto a "true" ou a "false". NULL: busca calquera sol_presuposto
	 * @param presuposto - presuposto a buscar. NULL: busca calquera presuposto. "-1": busca os presupostos nulos.
	 * @param factura - factura a buscar. NULL: busca calquera factura. "-1": busca as facutras nulas.
	 * @param dataMenor - busca datas maiores que esta. NULL: busca calquera data.
	 * @param dataMaior - busca datas menores que esta. NULL: busca calquera data.
	 * @param autor - autora buscar. NULL: busca calquera autor.
	 * @param codCliente - Cod_cliente a buscar. 0: busca calquera cliente.
	 * @param ver - Número de incidencias máximas a buscar. NULL: Sen límite.
	 * @param user - Usuario que realiza a consulta
	 * @return
	 */
	public static List<Incidencia> obter(int codParte, int ot, int idInstalacion,
			String zonaApartamento, String descripcionCurta, String observacions, List<String> estados,
			String solPresuposto, String presuposto, String factura, Calendar dataMenor, Calendar dataMaior,
			int autor, int ver, Usuario user) {
		List<Incidencia> incidencias = IncidenciaDAO.obter(codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions, estados, solPresuposto, presuposto, factura, dataMenor, dataMaior, autor, ver);
		List<Incidencia> ret = new ArrayList<>();
		for(Incidencia inc : incidencias) {
			if(user.xestionaInstalacion(inc.getInstalacion().getId())) //Solo se devolven as incidencias das instalacions que xestiona o usuario.
				ret.add(inc);
		}
		return ret;
	}
	
	/**
	 * Modifica varios parámetros da incidencia que corresponda co Id que se lle pasa.
	 * 
	 * @param id - Identificador da incidencia que se quere modificar.
	 * @param codParte - valor que se quere dar a cod_parte. 0: non se modifica. -1: ponse a nulo.
	 * @param ot - valor que se quere dar a Orden de traballo. 0: non se modifica. -1: ponse a nulo.
	 * @param idInstalacion - instalación a que se lle asigna a incidencia. 0: non se modifica.
	 * @param zonaApartamento - valor que se lle da a zona_apartamento. NULL: non se modifica.
	 * @param descripcionCurta - valor que se lle da a descripcion_curta. NULL: non se modifica.
	 * @param observacions - valor que se lle asigna a observacions. NULL: non se modifica.
	 * @param estado - valor que se lle asigna a estado. NULL: non se modifica.
	 * @param solPresuposto - Ponse a true ou a false. NULL: non se modifica.
	 * @return - True en caso de que se modifica correctamente. False ocorreu algún erro.
	 */
	public static boolean modificarIncidencia(int id, int codParte, int ot, int idInstalacion, String zonaApartamento,
			String descripcionCurta, String observacions, String estado, String solPresuposto) {
		return IncidenciaDAO.modificarIncidencia(id, codParte, ot, idInstalacion, zonaApartamento, descripcionCurta, observacions, estado, solPresuposto);
	}
	

	/**
	 * Modifica o estado da incidencia
	 * @param id
	 * @param estado
	 * @return
	 */
	public static boolean modificarEstado(int id, String estado) {
		return IncidenciaDAO.modificarIncidencia(id, 0, 0, 0, null, null, null, estado, null);
	}
	
	/**
	 * Modifica a factura en unha incidencia e cambia o estado da incidencia
	 * @param idIncidencia
	 * @param idFactura
	 */
	public static boolean modifcarFacturaEstado(int idIncidencia, String idFactura, String estado) {
		return IncidenciaDAO.modifcarFacturaEstado(idIncidencia, idFactura, estado);
	}
	
	/**
	 * Modifica o presuposto en unha incidencia e cambia o estado da incidencia
	 * @param idIncidencia
	 * @param idPresuposto
	 */
	public static boolean modifcarPresupostoEstado(int idIncidencia, String idPresuposto, String estado) {
		return IncidenciaDAO.modifcarPresupostoEstado(idIncidencia, idPresuposto, estado);
	}

	
	/**
	 * Elimina unha incidencia
	 * 
	 * @param id
	 * @return
	 */
	public static boolean delete(int id) {
		return IncidenciaDAO.eliminar(id);
	}
	
	
}
