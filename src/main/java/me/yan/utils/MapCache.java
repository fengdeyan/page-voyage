package me.yan.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache {

    //默认储存容量
    private static final int DEFAULT_CACHE=1024;

    private static final MapCache instance = new MapCache(DEFAULT_CACHE);

    private Map<String , CacheObject> cachePool;

    public static MapCache single(){
        return instance;
    };

    private MapCache(int cacheSize){
        cachePool = new ConcurrentHashMap<>(cacheSize);
    }

    public <T> void hset(String key,String field,T value,long expireTime){
        key=key+":"+field;
        expireTime=expireTime>0?expireTime+System.currentTimeMillis()/1000:expireTime;
        cachePool.put(key,new CacheObject<T>(value,expireTime));
    }

    public Object hget(String key,String field){
        key=key+":"+field;
        CacheObject cacheObject = cachePool.get(key);
        if (cacheObject==null){
            return null;
        }
        long cur=System.currentTimeMillis()/1000;
        if(cacheObject.expired>=0&&cacheObject.expired<=cur)
            return null;
        return cacheObject.getValue();
    }

    class CacheObject<E>{
        private E value;
        private long expired;

        public CacheObject(E value, long expired) {
            this.value = value;
            this.expired = expired;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }
    }
}
