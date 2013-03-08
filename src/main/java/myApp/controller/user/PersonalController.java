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

@Named("personal_ctrl")
@RequestScoped
public class PersonalController implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = -1L;

    @Inject FacesContext facesContext;
    @Inject @AppLog private Logger log;
    @Inject private UserService userService;
    @Inject @LoggedUser private User user;

// -------------------------- OTHER METHODS --------------------------

    @PostConstruct
    public void init() {

    }

    /**
     *  on saving and constraint checking
     *  before a new user can be saved a number of checks needs to be made, there are two tipes of check
     *
     *      - checks that do not require a trip to the DB, ie Bean checks, for example if a field is not null
     *      - checks that require some kind of query, ie username is unique
     *
     *  for the first kind of checks there is the JSR 303 Bean Validation spec and his Reference Implementation
     *  which is Hibernate Validator, the second kind of checks are outside of the scope of JSR 303 and are implemented
     *  by the application
     *  please note that by default JSF implements JSR 303 as validation framework, se every field annotated with @NotNull
     *  or other constraint is validated on submission.
     *  every constraint is also validated on flush of the entity by Hibernate itself.
     *  checks that requires a trip to the DB are checked only on submission, before we save so there is a sort of 3-step validation
     *
     *      - first JSF checks all JSR 303 constraints on validation phase
     *      - then Hibernate checks again all these constraints on save
     *        (all shall pass if we don't manipulate field server side after JSF validation)
     *      - in between we can implement our custom checks
     *
     *  this result in errors being displayed in two phase, first all that belongs to JSF/JSR 303 validation, then all custom errors
     *  like username not unique
     *
     *  also please not that this forces us to deal with different messages bundles for message customization and i18n
     *  (more on this later...)
     */
    public void save() {

        try {
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
}
