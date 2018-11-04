package es.brudi.incidencias.facturas.db;

import java.util.ArrayList;
import java.util.List;

import es.brudi.incidencias.facturas.Factura;
import es.brudi.incidencias.incidencias.Incidencia;
import es.brudi.incidencias.incidencias.db.IncidenciaAccessor;
import es.brudi.incidencias.usuarios.Usuario;

public class FacturaAccessor {

	private FacturaAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Inserta unha nova factura na base de datos.
	 * @param id
	 * @param rutaFicheiro
	 * @param tipoFicheiro
	 * @param comentarios
	 * @return Factura creada
	 */
	public static Factura crear(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		Factura fact = null;
		if(FacturaDAO.crear(id, rutaFicheiro, tipoFicheiro, comentarios))
			fact = new Factura(id, rutaFicheiro, tipoFicheiro, comentarios);
		return fact;
	}
	
	/**
	 * Obten unha factura mediante o id.
	 * @param id
	 * @return
	 */
	public static Factura obterPorId(String id) {
		return FacturaDAO.obterPorId(id);
	}
	
	/**
	 * Obten unha factura mediante o id. Sempre que o usuario xestione todas as instalacións coas que se relaciona
	 * @param id
	 * @param user
	 * @return
	 */
	public static Factura obterPorId(String id, Usuario user) {
		Factura fact = FacturaDAO.obterPorId(id);
		if(fact != null) {
			List<Incidencia> incidencias = IncidenciaAccessor.obterPorFactura(id);
			List<Integer> idIncidencias = new ArrayList<>();
			for(Incidencia inc : incidencias) {
				idIncidencias.add(inc.getId());
				if(!user.xestionaInstalacion(inc.getInstalacion().getId()))
					return null;
			}
			fact.setIdIncidencias(idIncidencias);
		}
		return fact;
	}
	
//	public static Factura obterPorIdIncidencia(int id, Usuario user) {
//		return FacturaDAO.obterPorIdIncidencia(id);
//	}

	/**
	 * Modifica os parámetros da factura na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param comentarios - Comentarios da factura. NULL non o modifica.
	 * @return Factura modificada
	 */
	public static Factura modificar(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		Factura fact = null;
		if(FacturaDAO.modificar(id, rutaFicheiro, tipoFicheiro, comentarios))
			fact = new Factura(id, rutaFicheiro, tipoFicheiro, comentarios);
		return fact;
	}
	
	
}
