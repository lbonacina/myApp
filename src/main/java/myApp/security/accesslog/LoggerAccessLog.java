package myApp.security.accesslog;

import myApp.model.accesslog.AccessLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 19/02/13
 * Time: 17.06
 * an implementation of the AccessLog interface that log out messages to a SLF4J Logger
 * here is a sample configuration for logback
 *
 * <appender name="ACCESS_LOG" class="ch.qos.logback.core.FileAppender">
 *      <file>${LOG_ROOT_DIR}/access.log</file>
 *      <append>true</append>
 *      <encoder>
 *          <pattern>%msg%n</pattern>
 *      </encoder>
 * </appender>
 *
 * access log, additivity="false" means logging is not propagated to appender up the hierarchy, ie LOG
 *
 * <logger name="myApp.security.accesslog.LoggerAccessLog" additivity="false" level="INFO">
 *  <appender-ref ref="ACCESS_LOG" />
 * </logger>
 */
public class LoggerAccessLog extends DefaultAccessLog {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final static String msgFormat = "event={0}, timestamp={1}, result={2}, reason={3}, username={4}, fullUserName={5}" ;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH24:MM:ss") ;

    @Override
    public void trackSuccessfulLogin(String username, String fullUserName) {

        log.info(MessageFormat.format(msgFormat,"Login",sdf.format(Calendar.getInstance().getTime()),"Success","",username,fullUserName));

    }

    @Override
    public void trackFailedLogin(String username, AccessLogEntry.Reason reason) {

        log.info(MessageFormat.format(msgFormat,"Login",sdf.format(Calendar.getInstance().getTime()),"Failure",reason,username,""));
    }

    @Override
    public void trackSuccessfulLogout(String username, String fullUserName) {

        log.info(MessageFormat.format(msgFormat,"Logout",sdf.format(Calendar.getInstance().getTime()),"Success","",username,fullUserName));
    }

    @Override
    public void trackSessionExpiration(String username, String fullUserName) {

        // TODO : track session expiration
    }
}
