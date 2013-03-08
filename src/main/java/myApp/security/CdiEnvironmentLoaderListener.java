package myApp.security;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.mgt.AuthenticatingSecurityManager;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * User: eluibon
 * Date: 11/12/12
 * Time: 19.29
 * CDI Listener to inject a JPA Realm into Shiro for authentication via JPA instead of JDBC
 * also inject the CacheManager into the SecurityManager
 * (EmbeddedCacheManager is an infinispan cache manager defined on standalone.xml and accessed with JNDI)
 *
 * for example define:
 *
 * <cache-container name="shiro-cache" default-cache="daily-cache" start="EAGER">
 *      <local-cache name="daily-cache">
 *          <eviction strategy="LRU" max-entries="1000"/>
 *          <expiration lifespan="86400000"/>
*       </local-cache>
 * </cache-container>
 *
 * under urn:jboss:domain:infinispan:1.2 subsystem
 *
 * basically it replaces the whole [main] part of shiro.ini
 */
public class CdiEnvironmentLoaderListener extends EnvironmentLoaderListener {

    @Inject JpaRealm jpaRealm;

    // cache manager for infinispan, producer is in myApp.util.ResourcesProducer
    @Inject EmbeddedCacheManager defaultCacheManager;

    @Override
    protected WebEnvironment createEnvironment(ServletContext sc) {

        WebEnvironment environment = super.createEnvironment(sc);

        AuthenticatingSecurityManager asm = (AuthenticatingSecurityManager) environment.getSecurityManager();

        // set my custom authenticator
        asm.setAuthenticator(new CustomAuthenticator());

        // set my custom InfinispanCacheManager into the SecurityManager
        asm.setCacheManager(new InfinispanCacheManager(defaultCacheManager));

        //SimpleCredentialsMatcher credentialsMatcher = new SimpleCredentialsMatcher() ;

        //HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher() ;
        //credentialsMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
        //credentialsMatcher.setHashIterations(1024);
        //credentialsMatcher.setStoredCredentialsHexEncoded(false);

        jpaRealm.setCredentialsMatcher(new PasswordMatcher());

        asm.setRealm(jpaRealm);
        ((DefaultWebEnvironment) environment).setSecurityManager(asm);

        return environment;
    }
}
