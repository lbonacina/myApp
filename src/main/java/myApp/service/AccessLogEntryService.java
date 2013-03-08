package myApp.service;

import myApp.model.accesslog.AccessLogEntry;
import myApp.repository.AccessLogEntryRepository;
import myApp.util.AppLog;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * User: eluibon
 * Date: 11/12/12
 * Time: 14.06
 */
@Stateless
public class AccessLogEntryService implements Serializable {

// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = -5301115131054281752L;


    @Inject @AppLog private Logger log ;
    @Inject AccessLogEntryRepository accessLogEntryRepository;


    public void trackSuccessfulLogin(String username, String fullUserName) {

        accessLogEntryRepository.save(AccessLogEntry.forSuccessfulLogin(username, fullUserName)) ;
    }

    public void trackFailedLogin(String username, AccessLogEntry.Reason reason) {

        accessLogEntryRepository.save(AccessLogEntry.forFailedLogin(username,reason)) ;
    }

    public void trackSuccessfulLogout(String username, String fullUserName) {

        accessLogEntryRepository.save(AccessLogEntry.forSuccessfulLogout(username, fullUserName)) ;
    }

    public void trackSessionExpiration(String username, String fullUserName) {

        accessLogEntryRepository.save(AccessLogEntry.forSuccessfulLogin(username, fullUserName)) ;
    }

    public List<AccessLogEntry> findAll() {

        return accessLogEntryRepository.findAll() ;
    }
}
