package es.brudi.incidencias.servlet;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import es.brudi.incidencias.db.DBConnectionManager;

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
    	
    	//Iniciase a conexión coa base de datos según os parámetros do web.xml
    	@SuppressWarnings("unused")
		DBConnectionManager connectionManager;
   	   	String dbURL = ctx.getInitParameter("dbURL");
	   	String dbName = ctx.getInitParameter("dbName");
	   	String user = ctx.getInitParameter("dbUser");
	   	String pwd = ctx.getInitParameter("dbPassword");
	    
	   	if(dbURL==null || dbURL.equals("") || dbName==null || dbName.equals("") || user==null || user.equals("") || pwd==null || pwd.equals("")) {
	    	connectionManager = new DBConnectionManager();
	   	}
	   	else {
	   		connectionManager = new DBConnectionManager(dbURL, dbName, user, pwd);
	   	}

    	ctx.setAttribute("DBConnection", DBConnectionManager.getConnection());
    }

    /**
     * Destrue o contexto, pechando a conexión coa base de datos.
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    	Connection con = (Connection) servletContextEvent.getServletContext().getAttribute("DBConnection");
    	try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
}