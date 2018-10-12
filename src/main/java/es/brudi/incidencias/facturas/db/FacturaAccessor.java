package es.brudi.incidencias.facturas.db;

import es.brudi.incidencias.facturas.Factura;

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
	 * @return
	 */
	public static boolean crear(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		return FacturaDAO.crear(id, rutaFicheiro, tipoFicheiro, comentarios);
	}
	
	/**
	 * Obten unha factura mediante o id.
	 * @param id
	 * @return
	 */
	public static Factura getById(String id) {
		return FacturaDAO.getById(id);
	}

	/**
	 * Modifica os parámetros da factura na base de datos
	 * @param id
	 * @param rutaFicheiro - Ruta completa do ficheiro. NULL non o modifica.
	 * @param tipoFicheiro - Extensión do ficheiro. NULL non o modifica.
	 * @param comentarios - Comentarios da factura. NULL non o modifica.
	 * @return
	 */
	public static boolean modificar(String id, String rutaFicheiro, String tipoFicheiro, String comentarios) {
		return FacturaDAO.modificar(id, rutaFicheiro, tipoFicheiro, comentarios);
	}
	
	
}
