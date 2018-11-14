package es.brudi.incidencias.servlet;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import es.brudi.incidencias.db.XestorConexions;
import es.brudi.incidencias.documentos.XestionFicheiros;
import es.brudi.incidencias.usuarios.XestionTokens;

/**
 * 
 * Clase Listener cos métodos que inicia o contexto o arrancar o servlet e destrueno o apagarse.
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Decembro - 2017
 * 
 */
@WebListener
public class AppContextListener implements ServletContextListener {
	
	/**
	 * Inicializa o contexto o arrancar o servlet. Inicia o sistema de logs segundo os parámetros do web.xml e establece conexión coa base de datos.
	 */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	
    	ServletContext ctx = servletContextEvent.getServletContext();
    	
    	//Iniciase log4j, o sistema de logs.
    	String log4jConfig = ctx.getInitParameter("log4j-config");
    	if(log4jConfig == null){
    		System.err.println("No log4j-config init param, initializing log4j with BasicConfigurator");
			BasicConfigurator.configure();
    	}else {
			String webAppPath = ctx.getRealPath("/");
			String log4jProp = webAppPath + log4jConfig;
			File log4jConfigFile = new File(log4jProp);
			if (log4jConfigFile.exists()) {
				System.out.println("Initializing log4j with: " + log4jProp);
				DOMConfigurator.configure(log4jProp);
			} else {
				System.err.println(log4jProp + " file not found, initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
		}
    	
    	
    	String pathFacturas = ctx.getInitParameter("pathFacturas");
    	String pathPresupostos = ctx.getInitParameter("pathPresupostos");
    	String pathImaxes = ctx.getInitParameter("pathImaxes");
    	String pathAlbarans = ctx.getInitParameter("pathAlbarans");
    	XestionFicheiros.updateParams(pathFacturas, pathPresupostos, pathImaxes, pathAlbarans);
    	
    	
    	Random random = new Random();
    	StringBuilder sb = new StringBuilder();
		while (sb.length() < 64 ) {
    		sb.append(Integer.toHexString(random.nextInt()));
    	}
    	
		try {
			SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
	    	XestionTokens.updateClave(secretKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
		XestorConexions.iniciarPool();
    }
}