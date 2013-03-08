package myApp.controller.user;

import myApp.model.user.User;
import myApp.repository.UserConstraintException;
import myApp.security.LoggedUser;
import myApp.service.UserService;
import myApp.util.AppLog;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("account_ctrl")
@RequestScoped
public class AccountController implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = -1L;

    @Inject FacesContext facesContext;
    @Inject @AppLog private Logger log;
    @Inject private UserService userService;
    @Inject @LoggedUser private User user;

    private String password ;
    private String passwordConfirm;

// -------------------------- OTHER METHODS --------------------------

    @PostConstruct
    public void init() {

    }

    public void save() {

        // TODO : here we can see if password match some security criteria like length and composition
        // for sake of simplicity assume that a first login is when password is 12345678

        if ( user.isForcePasswordChangeOnNextLogin() ) {
            // need to check if password not empty
            if ( ( password == null) || ( password.equals("") ) || ( password.equals("12345678")) ) {
                facesContext.validationFailed();
                // force ajax update if there is a validation error
                Messages.addError("users_form:password", "must change password");
                return ;
            }
        }

        try {
            user.assignPassword(password);
            userService.save(user);
        }
        catch (UserConstraintException uce) {
            // probably not the best implementation
            for (UserConstraintException.Violations v : uce.getConstraintsViolations()) {
                switch (v) {
                    case USERNAME_NOT_UNIQUE:
                        Messages.addError("users_form:username", "username is not unique");
                        break;
                    case EMAIL_NOT_UNIQUE:
                        Messages.addError("users_form:email", "email is not unique");
                        break;
                    case UNKNOWN_ERROR: // unknown exceptions are reported to user
                        Messages.addGlobalError("unknown error, please contact system administrator");
                        break;
                }
            }
            facesContext.validationFailed();
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
