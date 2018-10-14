package es.brudi.incidencias.rest.util;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import es.brudi.incidencias.db.DBConnectionManager;
import es.brudi.incidencias.error.Error;

@Checkdb
@Provider
public class CheckdbFilter implements ContainerRequestFilter {

	private Logger logger = Logger.getLogger(CheckdbFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
        if(DBConnectionManager.getConnection() == null ) {
        	logger.error("Non existe conexión coa base de datos.");
        	requestContext.abortWith(Response.ok(Error.DATABASE.toJSONError()).build());
        }
	}

}
