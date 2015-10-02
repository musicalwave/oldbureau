package tap.execounting.annotations;

import tap.execounting.security.AccessLevel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Security annotation, to restrict access, for different types of users
 * User: t00
 * Date: 6/5/13
 * Time: 11:33 PM
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface Access {
    AccessLevel level();
}
