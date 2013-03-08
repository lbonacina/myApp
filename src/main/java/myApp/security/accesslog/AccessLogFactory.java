package myApp.security.accesslog;

import myApp.service.AccessLogEntryService;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 19/02/13
 * Time: 17.22
 */
public class AccessLogFactory {

    @Inject private AccessLogEntryService accessLogEntryService ;

    @Produces
    @AppAccessLog
    public AccessLog getAccessLog() {
        return new DatabaseAccessLog(accessLogEntryService) ;
    }
}
