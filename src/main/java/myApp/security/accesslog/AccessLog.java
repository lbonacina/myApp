package myApp.security.accesslog;

import myApp.model.accesslog.AccessLogEntry;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 20/02/13
 * Time: 13.33
 */
public interface AccessLog {

    void trackSuccessfulLogin(String username, String fullUserName);

    void trackFailedLogin(String username, AccessLogEntry.Reason reason);

    void trackSuccessfulLogout(String username, String fullUserName);

    void trackSessionExpiration(String username, String fullUserName);
}
