package dev.su5ed.mffs.api;

/**
 * For objects that uses caching method to speed up process power.
 *
 * @author Calclavia
 */
public interface ObjectCache {
    Object getCache(String cacheID);
    
    void putCache(String cacheID, Object obj);

    void clearCache(String cacheID);

    void clearCache();
}
