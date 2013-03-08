package myApp.security;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.util.ByteSource;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 07/02/13
 * Time: 8.59
 * simple extension to return locked and enabled infos
 */
public class CustomAuthenticationInfo extends SimpleAuthenticationInfo {

    private boolean enabled ;
    private boolean locked ;

    public CustomAuthenticationInfo(Object principal, Object credentials, String realmName, boolean enabled, boolean locked) {
        super(principal, credentials, realmName);
        this.enabled = enabled;
        this.locked = locked;
    }

    public CustomAuthenticationInfo(Object principal, Object hashedCredentials, ByteSource credentialsSalt, String realmName, boolean enabled, boolean locked) {
        super(principal, hashedCredentials, credentialsSalt, realmName);
        this.enabled = enabled;
        this.locked = locked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }


}
