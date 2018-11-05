package es.brudi.incidencias.rest.util;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

import es.brudi.incidencias.usuarios.Usuario;
import es.brudi.incidencias.usuarios.XestionTokens;
import es.brudi.incidencias.usuarios.db.UsuarioAccessor;

/**
 * 
 * Clase que implementa ContainerRequestFilter
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Marzo - 2018
 *
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecuredFilter implements ContainerRequestFilter {

    public static final String AUTHENTICATION_SCHEME = "Bearer";
	
	// Get Log4j Logger
	private Logger logger = Logger.getLogger(SecuredFilter.class);

	/**
	 * Filtro para executar en unha petición para extraer o token da cabeceira,
	 * comprobar a firma e obter o usuario correspondente.
	 * 
	 */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader
                = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);


        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHENTICATION_SCHEME+" ")) {
        	logger.warn("Petición sen Token.");
        	requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        	return;
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        
        logger.debug("Obtemos o token e valídase.");
        
        // Validate the token
      	String subject = XestionTokens.verificarFirma(token);
       	if(subject == null) {
       		requestContext.abortWith(
                     Response.status(Response.Status.UNAUTHORIZED).build());
       	}
       	       	
       	//Modificamos o security context para que devolva o usuario que realizou a petición.
        SecurityContext originalContext = requestContext.getSecurityContext();
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
       	Authorizer authorizer = new Authorizer(roles, subject, originalContext.isSecure());
       	
       	if(((User)authorizer.getUserPrincipal()).obterUsuario() == null) {
       		logger.error("O token é correcto, pero non se obtiveron correctamente os datos do usuario.");
       		requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
       	}
       	requestContext.setSecurityContext(authorizer);
    }
    
    public static class Authorizer implements SecurityContext {

        Set<String> roles;
        String username;
        Usuario user;
        boolean isSecure;
        
        public Authorizer(Set<String> roles, final String username, boolean isSecure) {
            this.roles = roles;
            this.username = username;
            this.user = UsuarioAccessor.obterUsuario(username);
            this.isSecure = isSecure;
        }

        @Override
        public Principal getUserPrincipal() {
            return new User(user);
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            return isSecure;
        }

        @Override
        public String getAuthenticationScheme() {
            return AUTHENTICATION_SCHEME;
        }
        
    } 

    public static class User implements Principal {
        Usuario usuario;

        public User(Usuario user) {
            this.usuario = user;
        }

        @Override
        public String getName() {
        	return usuario.getNome();
        }
        
        public Usuario obterUsuario() {
        	return usuario;
//        	return UsuarioAccessor.obterUsuario(name);
        }
    }

}