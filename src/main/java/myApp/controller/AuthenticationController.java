package myApp.controller;

import myApp.model.accesslog.AccessLogEntry;
import myApp.model.user.User;
import myApp.security.SecurityProducer;
import myApp.security.accesslog.AccessLog;
import myApp.security.accesslog.AppAccessLog;
import myApp.service.UserService;
import myApp.util.AppLog;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.omnifaces.util.Messages;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;


@Named("auth_ctrl")
@RequestScoped
public class AuthenticationController implements Serializable {
// ------------------------------ FIELDS ------------------------------

    static final long serialVersionUID = 8277824266030751108L;

    @Inject FacesContext facesContext;

    @Inject @AppLog Logger log;
    @Inject @AppAccessLog AccessLog accessLog;

    String username;
    String password;

    @Inject UserService userService;

// -------------------------- OTHER METHODS --------------------------

    private Subject subject = SecurityUtils.getSubject();

    /**
     * get username & password from Shiro PassThruAuthenticationFilter and authenticate the user
     * also set in session the full name of the subject to display it (subject is normally the username)
     *
     * @return navigation
     */
    public String login() {

        log.debug("trying to login with {}/{}", username, password);

        boolean isAuth = subject.isAuthenticated();
        User user = null;

        AccessLogEntry.Reason reason = AccessLogEntry.Reason.NONE ;

        if (!isAuth) {

            log.debug("{} is not authenticated", username);
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);

            try {
                subject.login(token);
                user = userService.trackSuccessfulLoginAttempt(username);
                subject.getSession().setAttribute(SecurityProducer.SUBJECT_FULL_NAME_SESSION_KEY, user.getFullName());
                isAuth = true;
            }
            catch (UnknownAccountException uae) {
                log.debug("Username not found");
                reason = AccessLogEntry.Reason.UNKNOWN_USERNAME;
            }
            catch (IncorrectCredentialsException ice) {
                // password is wrong but username is right, need to track on DB the number of tries
                int count = userService.trackFailedLoginAttempt(username);
                log.debug("Password is wrong. {} tries left", count);
                reason = AccessLogEntry.Reason.WRONG_PASSWORD;
            }
            catch (LockedAccountException lae) {
                log.debug("Account is locked");
                reason = AccessLogEntry.Reason.ACCOUNT_LOCKED;
            }
            catch (DisabledAccountException dae) {
                log.debug("Account is disabled");
                reason = AccessLogEntry.Reason.ACCOUNT_DISABLED;
            }
            catch (AuthenticationException ae) {
                log.debug("Error during login", ae);
                reason = AccessLogEntry.Reason.GENERIC_EXCEPTION;
            }
            catch (Exception e) {
                log.error("Unexpected Exception while logging in user.", e);
                Messages.addGlobalError("unexpectedError", e.getMessage());
                reason = AccessLogEntry.Reason.UNCATCHED_EXCEPTION;
            }
        }
        else {

            log.debug("{} already authenticated.", username);
            user = userService.findByUsername(username); // really needed apart from satisfying the assert below ?
        }

        if (isAuth) {

            accessLog.trackSuccessfulLogin(username, user.getFullName());

            assert user != null;

            if (user.isForcePasswordChangeOnNextLogin()) {
                log.debug("Successful login but need to force password change, redirecting to personal page.");
                return "pages/user/account?faces-redirect=true";
            } else {
                log.debug("Redirecting to main page.");
                return "pages/main?faces-redirect=true";
            }
        }
        else {

            Messages.addGlobalError("genericLoginError");
            accessLog.trackFailedLogin(username, reason);
            return null;
        }
    }

    /**
     * logout & redirect
     *
     * @return navigation
     */
    public String logout() {

        String principal = (String) subject.getPrincipal();
        log.info("Logging out user : {}", principal);
        // get logged user full name
        String userFullName = (String) subject.getSession().getAttribute(SecurityProducer.SUBJECT_FULL_NAME_SESSION_KEY);
        // do logout from subject
        subject.logout();
        // track event
        accessLog.trackSuccessfulLogout(principal, userFullName);
        // redirect to login page
        return "/login?faces-redirect=true";
    }


// --------------------- GETTER / SETTER METHODS ---------------------

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
