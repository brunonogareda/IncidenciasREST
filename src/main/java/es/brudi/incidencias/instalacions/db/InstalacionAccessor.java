package es.brudi.incidencias.instalacions.db;

import java.util.List;

import es.brudi.incidencias.instalacions.Instalacion;

/**
 * 
 * Realiza peticións contra o DAO e procesa os datos.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Agosto - 2018
 *
 */
public class InstalacionAccessor {

	private InstalacionAccessor() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @param id de instalación.
	 * @return Devolve o obxecto de instalación que corresponde co id.
	 */
	public static Instalacion getInstalacionById(int id) {
		return InstalacionDAO.getInstalacionById(id);
		
	}
	
	/**
	 * Obten todas as instalación que pertencen o cliente que se lle pasa.
	 * Se se lle pasa un -1, obtéñense todas as instalacións
	 * @param idCliente Id do cliente do que se quere obter as instalacions
	 * @return Lista de instalacions
	 */
	public static List<Instalacion> getInstalacionsByCliente(int idCliente) {
		return InstalacionDAO.getInstalacionsByCliente(idCliente);
	}
 }
