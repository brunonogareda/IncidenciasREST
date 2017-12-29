package es.brudi.incidencias.util;

import java.util.EmptyStackException;

/**
 * Clase que conten diferentes métodos con utilidades para o sistema.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
public class Util {

	/**
	 * Comproba se un String é numérico
	 * @param text
	 * @return
	 */
	public static boolean isNumeric(String text) {
		try {
			Integer.parseInt(text);
			return true;
		}
		catch(NumberFormatException ne) {
			return false;
		}
	}
	
	/**
	 * Converte un int a un String. Se o parámetro nulo é true e o string e un nulo, devolve un -1. Se o formato do string é incorrecto devolve unha excepcion de NumberFormatException.
	 * Se o parámetro nulo é un false e o string é nulo, devolve unha excepción EmptyStackException.
	 * @param nulo
	 * @param text
	 * @return
	 * @throws EmptyStackException - En caso de que a parámetro nulo sea false e o text sexa null.
	 * @throws NumberFormatException - En caso de que o text non se poida parsear un un int.
	 */
	public static int stringToInt(boolean nulo, String text) throws EmptyStackException, NumberFormatException {
		if(nulo && text == null) {
			return -1;
		}
		else if(text != null) {
			return Integer.parseInt(text);
		}
		else {
			throw new EmptyStackException();
		}
	}

}
