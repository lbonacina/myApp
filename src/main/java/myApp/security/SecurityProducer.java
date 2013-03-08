package myApp.security;

import myApp.model.user.User;
import myApp.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Edward P. Legaspi
 * @since Oct 10, 2012 Produces an instance of Shiro's subject so that it can be
 *        injected.
 */
@Singleton
public class SecurityProducer {

    public final static String SUBJECT_FULL_NAME_SESSION_KEY = "subjectFullName" ;

    @Inject private UserService userService ;

    private SecurityManager securityManager;

    @PostConstruct
    public void init() {
        final String iniFile = "classpath:shiro.ini";
        //log.info("Initializing Shiro INI SecurityManager using " + iniFile);
        securityManager = new IniSecurityManagerFactory(iniFile).getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Produces
    @Named("securityManager")
    public SecurityManager getSecurityManager() {
        return securityManager;
    }


    /*
        seems like injecting this via CDI, especially in Stateless EJB, generate some kind of problem with sessions
        like Session invalidated and so on.
        we avoid it for now, just use the static SecurityUtils.getSubject() directly
        TODO : investigate (not a priority...)

    @Produces
    @Named("loggedSubject")
    public Subject getLoggedSubject() {
        return SecurityUtils.getSubject();
    }
    */

    /**
        produces the logged User from the principal identification, ie the username
        User is normally a detached object so if you need to associate some other object with the user it's
        probably better to load it in the transaction (session) instead of injecting it
        TODO : maybe it works if we inject it in the parameter of a method ?
     */
    @Produces
    @LoggedUser
    @Named("loggedUser")
    public User getLoggedUser() {
        return userService.findByUsername((String)SecurityUtils.getSubject().getPrincipal());
    }

    /**
        produces a String with the full name of the logged user, stored in the Subject session
        (for webapps it's just the http session)
        this is a simple solution to display the logged user name above the menu or to do some logging while
        avoiding using @LoggedUser that does a query (even though it may be cached)
        (it's just a neat workaround to store something in the HTTP Session)
     */
    @Produces
    @LoggedUserFullName
    @Named("loggedUserFullName")
    public String getLoggedUserFullName() {
        return (String)SecurityUtils.getSubject().getSession().getAttribute(SecurityProducer.SUBJECT_FULL_NAME_SESSION_KEY);
    }
}