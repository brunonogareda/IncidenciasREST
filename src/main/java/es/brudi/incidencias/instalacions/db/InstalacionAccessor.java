package es.brudi.incidencias.instalacions.db;

import java.util.ArrayList;
import java.util.List;

import es.brudi.incidencias.instalacions.Instalacion;
import es.brudi.incidencias.usuarios.Usuario;

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
	public static Instalacion obterInstalacionPorId(int id) {
		return InstalacionDAO.obterInstalacionPorId(id);
		
	}
	
	/**
	 * Obten todas as instalación que pertencen o cliente que se lle pasa.
	 * e que xestione o usuario que realice a consulta.
	 * Se se lle pasa un -1, obtéñense todas as instalacións
	 * @param idCliente Id do cliente do que se quere obter as instalacions
	 * @param user
	 * @return Lista de instalacions
	 */
	public static List<Instalacion> obterInstalacionsPorCliente(int idCliente, Usuario user) {
		List<Instalacion> instalacions = InstalacionDAO.obterInstalacionsPorCliente(idCliente);
		List<Instalacion> ret = new ArrayList<>();
		for(Instalacion inst : instalacions) {
			if(user.xestionaInstalacion(inst.getId()))
				ret.add(inst);
		}
		return ret;
	}

	/**
	 * Obten todas as instalacions xestionadas polo usuario que se pasa.
	 * Tanto as xestionadas directamentes polo usuario como poloo grupo o que pertence.
	 * @param user
	 * @return
	 */
	public static List<Instalacion> obterInstalacionsXestionadas(Usuario user) {
		List<Instalacion> instalacions;
		instalacions = InstalacionDAO.obterInstalacionsXestionadasUsuario(user.getId());
		instalacions.addAll(InstalacionDAO.obterInstalacionsXestionadasGrupo(user.getGrupo().getId()));
		return instalacions;
	}
 }
