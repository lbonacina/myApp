package myApp.security.accesslog;

import myApp.model.accesslog.AccessLogEntry;
import myApp.security.accesslog.AccessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 19/02/13
 * Time: 17.06
 * Default empty implementation of AccessLog
 */
public class DefaultAccessLog implements AccessLog, Serializable {

    private static final long serialVersionUID = -3708044878145913754L;

    @Override
    public void trackSuccessfulLogin(String username, String fullUserName) {
    }

    @Override
    public void trackFailedLogin(String username, AccessLogEntry.Reason reason) {
    }

    @Override
    public void trackSuccessfulLogout(String username, String fullUserName) {
    }

    @Override
    public void trackSessionExpiration(String username, String fullUserName) {
    }
}
