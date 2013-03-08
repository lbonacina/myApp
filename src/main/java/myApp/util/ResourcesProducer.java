package myApp.util;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ResourceBundle;

/**
    producer for some common resources like the EntityManages, the logs, the resource bundle and the Infinispan Cache
    (see below for comments)
 */
public class ResourcesProducer {

    // use @SuppressWarnings to tell IDE to ignore warnings about field not being referenced directly
    @SuppressWarnings("unused")
    @Produces
    @PersistenceContext
    private EntityManager em;

    @Produces
    @AppLog
    public Logger produceLog(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    // cannot @Produces since Resource bundle is not serializable and thus not injectable in a
    // passivating bean (like view or session)
    public static ResourceBundle getResourceBundle() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getResourceBundle(context, "msg");
    }

    /*
        producer for the Infinispan cache manager used by Shiro to cache Authorizations (could also be used to cache
        authentication if necessary).
        normally in a production environement it will be defined on JBoss (standalone.xml) and accessed via JNDI

        <cache-container name="shiro" default-cache="jpaRealm.authorizationCache" start="EAGER">
            <local-cache name="jpaRealm.authorizationCache">
                <eviction strategy="LRU" max-entries="1000"/>
                <expiration lifespan="86400000"/>
            </local-cache>
        </cache-container>

        @Resource(lookup = "java:jboss/infinispan/container/shiro")
        @Produces
        EmbeddedCacheManager defaultCacheManager;

        NB : there is a problem with the @Resource annotation since the one shipped with JDK 6 does
        not have the property "lookup" while the one shipped with JEE6 (ie Jboss, also with JDK 7)
        does have it. to bypass the problem at compilation time use the java variable
        -Djava.endorsed.dirs=<jboss_home>/modules/javax/annotation/api/main while compiling (ie with maven runner)
        dunno if the is a better way to do this, also IDEA still complain about the missing property.
        see also CdiEnvironmentLoaderListener.java
     */
    @Produces
    public EmbeddedCacheManager getCacheManager() {

        Configuration config = new ConfigurationBuilder()
                .eviction()
                .maxEntries(1000).strategy(EvictionStrategy.LRU).expiration()
                .lifespan(86400000)
                .build();

        return new DefaultCacheManager(config);
    }
}
