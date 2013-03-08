package myApp.security.accesslog;

import myApp.model.accesslog.AccessLogEntry;
import myApp.service.AccessLogEntryService;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 19/02/13
 * Time: 17.06
 */
public class DatabaseAccessLog extends DefaultAccessLog {

    private AccessLogEntryService accessLogEntryService ;

    public DatabaseAccessLog(AccessLogEntryService accessLogEntryService) {
        this.accessLogEntryService = accessLogEntryService;
    }

    @Override
    public void trackSuccessfulLogin(String username, String fullUserName) {

        accessLogEntryService.trackSuccessfulLogin(username, fullUserName);
    }

    @Override
    public void trackFailedLogin(String username, AccessLogEntry.Reason reason) {

        accessLogEntryService.trackFailedLogin(username, reason);
    }

    @Override
    public void trackSuccessfulLogout(String username, String fullUserName) {

        accessLogEntryService.trackSuccessfulLogout(username, fullUserName);
    }

    @Override
    public void trackSessionExpiration(String username, String fullUserName) {

        // TODO : track session expiration
    }
}
