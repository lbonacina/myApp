package myApp.security;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Luigi
 * Date: 01/02/13
 * Time: 20.46
 * Implementation of a Shiro Cache with an infinispan Cache
 */
public class InfinispanCache<K, V> implements Cache<K, V> {

    Logger log = LoggerFactory.getLogger(getClass());

    private String cacheName  ;
    private org.infinispan.Cache<K, V> cache;

    public InfinispanCache(String name, org.infinispan.Cache<K,V> cache) {
        cacheName = name ;
        this.cache = cache ;
    }

    @Override
    public V get(K k) throws CacheException {
        V v = cache.get(k) ;
        log.trace("Retrieved value : {} for key : {} on cache : " + cacheName, v, k );
        return v ;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        log.trace("Putting value : {} for key : {} on cache : " + cacheName, v, k );
        cache.put(k,v);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        return cache.remove(k) ;
    }

    @Override
    public void clear() throws CacheException {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public Set<K> keys() {
        return cache.keySet() ;
    }

    @Override
    public Collection<V> values() {
        return cache.values() ;
    }
}
