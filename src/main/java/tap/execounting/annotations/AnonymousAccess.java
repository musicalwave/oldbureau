package tap.execounting.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Security annotation for Tapestry Pages.
 * Enables anonymous access to pages, so the user does not
 * have to be logged in.
 * 
 * @author karesti
 * @version 1.0
 */
@Target(
{ TYPE })
@Retention(RUNTIME)
@Documented
public @interface AnonymousAccess { }