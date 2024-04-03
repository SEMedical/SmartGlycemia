package edu.tongji.backend.util;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY="login:code:";
    public static final String LOGIN_TOKEN_KEY="login:token:";
    public static final Integer LOGIN_TOKEN_TTL=30;//Unit:Minute
    public static final Integer LOGIN_CODE_TIMEOUT=1;
    public static final Long CACHE_NULL_TTL=2L;
    public static final String CACHE_GLYCEMIA_KEY="cache:glycemia:";
    public static final String CACHE_LATEST_GLYCEMIA_KEY ="latest:glycemia_id:";
    public static final Long LATEST_GLYCEMIA_TTL=2L;
    public static final Long EXERCISE_RUNNING_TTL=1L;
    public static final String EXERCISE_RUNNING_KEY="exercise:running";
    public static final Long CACHE_GLYCEMIA_TTL=7L;
    public static final String CACHE_DAILY_GLYCEMIA_KEY="cache:daily:glycemia:";
    public static final Long CACHE_DAILY_GLYCEMIA_TTL= 1L;
    public static final String CACHE_HISTORY_GLYCEMIA_KEY="cache:history:glycemia:";
    public static final Long CACHE_HISTORY_GLYCEMIA_TTL=7L;
    public static final String HOSPITAL_GEO_KEY="geo:hospital:";
    public static final String USER_SIGN_KEY="sign:";
}
