package myApp.controller.user;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: eluibon
 * Date: 22/08/12
 * Time: 15.11
 * qualifier per identificare l'evente di creazione di un nuovo utente che genera sia un refresh
 * della lista degli utenti nella pagina di amministrazione che l'invio di una mail
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface NewUser {
}