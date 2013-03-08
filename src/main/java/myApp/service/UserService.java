package myApp.service;

import com.mysema.query.BooleanBuilder;
import myApp.model.user.QUser;
import myApp.model.user.Role;
import myApp.model.user.User;
import myApp.repository.RoleRepository;
import myApp.repository.UserConstraintException;
import myApp.repository.UserRepository;
import myApp.util.AppLog;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: eluibon
 * Date: 11/12/12
 * Time: 14.06
 */
@Stateless
public class UserService implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 2332677310929733841L;

    @Inject @AppLog
    private Logger log ;

    @Inject UserRepository userRepository ;
    @Inject RoleRepository roleRepository ;

// -------------------------- OTHER METHODS --------------------------

    public List<User> findAll() {

        Subject loggedSubject = SecurityUtils.getSubject();
        /*
            extremely efficient use of querydsl generated metamodel classes and Spring Data JPA QueryDslPredicateExecutor
            to pass some predicates (ie a where condition) to the findAll method based on the permissions of the logged user (subject)
            it's also a wonderful alternative to the godawful JPA2 Criteria Queries, don't care if need to depend on querydsl :)
        */
        // generated metamodel class
        QUser user = QUser.user ;
        BooleanBuilder bb = new BooleanBuilder() ;

        // if we decide to allow for logical deletion of users or any other kind of filter, for example on organizations
        // or country they can be added here really quickly
        //if ( ! loggedSubject.isPermitted("user:list_deleted"))
        //    bb.and(user.deleted.isFalse()) ;
        // if not permitted to list superadmin users add superadmin = false
        if ( ! loggedSubject.isPermitted("user:list_superadmin") )
            bb.and(user.superadmin.isFalse()) ;

        return (List<User>)userRepository.findAll(bb.getValue()) ;
    }

    public List<Role> findAllRoles() {

        return roleRepository.findAll() ;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username) ;
    }

    public AuthorizationInfo getAuthorizationInfoForUser(String username) {

        User user = userRepository.findByUsername(username) ;
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(user.getRoleNames());
        info.setStringPermissions(user.getPermissionNames());
        log.debug("Found Roles : [{}], Permissions : [{}]",user.getRoleNames(),user.getPermissionNames());
        return info ;
    }

    public User trackSuccessfulLoginAttempt(String username) {

        User user = userRepository.findByUsername(username) ;
        user.trackSuccessfulLoginAttempt();
        log.debug("Successful login attempt by username : {}");
        return user ;
    }

    public int trackFailedLoginAttempt(String username) {

        User user = userRepository.findByUsername(username) ;
        int count = user.trackFailedLoginAttempt();
        log.debug("Failed login attempt by username : {}, current attempt count : {}", username, user.getFailedLoginAttemptCount());
        return count ;
    }

    public void save(User user) throws UserConstraintException {

        Set<UserConstraintException.Violations> cv = new HashSet<UserConstraintException.Violations>() ;

        if ( ! checkUsernameUniqueness(user) )
            cv.add(UserConstraintException.Violations.USERNAME_NOT_UNIQUE) ;

        if ( ! checkEmailUniqueness(user) )
            cv.add(UserConstraintException.Violations.EMAIL_NOT_UNIQUE) ;

        if (cv.size() > 0)
            throw new UserConstraintException(cv) ;

        try {
            userRepository.save(user);
        }
        catch (RuntimeException rte) {

            cv.add(UserConstraintException.Violations.UNKNOWN_ERROR) ;
            throw new UserConstraintException(cv) ;
        }
    }

    private boolean checkUsernameUniqueness(User user) {

        User u = userRepository.findByUsername(user.getUsername()) ;
        return  ( u == null ) || u.getId().equals(user.getId()) ;
    }

    private boolean checkEmailUniqueness(User user) {

        User u = userRepository.findByEmail(user.getEmail()) ;
        return  ( u == null ) || u.getId().equals(user.getId()) ;
    }

    public void delete(User user) {

        userRepository.delete(user);
    }
}
