package myApp.controller;

import myApp.controller.user.NewUser;
import myApp.controller.user.ResetUserPassword;
import myApp.model.accesslog.AccessLogEntry;
import myApp.model.user.Role;
import myApp.model.user.User;
import myApp.repository.UserConstraintException;
import myApp.service.AccessLogEntryService;
import myApp.service.UserService;
import myApp.util.AppLog;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.hibernate.validator.constraints.NotEmpty;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Named("logs_ctrl")
@RequestScoped
public class LogsController implements Serializable {

    private static final long serialVersionUID = -822414527884637683L;

// ------------------------------ FIELDS ------------------------------

    @Inject @AppLog private Logger log;
    @Inject private AccessLogEntryService accessLogEntryService ;

    List<AccessLogEntry> accessLogEntryList ;

    public List<AccessLogEntry> getAccessLogEntryList() {
        return accessLogEntryList;
    }

    public void setAccessLogEntryList(List<AccessLogEntry> accessLogEntryList) {
        this.accessLogEntryList = accessLogEntryList;
    }

    @PostConstruct
    public void init() {
        accessLogEntryList = accessLogEntryService.findAll() ;
    }
}
