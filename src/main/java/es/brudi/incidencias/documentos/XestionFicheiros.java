package es.brudi.incidencias.documentos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * 
 * Clase que xestiona e almacena os temas relacionados co almacenamento de ficheiros.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Xaneiro - 2018
 * 
 */
public class XestionFicheiros {
	
	private static Logger logger = Logger.getLogger(XestionFicheiros.class);
	private static String rutaFacturas = "/home/bruno/incidencias/ficheiros/facturas";
	private static String rutaPresupostos = "/home/bruno/incidencias/ficheiros/presupostos";
	private static String rutaImaxes = "/home/bruno/incidencias/ficheiros/imaxes";
	private static String rutaAlbarans = "/home/bruno/incidencias/ficheiros/albarans";
	
	private XestionFicheiros() {
	    throw new IllegalStateException("Utility class");
	}
	
	/**
	 * @return the rutaFacturas
	 */
	public static String getRutaFacturas() {
		return rutaFacturas;
	}

	/**
	 * @param rutaFacturas the rutaFacturas to set
	 */
	public static void setRutaFacturas(String rutaFacturas) {
		XestionFicheiros.rutaFacturas = rutaFacturas;
	}

	/**
	 * @return the rutaPresupostos
	 */
	public static String getRutaPresupostos() {
		return rutaPresupostos;
	}

	/**
	 * @param rutaPresupostos the rutaPresupostos to set
	 */
	public static void setRutaPresupostos(String rutaPresupostos) {
		XestionFicheiros.rutaPresupostos = rutaPresupostos;
	}

	/**
	 * @return the rutaImaxes
	 */
	public static String getRutaImaxes() {
		return rutaImaxes;
	}

	/**
	 * @param rutaImaxes the rutaImaxes to set
	 */
	public static void setRutaImaxes(String rutaImaxes) {
		XestionFicheiros.rutaImaxes = rutaImaxes;
	}

	/**
	 * @return the rutaAlbarans
	 */
	public static String getRutaAlbarans() {
		return rutaAlbarans;
	}

	/**
	 * @param rutaAlbarans the rutaAlbarans to set
	 */
	public static void setRutaAlbarans(String rutaAlbarans) {
		XestionFicheiros.rutaAlbarans = rutaAlbarans;
	}

	public static void updateParams(String rutaFacturas, String rutaPresupostos, String rutaImaxes, String rutaAlbarans) {
		if(rutaFacturas != null ) XestionFicheiros.rutaFacturas = rutaFacturas;
		if(rutaPresupostos != null ) XestionFicheiros.rutaPresupostos = rutaPresupostos;
		if(rutaImaxes != null ) XestionFicheiros.rutaImaxes = rutaImaxes;
		if(rutaAlbarans != null ) XestionFicheiros.rutaAlbarans= rutaAlbarans;
	}

	/**
	 * Sube un ficheiro desde un fluxo e gardao en local. Comproba se existe a localización
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return - Ruta total do ficheiro.
	 */
	public static String subirFicheiroEGardar(InputStream uploadedInputStream, String ruta, String nomeFicheiro) {
		
		//Creamos a carpeta se non existe.
		if(createFolderIfNotExists(ruta)) {
			//Se non hay problema gardamos o ficheiro.
			return gardarFicheiro(uploadedInputStream, ruta, nomeFicheiro);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Obten a ruta onde se gardará o ficheiro
	 * 
	 * @param rutaBase
	 * @param data
	 * @return
	 */
	public static String getRuteToFile(String rutaBase, Calendar data) {
		return rutaBase + "/" + data.get(Calendar.YEAR) + "/" + (data.get(Calendar.MONTH)+1);
	}
	
	/**
	 * Obten a ruta onde se gardará o ficheiro
	 * 
	 * @param rutaBase
	 * @param data
	 * @param id
	 * @return
	 */
	public static String getRuteToFile(String rutaBase, Calendar data, int id) {
		return getRuteToFile(rutaBase, data) + "/" + id;
	}
	
	/**
	 * Garda un ficheiro na localización target.
	 * 
	 * @param inStream - Fluxo de entrada do ficheiro.
	 * @param rutaFicheiro - Ruta onde gardar o ficheiro.
	 * @param nomeFicheiro - Nome de ficheiro a gardar.
	 * @return
	 */
	private static String gardarFicheiro(InputStream inStream, String rutaFicheiro, String nomeFicheiro) {
		int read = 0;
		
		File file = new File(rutaFicheiro, nomeFicheiro);
		try (OutputStream out = new FileOutputStream(file)) {
			byte[] bytes = new byte[1024];
			while ((read = inStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			logger.debug("Gardouse o ficheiro: "+file.getPath());
		}
		catch(IOException ioe) {
			logger.error("Erro gardando o ficheiro en: "+file.getPath());
			logger.error("Exception saving file: ", ioe);
			return null;
		}
		return file.getPath();
	}
	
	
	/**
	 * Comproba se existe unha carpeta e creea en caso de non existir.
	 * 
	 * @param dirName - Ruta do directorio.
	 */
	private static boolean createFolderIfNotExists(String dirName) {
		try {
			File theDir = new File(dirName);
			if (!theDir.exists()) {
				theDir.mkdirs();
				logger.debug("Creando o directorio: "+dirName);
			}
		}
		catch(Exception e) {
			logger.error("Exception creating folder: ", e);
			return false;
		}
		return true;
	}

	/**
	 * Borra o ficheiro que se lle pasa na ruta.
	 * 
	 * @param rutaFicheiro
	 * @return
	 */
	public static boolean borrar(String rutaFicheiro) {
		try {
			File file = new File(rutaFicheiro);
			if (file.exists()) {
				Files.delete(file.toPath());
				logger.debug("Borrouse o ficheiro: "+rutaFicheiro);
			}
			else {
				return false;
			}
		}
		catch(Exception e) {
			logger.error("Exception delete file: ", e);
			return false;
		}
		return true;
		
	}
	
	/**
	 * Obten o ficheiro.
	 * 
	 * @param rutaFicheiro
	 * @return
	 */
	public static File obterFicheiro(String rutaFicheiro) {
		File file = null;
		try {
			file = new File(rutaFicheiro);
		}
		catch(Exception e) {
			logger.error("Erro obtendo o ficheiro: "+rutaFicheiro);
			logger.error("Exception: ", e);
		}
		return file;
	}
	
}
