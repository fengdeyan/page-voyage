package me.yan.constant;

public class WebConst {
    public final static String LOGIN_SESSION_KEY="login_user";
    public final static int HITS_EXCEED=2;
    // 热点文章缓存Key
    public static final String HOT_ARTICLE_LIST_KEY = "blog:hot:article:list";
    // 缓存过期时间：10分钟，平衡实时性与数据库压力
    public static final int CACHE_EXPIRE_MIN = 10;
}
