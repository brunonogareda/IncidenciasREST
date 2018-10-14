package es.brudi.incidencias.usuarios;

import java.util.Calendar;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

/**
 * 
 * Clase para firmar e verificar Tokens
 * 
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Marzo - 2018
 * 
 */	
public class XestionTokens {

	private static final String DEFAULT_ADMIN_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhZG9yIiwiaWF0IjoxNTI1NjI3OTUyLCJleHAiOjE1MjgyMTk5NTJ9.wVBB_AjYdGVsBO46tA4Pvxb9mEv7L9NG9WFFCtxBUItb26PtjVqJc31-uf-3bDlqa2iC5-xFgbP2DQi_p7jUSw";	
	private static SecretKey clave;
	//Tempo que tarde en caducar un token dende que se expide (en minutos)
	private static final int MIN_CADUCIDADE = 43200; //30 Dias
	private static Logger logger = Logger.getLogger(XestionTokens.class);
	
	private XestionTokens() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Actualiza o valor da clave para firmar o token.
	 * Só se debería executar cando se inicia o servlet
	 * @param clave
	 */
	public static void updateClave(SecretKey clave) {
		XestionTokens.clave = clave;
	}
	
	/**
	 * Crea e firma un token engadindo o parámetro no subject. 
	 * @param login
	 * @return
	 */
    public static String xerarFirmarToken(String subject) {
        String jwtToken = "";
        
        Calendar now = Calendar.getInstance();
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.MINUTE, MIN_CADUCIDADE);
        
		jwtToken = Jwts.builder()
				  .setSubject(subject)
				  .setIssuedAt(now.getTime())
		          .setExpiration(exp.getTime())
		          .signWith(SignatureAlgorithm.HS512, clave)
				  .compact();
		
		return jwtToken;
    }
	
    /**
     * Verifica que un token esté firmado correctamente
     * @param token
     * @return Valor que de subject do token.
     */
    public static String verificarFirma(String token) {
    	String username = null;
    	
    	//Token por defecto para pruebas.
    	if(token.equals(DEFAULT_ADMIN_TOKEN))
    		return "administrador";
    	
        try {
            // Validate the token
        	logger.info("Validando token");
            Claims claims = Jwts.parser().setSigningKey(clave).parseClaimsJws(token).getBody();
            username = claims.getSubject();
            return username;
 
        } catch (SignatureException e) {
        	logger.info("Erro en token: ", e);
        } catch (Exception e) {
        	logger.error("Error verificando a signatura: ", e);
        }
    	return null;
    }
	
}
