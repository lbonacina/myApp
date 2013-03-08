package myApp.controller;

import myApp.controller.user.NewUser;
import myApp.controller.user.ResetUserPassword;
import myApp.model.user.User;
import myApp.util.AppLog;
import org.slf4j.Logger;

import javax.enterprise.event.Observes;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * User: eluibon
 * Date: 22/08/12
 * Time: 14.56
 * prototype for a component that sends out mails on certain events
 * uses CDI Events http://docs.oracle.com/javaee/6/tutorial/doc/gkhic.html to decouple mail sending from the business code
 */
@Named("mail")
@RequestScoped
public class MailController {

    @Inject @AppLog private Logger log;

    /**
     * @param user the user
     */
    public void onNewUser(@Observes @NewUser final User user) {
        log.debug("MAIL PROTOTYPE : new user notification : {}", user.toString());
        log.debug("MAIL PROTOTYPE : generated user password : {}", user.getDecryptedPassword());
    }

    /**
     * @param user the user
     */
    public void onResetUserPasswordUser(@Observes @ResetUserPassword final User user) {
        log.debug("MAIL PROTOTYPE : user password reset notification : {}", user.toString());
        log.debug("MAIL PROTOTYPE : generated user password : {}", user.getDecryptedPassword());
    }

}
