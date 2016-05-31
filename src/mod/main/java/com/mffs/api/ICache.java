package com.mffs.api;

/**
 * @author Calclavia
 */
public interface ICache {
    Object getCache(String paramString);

    void clearCache(String paramString);

    void clearCache();
}