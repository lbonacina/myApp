package myApp.security;

import org.apache.shiro.SecurityUtils;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 25/01/13
 * Time: 12.41
 */
public final class ELSecutiryFunctions {

    private ELSecutiryFunctions() {}

    public static boolean hasPermission(String permission) {
        return  SecurityUtils.getSubject().isPermitted(permission);
    }
}
