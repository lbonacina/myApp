package myApp.security;

import myApp.model.user.User;
import myApp.service.UserService;
import myApp.util.AppLog;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: eluibon
 * Date: 11/12/12
 * Time: 14.13
 * Sample for JPA Realm, currently not used
 */
public class JpaRealm extends AuthorizingRealm {

    private static final String REALM_NAME = "jpaRealm";

    @Inject
    private UserService userService;

    @Inject @AppLog
    private Logger log ;

    public JpaRealm() {

        setName(REALM_NAME); //This name must match the name in the User class's getPrincipals() method
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {

        // just find the User that match the username inside the auth toker if any
        // and verify that the account is not locked or disabled
        // if the account is valid return a SimpleAuthenticationInfo and let Shiro do the password matching
        // (pluggable with CredentialMatcher or something else to implement hashing of passwords)
        // after the login (ie after password match) in AuthenticationController.login we will track the success/failure

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        if ((username == null) || (username.equals(""))) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        log.trace("Do Authentication for : {}", username);
        User user = userService.findByUsername(username);

        if (user == null)
            throw new UnknownAccountException();

        return new CustomAuthenticationInfo(username, user.getPassword(), REALM_NAME, user.isEnabled(), user.isLocked());
        //return new SimpleAuthenticationInfo(username, user.getPassword(), new SimpleByteSource(user.getSalt()), REALM_NAME);
    }




    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = (String) getAvailablePrincipal(principals);
        log.debug("Do Authorization for {}",username);
        return userService.getAuthorizationInfoForUser(username) ;
    }
}
