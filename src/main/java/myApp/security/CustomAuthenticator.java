package myApp.security;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 07/02/13
 * Time: 10.05
 * a simple extension of ModularRealmAuthenticator to check if the account is either locked or disabled
 * AFTER the credential matching.
 * otherwise you could test the locked/disabled in JPARealm but this will return locked/disabled errors
 * if the user gets the username right but the password is wrong since it's done BEFORE credential matching
 * (really don't know which scenario is better, from a security POV every failed attempt should return a simple
 *  "no account")
 */
public class CustomAuthenticator extends ModularRealmAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticator.class);

    @Override
    protected AuthenticationInfo doSingleRealmAuthentication(Realm realm, AuthenticationToken token) {

        AuthenticationInfo info = super.doSingleRealmAuthentication(realm, token);

        // need to check for disabled or locked account
        CustomAuthenticationInfo customInfo = (CustomAuthenticationInfo)info ;

        if ( customInfo.isLocked() ) {
            log.trace("Account {} is locked.", token);
            throw new LockedAccountException();
        }

        if ( ! customInfo.isEnabled() ) {
            log.trace("Account {} is disabled.", token);
            throw new DisabledAccountException();
        }

        return info ;
    }
}
