package es.brudi.incidencias.clientes.db;

import java.util.List;

import es.brudi.incidencias.clientes.Cliente;

public class ClienteAccessor {

	private ClienteAccessor() {
		 throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return Número de tuplas na táboa de Clientes.
	 */
	public static int count() {
	    return ClienteDAO.count();
	}
	
	/**
	 * @return ArrayList con todos os Clientes da base de datos.
	 */
	public static List<Cliente> getClientes() {
		return ClienteDAO.getClientes();
	}
	
	/**
	 * @param id de cliente
	 * @return Devolve o obxecto cliente que corresponde co Id que se lle pasou.
	 */
	public static Cliente getClienteById(int id) {
		return ClienteDAO.getClienteById(id);
	}
}
