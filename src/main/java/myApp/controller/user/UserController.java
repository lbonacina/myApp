package myApp.controller.user;

import myApp.model.user.Role;
import myApp.model.user.User;
import myApp.repository.UserConstraintException;
import myApp.service.UserService;
import myApp.util.AppLog;
import myApp.util.ResourcesProducer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.hibernate.validator.constraints.NotEmpty;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named("user_ctrl")
@ViewAccessScoped
public class UserController implements Serializable {

    private static final long serialVersionUID = -1L;

// ------------------------------ FIELDS ------------------------------

    @Inject @AppLog private Logger log;
    @Inject private UserService userService;

    @Inject FacesContext facesContext;

    List<User> usersList;
    List<Role> userRoles;
    List<String> userRolesAsString;

    private User selectedUser = null;

    private boolean newUserFlag = false;

    @Inject private Event<User> userEventSrc;
    @Inject @NewUser private Event<User> newUserEventSrc;
    @Inject @ResetUserPassword private Event<User> resetUserPasswordEventSrc;

    private DualListModel<String> rolesPickList = new DualListModel<String>() ;


// -------------------------- OTHER METHODS --------------------------

    @PostConstruct
    public void init() {

        log.debug("PersonalController::init");

        usersList = userService.findAll();
        userRoles = userService.findAllRoles() ;
        userRolesAsString = new ArrayList<String>() ;
        for ( Role r : userRoles ) {
            userRolesAsString.add(r.getRole()) ;
        }
    }

    public void onUserListChanged(@Observes final User user) {

        log.debug("refresh users list");
        // find all uses the information of the logged user and his permissions to decide
        // if we need to filter something out
        // for example if we add a Country to a User and a user can see only other users from the same Country
        // the constraint should be implemented inside the Service so the client remains the same.
        // this is especially useful if we have some other client, for example a REST WS backing a JS frontend
        usersList = userService.findAll();
    }

    public void selectionListener() {

        log.debug("selected user : {}", selectedUser);
        newUserFlag = false ;
        // extract roles from user

        List<String> list = new ArrayList<String>(selectedUser.getRoleNames()) ;
        rolesPickList.setSource(new ArrayList<String>(CollectionUtils.subtract(userRolesAsString, list)));
        rolesPickList.setTarget(list);
    }

    public void create() {

        log.debug("create new user");
        newUserFlag = true;
        selectedUser = new User();
        log.debug("new user : {}", selectedUser);
        rolesPickList.setSource(userRolesAsString);
        rolesPickList.setTarget(new ArrayList<String>());
    }

    public void delete() {
        // TODO : implement
    }

    public void resetPasswordAndUnlock() {

        selectedUser.resetPassword();
        selectedUser.unlock();
        try {
            userService.save(selectedUser);
        }
        catch (UserConstraintException e) {
            Messages.addGlobalError("unknown error, please contact system administrator");
            return;
        }

        resetUserPasswordEventSrc.fire(selectedUser);
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

        log.debug("save user : {}", selectedUser);

        log.debug("with roles : {}", rolesPickList.getTarget());
        if ( this.newUserFlag )
            log.debug("saving a new user");
        else
            log.debug("updating an existing user");

        if ( rolesPickList.getTarget().size() == 0 ) {
            Messages.addError("users_form:roles", "at_least_one_role");
            facesContext.validationFailed();
            RequestContext.getCurrentInstance().update("user_details");
            return;
        }

        List<Role> rolesList = new ArrayList<Role>() ;
        for ( String s : rolesPickList.getTarget() )
            for ( Role r : userRoles )
                if ( r.getRole().equals(s) )
                    rolesList.add(r) ;

        selectedUser.assignRoles(rolesList);

        try {
            userService.save(selectedUser);

            if (newUserFlag)
                newUserEventSrc.fire(selectedUser);
            else
                userEventSrc.fire(selectedUser);

            newUserFlag = false;
        }
        catch (UserConstraintException uce) {

            // probably not the best implementation
            for (UserConstraintException.Violations v : uce.getConstraintsViolations()) {
                switch (v) {

                    case USERNAME_NOT_UNIQUE:
                        Messages.addError("users_form:username", "username_unique");
                        break;
                    case EMAIL_NOT_UNIQUE:
                        Messages.addError("users_form:email", "email_unique");
                        break;
                    case UNKNOWN_ERROR: // unknown exceptions are reported to user
                        Messages.addGlobalError("unexpectedError");
                        break;

                }
            }
            facesContext.validationFailed();
            // force ajax update if there is a validation error
            RequestContext.getCurrentInstance().update("user_details");
        }
    }


    public String getFieldsetHeader() {

        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msg");

        if (selectedUser == null )
            return resourceBundle.getString("user_pages_welcome_fieldset_header") ;
        else
            return ( newUserFlag) ?
                    resourceBundle.getString("user_pages_new_fieldset_header") :
                    resourceBundle.getString("user_pages_edit_fieldset_header") ;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    @Produces
    @Named
    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public boolean isNewUserFlag() {
        return newUserFlag;
    }

    public void setNewUserFlag(boolean newUserFlag) {
        this.newUserFlag = newUserFlag;
    }

    public DualListModel<String> getRolesPickList() {
        return rolesPickList;
    }

    public void setRolesPickList(DualListModel<String> rolesPickList) {
        this.rolesPickList = rolesPickList;
    }
}
