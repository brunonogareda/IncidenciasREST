package es.brudi.incidencias.rest.util;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

/**
 *  
 * @author Bruno Nogareda Da Cruz <brunonogareda@gmail.com>
 * @version 0.1
 * @year Marzo - 2018
 *
 */
@NameBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Secured {
}