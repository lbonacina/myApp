package myApp.security;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Created with IntelliJ IDEA.
 * User: Luigi
 * Date: 01/02/13
 * Time: 20.51
 * Implementation of Shiro AbstractCacheManager that returns an InfinispanCache
 */
public class InfinispanCacheManager extends AbstractCacheManager {

    EmbeddedCacheManager defaultCacheManager;

    public InfinispanCacheManager(EmbeddedCacheManager cacheManager) {

        this.defaultCacheManager = cacheManager ;
    }

    @Override
    protected Cache createCache(String name) throws CacheException {
        return new InfinispanCache(name,defaultCacheManager.getCache(name));
    }
}
