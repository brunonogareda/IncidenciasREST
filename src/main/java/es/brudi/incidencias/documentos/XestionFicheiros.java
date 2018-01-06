package es.brudi.incidencias.documentos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static String RUTA_FACTURAS = "/home/bruno/incidencias/ficheiros/facturas";
	public static String RUTA_PRESUPOSTOS = "/home/incidencias/ficheiros/presupostos";
	public static String RUTA_IMAXES = "/home/bruno/incidencias/ficheiros/imaxes";
	public static String RUTA_ALBARANS = "/home/bruno/incidencias/ficheiros/albarans";
	
	
	public static void updateParams(String rutaFacturas, String rutaPresupostos, String rutaImaxes, String rutaAlbarans) {
		if(rutaFacturas != null ) RUTA_FACTURAS = rutaFacturas;
		if(rutaPresupostos != null ) RUTA_PRESUPOSTOS = rutaPresupostos;
		if(rutaImaxes != null ) RUTA_IMAXES = rutaImaxes;
		if(rutaAlbarans != null ) RUTA_ALBARANS = rutaAlbarans;
	}

	/**
	 * Sube un ficheiro desde un fluxo e gardao en local. Comproba se existe a localización
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return - Ruta total do ficheiro.
	 */
	public static String subirFicheiroEGardar(InputStream uploadedInputStream, String ruta, String nomeFicheiro) {
		boolean ok = false;
		
		
		//Creamos a carpeta se non existe.
		ok = createFolderIfNotExists(ruta);

		String CompletePathToFile = ruta + "/" + nomeFicheiro;
		
		//Se non hay problema gardamos o ficheiro.
		if(ok)
			ok = gardarFicheiro(uploadedInputStream, CompletePathToFile);
		
		if(ok)
			return CompletePathToFile;
		
		return null;
	}
	
	/**
	 * Obten a ruta onde se gardará o ficheiro
	 * 
	 * @param ruta
	 * @param data
	 * @return
	 */
	public static String getRuteToFile(String ruta, Calendar data) {
		return ruta + "/" + data.get(Calendar.YEAR) + "/" + (data.get(Calendar.MONTH)+1);
	}
	
	/**
	 * Garda un ficheiro na localización target.
	 * 
	 * @param inStream - Fluxo de entrada do ficheiro.
	 * @param target - Localización onde gardar o ficheiro.
	 * @return
	 */
	private static boolean gardarFicheiro(InputStream inStream, String target) {
		OutputStream out = null;
		int read = 0;
		
		try {
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(target));
			while ((read = inStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			logger.debug("Gardouse o ficheiro: "+target);
		}
		catch(IOException ioe) {
			logger.error("Erro gardando o ficheiro en: "+target);
			logger.error(ioe);
			return false;
		}
		finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return true;
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
			logger.error(e);
			return false;
		}
		return true;
	}

	/**
	 * Borra o ficheiro que se lle pasa na ruta.
	 * 
	 * @param ruta_ficheiro
	 * @return
	 */
	public static boolean borrar(String ruta_ficheiro) {
		try {
			File file = new File(ruta_ficheiro);
			if (file.exists()) {
				file.delete();
				logger.debug("Borrouse o ficheiro: "+ruta_ficheiro);
			}
			else {
				return false;
			}
		}
		catch(Exception e) {
			logger.error(e);
			return false;
		}
		return true;
		
	}
	
	/**
	 * Obten o ficheiro.
	 * 
	 * @param ruta_ficheiro
	 * @return
	 */
	public static File obterFicheiro(String ruta_ficheiro) {
		File file = null;
		try {
			file = new File(ruta_ficheiro);
		}
		catch(Exception e) {
			logger.error("Erro obtendo o ficheiro: "+ruta_ficheiro);
		}
		return file;
	}
	
}
